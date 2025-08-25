# Authentication Server

A Flask-based authentication server with JWT token support, user registration, login, and profile management.

## Prerequisites

- Docker and Docker Compose

## Running the Server

### Using Docker Compose (Recommended)

1. Make sure Docker and Docker Compose are installed on your system
2. Run the server using docker-compose:
   ```bash
   docker-compose up -d
   ```

The server will be available at `http://localhost:5000`.

To stop the server:
```bash
docker-compose down
```

### Using Docker directly

1. Build the Docker image:
   ```bash
   docker build -t auth-server .
   ```

2. Run the container:
   ```bash
   docker run -p 5000:5000 auth-server
   ```

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