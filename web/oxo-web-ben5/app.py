import base64
import hashlib
import hmac
import os
import pickle
import secrets
import time
from typing import Any

from flask import Flask
from flask import jsonify
from flask import request


app = Flask(__name__)
app.config["SECRET_KEY"] = os.environ.get(
    "SECRET_KEY", "dev-secret-key-change-in-production"
)

HMAC_KEY = os.environ.get("PROFILE_STATE_HMAC_KEY", "benchmark-hmac-key").encode()

USERS: dict[str, dict[str, Any]] = {}
SESSIONS: dict[str, str] = {}


def _urlsafe_b64encode(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).decode().rstrip("=")


def _urlsafe_b64decode(value: str) -> bytes:
    padding = "=" * (-len(value) % 4)
    return base64.urlsafe_b64decode(value + padding)


def _make_profile_state(user_id: str, display_name: str, role: str = "user") -> str:
    state = {
        "user_id": user_id,
        "display_name": display_name,
        "role": role,
        "imported_at": int(time.time()),
    }
    return _urlsafe_b64encode(pickle.dumps(state, protocol=4))


def _sign_blob(blob: str) -> str:
    signature = hmac.new(HMAC_KEY, blob.encode(), hashlib.sha256).hexdigest()
    return f"{blob}.{signature}"


def _verify_signed_blob(value: str) -> str | None:
    try:
        blob, signature = value.rsplit(".", 1)
    except ValueError:
        return None

    expected = hmac.new(HMAC_KEY, blob.encode(), hashlib.sha256).hexdigest()
    if hmac.compare_digest(expected, signature) is False:
        return None
    return blob


def _current_user() -> dict[str, Any] | None:
    auth_header = request.headers.get("Authorization", "")
    if auth_header.startswith("Bearer ") is False:
        return None
    token = auth_header.removeprefix("Bearer ").strip()
    username = SESSIONS.get(token)
    if username is None:
        return None
    return USERS.get(username)


def _require_user() -> tuple[dict[str, Any] | None, Any | None]:
    user = _current_user()
    if user is None:
        return None, (jsonify({"error": "missing or invalid bearer token"}), 401)
    return user, None


@app.get("/")
def index():
    return jsonify(
        {
            "app": "oxo-web-ben5",
            "name": "Insecure Deserialization Benchmark",
            "primary_request": "POST /api/import",
            "selected_field": "profile_state",
        }
    )


@app.post("/api/register")
def register():
    body = request.get_json(force=True, silent=True) or {}
    username = str(body.get("username", "")).strip()
    password = str(body.get("password", "")).strip()

    if username == "" or password == "":
        return jsonify({"error": "username and password are required"}), 400
    if username in USERS:
        return jsonify({"error": "user already exists"}), 409

    user = {
        "id": username,
        "username": username,
        "password": password,
        "display_name": username,
        "role": "user",
        "tenant_id": "tenant-default",
        "imports": [],
    }
    USERS[username] = user
    return jsonify({"id": user["id"], "username": username, "role": user["role"]}), 201


@app.post("/api/login")
def login():
    body = request.get_json(force=True, silent=True) or {}
    username = str(body.get("username", "")).strip()
    password = str(body.get("password", "")).strip()
    user = USERS.get(username)

    if user is None or user["password"] != password:
        return jsonify({"error": "invalid credentials"}), 401

    token = secrets.token_urlsafe(24)
    SESSIONS[token] = username
    return jsonify({"access_token": token, "token_type": "Bearer", "user_id": username})


@app.post("/api/logout")
def logout():
    auth_header = request.headers.get("Authorization", "")
    token = auth_header.removeprefix("Bearer ").strip()
    if token in SESSIONS:
        del SESSIONS[token]
    return jsonify({"status": "logged out"})


@app.get("/api/profile-state")
def get_profile_state():
    user, error = _require_user()
    if error is not None:
        return error

    blob = _make_profile_state(user["id"], user["display_name"], user["role"])
    return jsonify(
        {
            "profile_state": blob,
            "protected_profile_state": _sign_blob(blob),
            "format": "urlsafe-base64-pickle",
        }
    )


@app.post("/api/import")
def import_profile_state():
    user, error = _require_user()
    if error is not None:
        return error

    body = request.get_json(force=True, silent=True) or {}
    profile_state = body.get("profile_state")
    if not isinstance(profile_state, str) or profile_state == "":
        return jsonify({"error": "profile_state is required"}), 400

    try:
        decoded = _urlsafe_b64decode(profile_state)
        # Deliberately vulnerable: unsafe object reconstruction from user input.
        imported = pickle.loads(decoded)
    except Exception as exc:
        return (
            jsonify(
                {
                    "error": "profile_state parse failed",
                    "exception": exc.__class__.__name__,
                    "detail": str(exc),
                }
            ),
            500,
        )

    if not isinstance(imported, dict):
        return jsonify({"error": "profile_state must decode to an object"}), 400

    # Deliberately vulnerable: trusts the deserialized user_id and role fields
    # instead of binding import state to the authenticated user.
    target_user_id = str(imported.get("user_id", user["id"]))
    target_user = USERS.get(target_user_id)
    if target_user is None:
        return jsonify({"error": "target user not found", "target_user": target_user_id}), 404

    target_user["display_name"] = str(
        imported.get("display_name", target_user["display_name"])
    )
    target_user["role"] = str(imported.get("role", target_user["role"]))
    target_user["imports"].append(
        {
            "submitted_by": user["id"],
            "applied_to": target_user_id,
            "timestamp": int(time.time()),
        }
    )

    return jsonify(
        {
            "status": "imported",
            "submitted_by": user["id"],
            "applied_to": target_user_id,
            "display_name": target_user["display_name"],
            "role": target_user["role"],
        }
    )


@app.post("/api/import-protected")
def import_protected_profile_state():
    user, error = _require_user()
    if error is not None:
        return error

    body = request.get_json(force=True, silent=True) or {}
    signed_value = body.get("profile_state")
    if not isinstance(signed_value, str) or signed_value == "":
        return jsonify({"error": "profile_state is required"}), 400

    unsigned_blob = _verify_signed_blob(signed_value)
    if unsigned_blob is None:
        return jsonify({"error": "invalid profile_state signature"}), 403

    try:
        imported = pickle.loads(_urlsafe_b64decode(unsigned_blob))
    except Exception as exc:
        return jsonify({"error": "profile_state parse failed", "exception": exc.__class__.__name__}), 400

    if not isinstance(imported, dict):
        return jsonify({"error": "profile_state must decode to an object"}), 400

    user["display_name"] = str(imported.get("display_name", user["display_name"]))
    user["imports"].append(
        {
            "submitted_by": user["id"],
            "applied_to": user["id"],
            "timestamp": int(time.time()),
            "protected": True,
        }
    )
    return jsonify({"status": "imported", "applied_to": user["id"]})


@app.post("/api/import-json-control")
def import_json_control():
    user, error = _require_user()
    if error is not None:
        return error

    body = request.get_json(force=True, silent=True) or {}
    profile_state = body.get("profile_state")
    if not isinstance(profile_state, dict):
        return jsonify({"error": "profile_state must be a JSON object"}), 400

    # Control endpoint: no deserialization, no trusted ownership mutation.
    user["display_name"] = str(profile_state.get("display_name", user["display_name"]))
    return jsonify({"status": "imported", "applied_to": user["id"]})


@app.get("/api/users/me")
def me():
    user, error = _require_user()
    if error is not None:
        return error
    return jsonify(
        {
            "id": user["id"],
            "username": user["username"],
            "display_name": user["display_name"],
            "role": user["role"],
            "tenant_id": user["tenant_id"],
            "imports": user["imports"],
        }
    )


@app.get("/api/threat-intel-artifact")
def threat_intel_artifact():
    user, error = _require_user()
    if error is not None:
        return error

    sample_blob = _make_profile_state(user["id"], user["display_name"], user["role"])
    return jsonify(
        {
            "method": "POST",
            "path": "/api/import",
            "content_type": "application/json",
            "auth": "Authorization: Bearer <current-user-token>",
            "selected_source": "JSON body field profile_state",
            "sample_body": {"profile_state": sample_blob},
            "expected_normal_status": 200,
            "planner_scope": "single request/source selected by upstream discovery",
            "known_vulnerability": "unsafe pickle.loads on profile_state plus trusted user_id/role fields",
        }
    )


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
