"""
This module defines the API routes for the PurpleCloud service, including OAuth 2.0 endpoints.
"""

from typing import Annotated, Optional
from urllib.parse import urlencode

import fastapi
from fastapi import Form, Query, Request, responses
from starlette import status

from purplecloud import auth
from purplecloud.storage import storage_provider

router = fastapi.APIRouter()


def _get_login_form(
    client_id,
    redirect_uri,
    response_type,
    state,
    code_challenge,
    code_challenge_method,
    error="",
):
    """Returns an HTML login form."""
    client_name = auth.REGISTERED_CLIENTS.get(client_id, {}).get(
        "client_name", "the application"
    )
    error_html = f"<p style='color:red;'>{error}</p>" if error else ""
    return f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>Login to PurpleCloud</title>
        <style>
            body {{ font-family: sans-serif; }}
            form {{ border: 1px solid #ccc; padding: 20px; border-radius: 5px; }}
            input {{ width: 100%; padding: 8px; margin-bottom: 10px; box-sizing: border-box; }}
        </style>
    </head>
    <body>
        <h2>Login to authorize {client_name}</h2>
        <form method="post">
            <input type="hidden" name="client_id" value="{client_id}"/>
            <input type="hidden" name="redirect_uri" value="{redirect_uri}"/>
            <input type="hidden" name="response_type" value="{response_type}"/>
            <input type="hidden" name="state" value="{state}"/>
            <input type="hidden" name="code_challenge" value="{code_challenge}"/>
            <input type="hidden" name="code_challenge_method" value="{code_challenge_method}"/>
            <label for="username">Username:</label><br>
            <input type="text" id="username" name="username" required><br>
            <label for="password">Password:</label><br>
            <input type="password" id="password" name="password" required><br><br>
            <input type="submit" value="Login and Authorize">
        </form>
        {error_html}
    </body>
    </html>
    """


@router.get("/")
async def root():
    return {"message": "PurpleCloud is running"}


@router.api_route("/authorize", methods=["GET", "POST"])
async def authorize(
    request: Request,
    client_id: str = Query(None),
    redirect_uri: str = Query(None),
    response_type: str = Query(None),
    state: str = Query(None),
    code_challenge: str = Query(None),
    code_challenge_method: str = Query(None),
):
    """
    OAuth 2.0 Authorization Endpoint with PKCE.
    Presents a login form and, upon successful authentication, redirects to the
    client's redirect_uri with an authorization code.
    """
    # On POST, we get form data instead of query params
    if request.method == "POST":
        form_data = await request.form()
        client_id = form_data.get("client_id")
        redirect_uri = form_data.get("redirect_uri")
        response_type = form_data.get("response_type")
        state = form_data.get("state")
        code_challenge = form_data.get("code_challenge")
        code_challenge_method = form_data.get("code_challenge_method")
        username = form_data.get("username")
        password = form_data.get("password")

        user = auth.authenticate_user(auth.fake_users_db, username, password)
        if not user:
            html_form = _get_login_form(
                client_id,
                redirect_uri,
                response_type,
                state,
                code_challenge,
                code_challenge_method,
                "Invalid credentials",
            )
            return responses.HTMLResponse(content=html_form, status_code=401)

        code = auth.create_authorization_code(
            username=user.username,
            client_id=client_id,
            code_challenge=code_challenge,
            code_challenge_method=code_challenge_method,
        )
        params = {"code": code, "state": state}
        return responses.RedirectResponse(f"{redirect_uri}?{urlencode(params)}")

    # On GET, validate params and show login form
    if not all(
        [
            client_id,
            redirect_uri,
            response_type,
            state,
            code_challenge,
            code_challenge_method,
        ]
    ):
        raise fastapi.HTTPException(400, "Missing required authorization parameters.")
    if client_id not in auth.REGISTERED_CLIENTS:
        raise fastapi.HTTPException(400, "Invalid client_id.")
    if auth.REGISTERED_CLIENTS[client_id]["redirect_uri"] != redirect_uri:
        raise fastapi.HTTPException(400, "Mismatched redirect_uri.")
    if response_type != "code":
        raise fastapi.HTTPException(400, "Unsupported response_type.")
    if code_challenge_method not in ["S256", "plain"]:
        raise fastapi.HTTPException(400, "Unsupported code_challenge_method.")

    html_form = _get_login_form(
        client_id,
        redirect_uri,
        response_type,
        state,
        code_challenge,
        code_challenge_method,
    )
    return responses.HTMLResponse(content=html_form)


@router.post("/token", response_model=auth.OAuthToken)
async def token(
    grant_type: str = Form(...),
    code: Optional[str] = Form(None),
    redirect_uri: Optional[str] = Form(None),
    client_id: Optional[str] = Form(None),
    refresh_token: Optional[str] = Form(None),
    code_verifier: Optional[str] = Form(None),
):
    """OAuth 2.0 Token Endpoint."""
    if grant_type == "authorization_code":
        if not all([code, redirect_uri, client_id, code_verifier]):
            raise fastapi.HTTPException(400, "Missing parameters for authorization_code grant.")
        tokens = auth.exchange_code_for_tokens(code, client_id, redirect_uri, code_verifier)
        if not tokens:
            raise fastapi.HTTPException(401, "Invalid authorization code or client details.")
        access_token, refresh_token = tokens
        return auth.OAuthToken(access_token=access_token, refresh_token=refresh_token)

    if grant_type == "refresh_token":
        if not refresh_token:
            raise fastapi.HTTPException(400, "Missing refresh_token.")
        new_access_token = auth.exchange_refresh_token(refresh_token)
        if not new_access_token:
            raise fastapi.HTTPException(401, "Invalid or revoked refresh token.")
        # Note: A new refresh token is not issued here for simplicity.
        return auth.OAuthToken(access_token=new_access_token, refresh_token=refresh_token)

    raise fastapi.HTTPException(400, "Unsupported grant_type.")


@router.post("/revoke")
async def revoke(token: str = Form(...)):
    """Revokes an access or refresh token."""
    auth.revoke_token(token)
    return {"message": "Token successfully revoked."}


@router.get("/userinfo", response_model=auth.User)
async def userinfo(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
):
    """Returns information about the currently authenticated user."""
    return current_user


@router.post("/upload")
async def upload_file(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
    logicalPath: str = fastapi.Form(...),
    file: fastapi.UploadFile = fastapi.File(...),
) -> dict[str, str]:
    """Uploads a file to a specified logical path. Requires authentication."""
    await storage_provider.save_file(
        username=current_user.username, logical_path=logicalPath, file_data=file
    )
    return {"message": f"File '{file.filename}' uploaded to '{logicalPath}'."}


@router.get("/users/me/", response_model=auth.User)
async def read_users_me(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
) -> auth.User:
    """Returns the details of the currently authenticated user."""
    return current_user


@router.get("/items/{logical_path:path}", response_class=responses.Response)
async def get_item(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
    logical_path: str,
):
    """
    Downloads a file or lists a directory's contents.
    - To download a file: `/items/path/to/file.txt`
    - To list a directory: `/items/path/to/dir/`
    - To list the root directory: `/items/`
    """
    return await storage_provider.get_item(
        username=current_user.username, logical_path=logical_path
    )


@router.delete("/items/{logical_path:path}", status_code=status.HTTP_200_OK)
async def delete_item(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
    logical_path: str,
) -> dict[str, str]:
    """
    Deletes a file or directory.
    - To delete a file: `/items/path/to/file.txt`
    - To delete a directory: `/items/path/to/dir/`
    """
    await storage_provider.delete_item(
        username=current_user.username, logical_path=logical_path
    )
    return {"message": f"Item '{logical_path}' deleted successfully."}
