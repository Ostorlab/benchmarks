"""
This module provides a local file system storage implementation for PurpleCloud.

It handles file and directory operations such as saving, retrieving, and deleting
items within a user-specific storage space, ensuring path safety.
"""

import os
from pathlib import Path
import shutil

import fastapi
from fastapi import responses
from starlette import status


class LocalFileSystemStorage:
    """
    Manages storage of files and directories on the local file system.

    This class abstracts file operations and ensures that all paths are
    sandboxed within a user-specific directory to prevent security vulnerabilities
    like path traversal.
    """

    def __init__(self, base_directory="purplecloud_data"):
        """
        Initializes the storage provider.

        Args:
            base_directory (str): The root directory for all user data.
        """
        self.base_path = Path(base_directory).resolve()

    def setup(self) -> None:
        """Create the base directory if it doesn't exist."""
        os.makedirs(self.base_path, exist_ok=True)

    def _get_sanitized_path(self, username: str, logical_path: str) -> Path:
        """Resolves and validates a logical path against the user's storage."""
        if ".." in Path(logical_path).parts or Path(logical_path).is_absolute():
            raise fastapi.HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid path."
            )

        user_storage_path = self.base_path.joinpath(username)
        destination = user_storage_path.joinpath(logical_path)

        if not str(destination.resolve()).startswith(str(user_storage_path.resolve())):
            raise fastapi.HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Path traversal attempt detected.",
            )
        return destination

    async def save_file(
        self, username: str, logical_path: str, file_data: fastapi.UploadFile
    ) -> None:
        """Saves a file to the specified logical path for a given user."""
        destination = self._get_sanitized_path(username, logical_path)

        destination.parent.mkdir(parents=True, exist_ok=True)

        try:
            with open(destination, "wb") as buffer:
                while content := await file_data.read(1024 * 1024):  # 1MB chunks
                    buffer.write(content)
        finally:
            await file_data.close()

    async def get_item(
        self, username: str, logical_path: str
    ) -> responses.Response | None:
        """
        Retrieves a file or lists a directory's contents.
        Returns a FileResponse for files, or a JSONResponse for directories.
        """
        full_path = self._get_sanitized_path(username, logical_path)

        if not full_path.exists():
            raise fastapi.HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, detail="Item not found"
            )

        if full_path.is_file():
            return responses.FileResponse(path=full_path, filename=full_path.name)

        if full_path.is_dir():
            user_storage_path = self.base_path.joinpath(username)
            items = []
            for item in sorted(list(full_path.iterdir())):
                items.append(
                    {
                        "name": item.name,
                        "path": str(item.relative_to(user_storage_path)),
                        "type": "directory" if item.is_dir() else "file",
                    }
                )
            return responses.JSONResponse(content=items)
        return None

    async def delete_item(self, username: str, logical_path: str) -> None:
        """Deletes a file or a directory at the specified logical path."""
        full_path = self._get_sanitized_path(username, logical_path)

        if not full_path.exists():
            raise fastapi.HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, detail="Item not found"
            )

        try:
            if full_path.is_file():
                full_path.unlink()
            elif full_path.is_dir():
                shutil.rmtree(full_path)
        except OSError as e:
            raise fastapi.HTTPException(
                status_code=500, detail=f"Could not delete item: {e}"
            )


storage_provider = LocalFileSystemStorage()
