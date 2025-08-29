"""
This module handles user authentication for the PurpleCloud service using OAuth 2.0.

It includes functions for:
- OAuth 2.0 Authorization Code Flow with PKCE.
- User authentication.
- Token and authorization code management (generation, validation, revocation).
- Dependency injectors for getting the current user from a token.
"""

import base64
import hashlib
import json
import random
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

# --- Configuration ---
TOKEN_DIR = Path("tokens")
ACCESS_TOKENS_FILE = TOKEN_DIR / "access.json"
REFRESH_TOKENS_FILE = TOKEN_DIR / "refresh.json"
REVOKED_TOKENS_FILE = TOKEN_DIR / "revoked.json"
AUTH_CODES_FILE = TOKEN_DIR / "auth_codes.json"

ACCESS_TOKEN_EXPIRE_MINUTES = 15
REFRESH_TOKEN_EXPIRE_DAYS = 7
AUTH_CODE_EXPIRE_SECONDS = 600  # 10 minutes

REGISTERED_CLIENTS = {
    "purplecloud-client": {
        "redirect_uri": "com.purpleapps.purplecloud://oauth2/callback",
        "client_name": "PurpleCloud Mobile App",
    }
}

# Thread lock for safe file operations
_lock = threading.Lock()
_2fa_codes: dict[str, str] = {}


def setup_token_storage():
    """Create token storage directory and files if they don't exist."""
    with _lock:
        TOKEN_DIR.mkdir(exist_ok=True)
        for file_path in [
            ACCESS_TOKENS_FILE,
            REFRESH_TOKENS_FILE,
            AUTH_CODES_FILE,
        ]:
            if not file_path.exists():
                file_path.write_text("{}")  # JSON object for tokens/codes
        if not REVOKED_TOKENS_FILE.exists():
            REVOKED_TOKENS_FILE.write_text("[]")  # JSON array for revoked tokens


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
class OAuthToken(pydantic.BaseModel):
    """Token model for the /token endpoint response."""

    access_token: str
    refresh_token: str
    token_type: str = "bearer"
    expires_in: int = ACCESS_TOKEN_EXPIRE_MINUTES * 60


class User(pydantic.BaseModel):
    """User model for API requests and responses."""

    username: str
    email: str | None = None
    full_name: str | None = None
    disabled: bool | None = None


class UserInDB(User):
    """User model as stored in the database, including hashed password."""

    hashed_password: str
    two_factor_enabled: bool = False


# --- Authentication Dependencies and Data ---
pwd_context = crypt_context.CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

fake_users_db = {
    "testuser@example.com": {
        "username": "testuser@example.com",
        "full_name": "Test User",
        "email": "testuser@example.com",
        "hashed_password": pwd_context.hash("secretpassword"),
        "disabled": False,
        "two_factor_enabled": True,
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


def create_authorization_code(
    username: str, client_id: str, code_challenge: str, code_challenge_method: str
) -> str | None:
    """Create and store a new authorization code with PKCE details."""
    code = secrets.token_urlsafe(32)
    expiry = time.time() + AUTH_CODE_EXPIRE_SECONDS
    auth_codes = _read_tokens(AUTH_CODES_FILE)
    if isinstance(auth_codes, dict):
        auth_codes[code] = {
            "username": username,
            "client_id": client_id,
            "expiry": expiry,
            "code_challenge": code_challenge,
            "code_challenge_method": code_challenge_method,
        }
        _write_tokens(AUTH_CODES_FILE, auth_codes)
        return code
    return None


def exchange_code_for_tokens(
    code: str, client_id: str, redirect_uri: str, code_verifier: str
) -> tuple[str, str] | None:
    """Validate an authorization code with PKCE and exchange it for tokens."""
    auth_codes = _read_tokens(AUTH_CODES_FILE)
    if not isinstance(auth_codes, dict) or code not in auth_codes:
        return None

    code_data = auth_codes[code]

    # Verify client_id and that the client is registered
    if code_data["client_id"] != client_id or client_id not in REGISTERED_CLIENTS:
        return None
    # Verify redirect_uri matches the registered one
    if REGISTERED_CLIENTS[client_id]["redirect_uri"] != redirect_uri:
        return None

    # --- PKCE Verification ---
    if "code_challenge" not in code_data:
        return None  # PKCE is required

    code_challenge = code_data["code_challenge"]
    code_challenge_method = code_data.get("code_challenge_method", "plain")

    if code_challenge_method == "S256":
        hashed_verifier = hashlib.sha256(code_verifier.encode("ascii")).digest()
        recreated_challenge = base64.urlsafe_b64encode(hashed_verifier).decode("ascii").rstrip("=")
    elif code_challenge_method == "plain":
        recreated_challenge = code_verifier
    else:
        return None  # Unsupported method

    if not secrets.compare_digest(recreated_challenge, code_challenge):
        return None  # PKCE verification failed

    # Consume the code by deleting it
    del auth_codes[code]
    _write_tokens(AUTH_CODES_FILE, auth_codes)

    # Issue new tokens
    return create_auth_tokens(username=code_data["username"])


def exchange_refresh_token(refresh_token: str) -> str | None:
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
        raise credentials_exception

    if token in revoked_tokens or token not in access_tokens:
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


def generate_2fa_code(username: str) -> str:
    """
    Generates a weak 4-digit 2FA code and stores it.
    VULNERABILITY: The code is only 4 digits, making it easy to brute-force.
    """
    with _lock:
        code = str(random.randint(1000, 9999)).zfill(4)
        _2fa_codes[username] = code
        print(f"2FA code for {username}: {code}")
        return code


def verify_2fa_code(username: str, code: str) -> bool:
    """
    Verifies the 2FA code.
    VULNERABILITY: No rate limiting or expiration, allowing for unlimited guesses.
    """
    with _lock:
        correct_code = _2fa_codes.get(username)
        return correct_code is not None and correct_code == code


def clear_2fa_code(username: str) -> None:
    """Clears the 2FA code for a user after it has been used."""
    with _lock:
        if username in _2fa_codes:
            del _2fa_codes[username]


# Initialize token storage on startup
setup_token_storage()
