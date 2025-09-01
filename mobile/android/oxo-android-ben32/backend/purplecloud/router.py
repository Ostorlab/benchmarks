"""
This module defines the API routes for the PurpleCloud service.

It includes routes for:
- User authentication (token generation)
- File and directory operations (upload, download, delete)
- User information retrieval
"""

from typing import Annotated, Optional

import fastapi
import pydantic
from fastapi import Body, responses, security
from starlette import status

from purplecloud import auth
from purplecloud.storage import storage_provider

router = fastapi.APIRouter()


class RefreshTokenRequest(pydantic.BaseModel):
    refresh_token: str


class BuggyLogoutRequest(pydantic.BaseModel):
    refresh_token: str
    logout_hint: Optional[str] = None


@router.get("/")
async def root():
    return {"message": "PurpleCloud is running"}


@router.post("/auth/token", response_model=auth.Token)
async def login_for_access_token(
    form_data: Annotated[security.OAuth2PasswordRequestForm, fastapi.Depends()],
) -> auth.Token:
    """
    Authenticates a user and returns an access and refresh token.
    """
    user = auth.authenticate_user(
        auth.fake_users_db, form_data.username, form_data.password
    )
    if not user:
        raise fastapi.HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token, refresh_token = auth.create_auth_tokens(username=user.username)
    return auth.Token(access_token=access_token, refresh_token=refresh_token)


@router.post("/auth/refresh")
async def refresh_token(body: RefreshTokenRequest) -> dict[str, str]:
    """
    Refreshes an access token using a valid refresh token.
    """
    new_access_token = auth.refresh_access_token(body.refresh_token)
    if not new_access_token:
        raise fastapi.HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired refresh token",
        )
    return {"access_token": new_access_token, "token_type": "bearer"}


@router.post("/auth/logout")
async def logout(
    token: Annotated[str, fastapi.Depends(auth.oauth2_scheme)],
    body: BuggyLogoutRequest,
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
) -> dict[str, str]:
    """
    Revoke the tokens.
    """
    auth.revoke_token(body.refresh_token)
    if body.logout_hint:
        auth.revoke_token(token)
        return {"message": "Successfully logged out"}
    # Vulnerability: If the hint is missing the access_token is not revoked.
    raise fastapi.HTTPException(
        status_code=status.HTTP_400_BAD_REQUEST,
        detail="Logout hint missing.",
    )


@router.post("/upload")
async def upload_file(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
    logicalPath: str = fastapi.Form(...),
    file: fastapi.UploadFile = fastapi.File(...),
) -> dict[str, str]:
    """
    Uploads a file to a specified logical path. Requires authentication.
    """
    await storage_provider.save_file(
        username=current_user.username, logical_path=logicalPath, file_data=file
    )
    return {"message": f"File '{file.filename}' uploaded to '{logicalPath}'."}


@router.get("/users/me/", response_model=auth.User)
async def read_users_me(
    current_user: Annotated[auth.User, fastapi.Depends(auth.get_current_active_user)],
) -> auth.User:
    """
    Returns the details of the currently authenticated user.
    """
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
