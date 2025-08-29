from flask import Flask, jsonify

app = Flask(__name__)

# Hardcoded client data (with "accidental" IP addresses included)
clients = [
    {"name": "Alice", "location": "Paris, France", "ip": "192.168.1.42"},
    {"name": "Bob", "location": "Berlin, Germany", "ip": "10.0.0.14"},
    {"name": "Charlie", "location": "Casablanca, Morocco", "ip": "172.16.5.99"},
]

@app.route("/clients", methods=["GET"])
def get_clients():
    return jsonify(clients)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
