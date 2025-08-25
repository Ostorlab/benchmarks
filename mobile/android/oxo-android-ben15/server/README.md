# Authentication Server

A Flask-based authentication server with JWT token support, user registration, login, and profile management.

## Prerequisites

- Python 3.7+
- pip (Python package manager)

## Installation

1. Clone or navigate to the project directory
2. Create a virtual environment:
   ```bash
   python3 -m venv venv
   ```

3. Activate the virtual environment:
   ```bash
   source venv/bin/activate
   ```

4. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Running the Server

Start the server with:
```bash
python3 app.py
```

The server will run on `http://localhost:5000` by default.

## Default User

When the server starts for the first time, it creates a default user:

- **Username:** `admin`
- **Password:** `admin123`
- **Email:** `admin@example.com`

You can customize these defaults using environment variables:
- `DEFAULT_USERNAME`
- `DEFAULT_PASSWORD`
- `DEFAULT_EMAIL`

## API Endpoints

### Authentication
- `POST /register` - Register a new user
- `POST /login` - Login with username/password
- `GET /profile` - Get user profile (requires JWT token)
- `POST /change-password` - Change user password (requires JWT token)

### Utility
- `GET /health` - Health check endpoint
- `GET /protected` - Protected endpoint example (requires JWT token)
- `DELETE /clear-users` - Clear all users from database

## Environment Variables

You can configure the following environment variables:

- `SECRET_KEY` - Flask secret key (default: 'your-secret-key-here')
- `DATABASE_URL` - Database URL (default: 'sqlite:///auth.db')
- `JWT_SECRET_KEY` - JWT secret key (default: 'jwt-secret-string')
- `DEFAULT_USERNAME` - Default user username (default: 'admin')
- `DEFAULT_PASSWORD` - Default user password (default: 'admin123')
- `DEFAULT_EMAIL` - Default user email (default: 'admin@example.com')

## Usage Examples

### Login with curl:
```bash
curl -X POST http://localhost:5000/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Access protected endpoint:
```bash
curl -X GET http://localhost:5000/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```