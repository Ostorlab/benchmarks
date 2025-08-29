from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

@app.route('/api/user-data', methods=['GET'])
def get_user_data():
    return jsonify({
        "userId": "12345",
        "email": "user@example.com",
        "token": "secret_token_123",
        "role": "admin"
    })

@app.route('/api/system-info', methods=['GET'])
def get_system_info():
    return jsonify({
        "version": "1.2.3",
        "debug": True,
        "api_key": "sk_live_12345"
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)