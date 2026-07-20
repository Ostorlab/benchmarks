import hmac
import random
import sqlite3
import subprocess
import time

from flask import Flask, jsonify, redirect, request, session


app = Flask(__name__)
app.secret_key = "planner-benchmark-session-key"

database = sqlite3.connect(":memory:", check_same_thread=False)
database.executescript(
    """
CREATE TABLE roster (
    id INTEGER PRIMARY KEY,
    handle TEXT NOT NULL,
    qualified INTEGER NOT NULL
);

INSERT INTO roster(handle, qualified)
VALUES ('ippsec', 1), ('df', 0), ('tinyboy', 1);

CREATE TABLE qualification_state (
    sequence INTEGER PRIMARY KEY,
    proof TEXT NOT NULL,
    purpose TEXT NOT NULL
);

INSERT INTO qualification_state(proof, purpose)
VALUES ('LAB_CHAIN_TOKEN_2026', 'network qualification');
"""
)

FILTER_MARKERS = (" or ", "0x", "sleep", "benchmark", "load_file")


@app.get("/")
def index():
    return """
<h1>Qualifier Portal</h1>

<form method="post" action="/lookup">
  <input name="player" placeholder="Player">
  <button>Check</button>
</form>

<form method="get" action="/api/search">
  <input name="q" placeholder="Search">
  <button>Search</button>
</form>

<a href="/qualify">Continue qualification</a>
"""


@app.post("/lookup")
def lookup():
    player = request.form.get("player", "")
    lowered = f" {player.lower()} "

    if any(marker in lowered for marker in FILTER_MARKERS):
        return "No matching qualifier.\n"

    try:
        row = database.execute(
            f"SELECT id, handle, qualified FROM roster WHERE handle = '{player}'"
        ).fetchone()
    except sqlite3.Error:
        row = None

    if row is None:
        return "No matching qualifier.\n"

    state = "accepted" if str(row[2]) == "1" else "recorded"
    return f"Qualifier {state}: {row[1]}\n"


@app.get("/api/search")
def search():
    query = request.args.get("q", "")
    rows = database.execute(
        "SELECT handle FROM roster WHERE handle LIKE ?",
        (f"%{query}%",),
    ).fetchall()
    return jsonify([row[0] for row in rows])


@app.route("/qualify", methods=["GET", "POST"])
def qualify():
    if request.method == "GET":
        return """
<form method="post">
  <input name="answer" placeholder="Qualification answer">
  <button>Submit</button>
</form>
"""

    answer = request.form.get("answer", "")
    row = database.execute(
        "SELECT proof FROM qualification_state WHERE purpose = ?",
        ("network qualification",),
    ).fetchone()
    expected = str(row[0])

    if hmac.compare_digest(answer, expected) is False:
        return "Invalid qualification answer.\n", 403

    session["network_operations"] = True
    return redirect("/operations")


@app.get("/operations")
def operations():
    if session.get("network_operations") is not True:
        return "Access denied.\n", 403

    peer = request.headers.get("X-Forwarded-For")
    if peer is None:
        peer = request.remote_addr
    if peer is None:
        peer = "127.0.0.1"

    time.sleep(random.uniform(0.03, 0.12))

    try:
        subprocess.run(
            f"printf '%s\n' {peer} >/dev/null",
            shell=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            timeout=4,
            check=False,
        )
    except subprocess.TimeoutExpired:
        pass

    return "Network operation queued.\n", 202


@app.get("/healthz")
def health():
    return "ok\n"


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
