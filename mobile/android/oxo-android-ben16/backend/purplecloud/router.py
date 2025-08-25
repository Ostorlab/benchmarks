"""
This module defines the API routes for the PurpleCloud service.

It includes routes for:
- User authentication (token generation)
- File and directory operations (upload, download, delete)
- User information retrieval
"""

from datetime import timedelta
from typing import Annotated


import fastapi
from fastapi import responses
from fastapi import security
from starlette import status

from purplecloud import auth
from purplecloud.storage import storage_provider

router = fastapi.APIRouter()


@router.get("/")
async def root():
    return {"message": "PurpleCloud is running"}


@router.post("/token", response_model=auth.Token)
async def login_for_access_token(
    form_data: Annotated[security.OAuth2PasswordRequestForm, fastapi.Depends()],
) -> dict[str, str]:
    """
    Authenticates a user and returns an access token.
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
    access_token_expires = timedelta(minutes=auth.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = auth.create_access_token(
        data={"sub": user.username}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}


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
