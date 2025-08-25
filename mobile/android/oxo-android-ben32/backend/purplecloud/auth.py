"""
This module handles user authentication for the PurpleCloud service.

It includes functions for:
- Password verification.
- User retrieval and authentication.
- Simple, file-based token generation and validation (access, refresh).
- Dependency injectors for getting the current user from a token.
"""

import json
import secrets
import threading
import time
from pathlib import Path
from typing import Annotated, Any

import fastapi
from fastapi.security import OAuth2PasswordBearer
import pydantic
from passlib import context as crypt_context
from starlette import status

# --- Token Configuration ---
TOKEN_DIR = Path("tokens")
ACCESS_TOKENS_FILE = TOKEN_DIR / "access.json"
REFRESH_TOKENS_FILE = TOKEN_DIR / "refresh.json"
REVOKED_TOKENS_FILE = TOKEN_DIR / "revoked.json"
ACCESS_TOKEN_EXPIRE_MINUTES = 5
REFRESH_TOKEN_EXPIRE_DAYS = 1

# Thread lock for safe file operations
_lock = threading.Lock()


def setup_token_storage():
    """Create token storage directory and files if they don't exist."""
    with _lock:
        TOKEN_DIR.mkdir(exist_ok=True)
        for file_path in [ACCESS_TOKENS_FILE, REFRESH_TOKENS_FILE, REVOKED_TOKENS_FILE]:
            if not file_path.exists():
                if "revoked" in file_path.name:
                    file_path.write_text("[]")  # JSON array for revoked tokens
                else:
                    file_path.write_text("{}")  # JSON object for other tokens


def _read_tokens(file_path: Path) -> dict[str, Any] | list[str]:
    """Safely read and clean expired tokens from a file."""
    with _lock:
        if not file_path.exists():
            return {} if "revoked" not in file_path.name else []

        with file_path.open("r") as f:
            try:
                tokens = json.load(f)
            except json.JSONDecodeError:
                return {} if "revoked" not in file_path.name else []

        if isinstance(tokens, dict):
            now = time.time()
            # Filter out expired tokens
            active_tokens = {
                token: data for token, data in tokens.items() if data["expiry"] > now
            }
            if len(active_tokens) < len(tokens):
                # Write back the cleaned tokens
                with file_path.open("w") as f:
                    json.dump(active_tokens, f)
            return active_tokens
        return tokens  # For revoked_tokens.json which is a list


def _write_tokens(file_path: Path, tokens: dict[str, Any] | list[str]) -> None:
    """Safely write tokens to a file."""
    with _lock:
        with file_path.open("w") as f:
            json.dump(tokens, f, indent=2)


def _add_token(file_path: Path, token: str, data: dict[str, Any]) -> None:
    """Safely add a single token to a file."""
    tokens = _read_tokens(file_path)
    if isinstance(tokens, dict):
        tokens[token] = data
        _write_tokens(file_path, tokens)


def revoke_token(token: str) -> None:
    """Revoke a token by adding it to the revoked list."""
    with _lock:
        if not REVOKED_TOKENS_FILE.exists():
            revoked_list = []
        else:
            with REVOKED_TOKENS_FILE.open("r") as f:
                try:
                    revoked_list = json.load(f)
                except json.JSONDecodeError:
                    revoked_list = []

        if token not in revoked_list:
            revoked_list.append(token)
            with REVOKED_TOKENS_FILE.open("w") as f:
                json.dump(revoked_list, f, indent=2)


# --- Pydantic Models ---
class Token(pydantic.BaseModel):
    """Token model for API responses."""

    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class User(pydantic.BaseModel):
    """User model for API requests and responses."""

    username: str
    email: str | None = None
    full_name: str | None = None
    disabled: bool | None = None


class UserInDB(User):
    """User model as stored in the database, including hashed password."""

    hashed_password: str


# --- Authentication Dependencies and Data ---
pwd_context = crypt_context.CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/token")

fake_users_db = {
    "testuser@example.com": {
        "username": "testuser@example.com",
        "full_name": "Test User",
        "email": "testuser@example.com",
        "hashed_password": pwd_context.hash("secretpassword"),
        "disabled": False,
    }
}


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify a plain password against a hashed password."""
    return pwd_context.verify(plain_password, hashed_password)


def get_user(db, username: str) -> UserInDB | None:
    """Retrieve a user from the database."""
    if username in db:
        user_dict = db[username]
        return UserInDB(**user_dict)
    return None


def authenticate_user(db, username: str, password: str) -> User | None:
    """Authenticate a user."""
    user = get_user(db, username)
    if user is None or not verify_password(password, user.hashed_password):
        return None
    return user


def create_auth_tokens(username: str) -> tuple[str, str]:
    """Create and store new access and refresh tokens."""
    access_token = secrets.token_hex(32)
    refresh_token = secrets.token_hex(32)

    access_token_expiry = time.time() + (ACCESS_TOKEN_EXPIRE_MINUTES * 60)
    refresh_token_expiry = time.time() + (REFRESH_TOKEN_EXPIRE_DAYS * 24 * 60 * 60)

    _add_token(
        ACCESS_TOKENS_FILE, access_token, {"user": username, "expiry": access_token_expiry}
    )
    _add_token(
        REFRESH_TOKENS_FILE,
        refresh_token,
        {"user": username, "expiry": refresh_token_expiry},
    )

    return access_token, refresh_token


def refresh_access_token(refresh_token: str) -> str | None:
    """Validate a refresh token and issue a new access token."""
    refresh_tokens = _read_tokens(REFRESH_TOKENS_FILE)
    revoked_tokens = _read_tokens(REVOKED_TOKENS_FILE)

    if not isinstance(refresh_tokens, dict) or not isinstance(revoked_tokens, list):
        return None

    if refresh_token in revoked_tokens or refresh_token not in refresh_tokens:
        return None

    token_data = refresh_tokens[refresh_token]
    username = token_data["user"]

    # Issue a new access token
    new_access_token = secrets.token_hex(32)
    access_token_expiry = time.time() + (ACCESS_TOKEN_EXPIRE_MINUTES * 60)
    _add_token(
        ACCESS_TOKENS_FILE,
        new_access_token,
        {"user": username, "expiry": access_token_expiry},
    )

    return new_access_token


async def get_current_user(
    token: Annotated[str, fastapi.Depends(oauth2_scheme)],
) -> User:
    """
    Get the current user from a token. This is the dependency for protected routes.
    """
    credentials_exception = fastapi.HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )

    access_tokens = _read_tokens(ACCESS_TOKENS_FILE)
    revoked_tokens = _read_tokens(REVOKED_TOKENS_FILE)

    if not isinstance(access_tokens, dict) or not isinstance(revoked_tokens, list):
        print("Cloud not read stored tokens.")
        raise credentials_exception

    if token in revoked_tokens or token not in access_tokens:
        print("Token is revoked.")
        raise credentials_exception

    username = access_tokens[token]["user"]
    user = get_user(fake_users_db, username=username)
    if user is None:
        raise credentials_exception
    return user


async def get_current_active_user(
    current_user: Annotated[User, fastapi.Depends(get_current_user)],
) -> User:
    """Get the current active user."""
    if current_user.disabled:
        raise fastapi.HTTPException(status_code=400, detail="Inactive user")
    return current_user


# Initialize token storage on startup
setup_token_storage()
