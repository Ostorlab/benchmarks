"""
This module handles user authentication for the PurpleCloud service.

It includes functions for:
- Password verification and hashing.
- User retrieval and authentication.
- JWT access token creation.
- Dependency injectors for getting the current user from a token.
"""

from datetime import datetime, timedelta, timezone
from typing import Annotated, cast

import fastapi
from fastapi.security import OAuth2PasswordBearer
import jose
from jose import jwt
from passlib import context as crypt_context
import pydantic
from starlette import status

SECRET_KEY = "a_very_secret_key_that_should_be_in_env_vars"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60


class Token(pydantic.BaseModel):
    """Token model for API responses."""

    access_token: str
    token_type: str


class TokenData(pydantic.BaseModel):
    """Data model for token payload."""

    username: str | None = None


class User(pydantic.BaseModel):
    """User model for API requests and responses."""

    username: str
    email: str | None = None
    full_name: str | None = None
    disabled: bool | None = None


class UserInDB(User):
    """User model as stored in the database, including hashed password."""

    hashed_password: str


pwd_context = crypt_context.CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

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
    """
    Verify a plain password against a hashed password.

    Args:
        plain_password: The plain text password.
        hashed_password: The hashed password.

    Returns:
        True if the password is correct, False otherwise.
    """
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password: str) -> str:
    """
    Hash a plain password.

    Args:
        password: The plain text password.

    Returns:
        The hashed password.
    """
    return pwd_context.hash(password)


def get_user(db, username: str) -> UserInDB | None:
    """
    Retrieve a user from the database.

    Args:
        db: The database of users.
        username: The username to retrieve.

    Returns:
        The user if found, otherwise None.
    """
    if username in db:
        user_dict = db[username]
        return UserInDB(**user_dict)
    return None


def authenticate_user(db, username: str, password: str) -> User | None:
    """
    Authenticate a user.

    Args:
        db: The database of users.
        username: The username to authenticate.
        password: The password to check.

    Returns:
        The authenticated user, or None if authentication fails.
    """
    user = get_user(db, username)
    if user is None or verify_password(password, user.hashed_password) is False:
        return None
    return user


def create_access_token(data: dict, expires_delta: timedelta | None = None) -> str:
    """
    Create a new access token.

    Args:
        data: The data to encode in the token.
        expires_delta: The expiration delta for the token.

    Returns:
        The encoded JWT access token.
    """
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jose.jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


async def get_current_user(
    token: Annotated[str, fastapi.Depends(oauth2_scheme)],
) -> User | None:
    """
    Get the current user from a JWT token.

    Args:
        token: The OAuth2 token.

    Returns:
        The current user, or raises HTTPException if authentication fails.
    """
    credentials_exception = fastapi.HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = cast(str, payload.get("sub"))
        if username is None:
            raise credentials_exception
        token_data = TokenData(username=username)
    except jose.JWTError:
        raise credentials_exception
    user = get_user(fake_users_db, username=cast(str, token_data.username))
    if user is None:
        raise credentials_exception
    return user


async def get_current_active_user(
    current_user: Annotated[User, fastapi.Depends(get_current_user)],
) -> User:
    """
    Get the current active user.

    Args:
        current_user: The current user, injected by dependency.

    Returns:
        The current user if active, otherwise raises HTTPException.
    """
    if current_user.disabled:
        raise fastapi.HTTPException(status_code=400, detail="Inactive user")
    return current_user
