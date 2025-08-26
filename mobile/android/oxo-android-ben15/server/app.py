"""Flask authentication server with JWT token support.

This module provides user registration, login, and authentication functionality
using Flask, SQLAlchemy, and JWT tokens.
"""

import os
from datetime import datetime, timedelta

import flask
import flask_sqlalchemy
import flask_jwt_extended
import werkzeug.security

app = flask.Flask(__name__)

app.config['SECRET_KEY'] = 'your-production-secret-key-here'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JWT_SECRET_KEY'] = 'your-production-jwt-secret-here'
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(hours=24)

db = flask_sqlalchemy.SQLAlchemy(app)
jwt = flask_jwt_extended.JWTManager(app)

@jwt.invalid_token_loader
def invalid_token_callback(error):
    """Handle invalid JWT tokens.
    
    Args:
        error: The error that occurred during token validation.
        
    Returns:
        JSON response with error message and 422 status code.
    """
    print(f"Invalid token error: {error}")
    return flask.flask.jsonify({'error': 'Invalid token'}), 422

@jwt.expired_token_loader
def expired_token_callback(jwt_header, jwt_payload):
    """Handle expired JWT tokens.
    
    Args:
        jwt_header: The JWT header.
        jwt_payload: The JWT payload.
        
    Returns:
        JSON response with error message and 422 status code.
    """
    print(f"Expired token: {jwt_payload}")
    return flask.flask.jsonify({'error': 'Token has expired'}), 422

@jwt.unauthorized_loader
def missing_token_callback(error):
    """Handle missing JWT tokens.
    
    Args:
        error: The error that occurred when no token was provided.
        
    Returns:
        JSON response with error message and 422 status code.
    """
    print(f"Missing token error: {error}")
    return flask.flask.jsonify({'error': 'Authorization token is required'}), 422

class User(db.Model):
    """User model for storing user authentication data.
    
    Attributes:
        id: Primary key for the user.
        username: Unique username for the user.
        email: Unique email address for the user.
        password_hash: Hashed password for security.
        created_at: Timestamp when the user was created.
    """
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def set_password(self, password):
        """Hash and set the user's password.
        
        Args:
            password: The plain text password to hash and store.
        """
        self.password_hash = werkzeug.security.generate_password_hash(password)

    def check_password(self, password):
        """Verify a password against the stored hash.
        
        Args:
            password: The plain text password to verify.
            
        Returns:
            True if password matches, False otherwise.
        """
        return werkzeug.security.check_password_hash(self.password_hash, password)

    def to_dict(self):
        """Convert user object to dictionary representation.
        
        Returns:
            Dictionary containing user data (excluding password).
        """
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'created_at': self.created_at.isoformat()
        }

@app.route('/register', methods=['POST'])
def register():
    """Register a new user.
    
    Expects JSON payload with username, email, and password.
    
    Returns:
        JSON response with user data and access token on success,
        or error message on failure.
    """
    data = flask.request.get_json()
    
    required_fields = ['username', 'email', 'password']
    if data is None or not all(data.get(field) is not None and data.get(field) != '' for field in required_fields):
        return flask.flask.jsonify({
            'error': 'Username, email, and password are required'
        }), 400
    
    if User.query.filter_by(username=data['username']).first() is not None:
        return flask.jsonify({'error': 'Username already exists'}), 400
    
    if User.query.filter_by(email=data['email']).first() is not None:
        return flask.jsonify({'error': 'Email already exists'}), 400
    
    user = User(username=data['username'], email=data['email'])
    user.set_password(data['password'])
    
    db.session.add(user)
    db.session.commit()
    
    access_token = flask_jwt_extended.create_access_token(identity=str(user.id))
    
    return flask.jsonify({
        'message': 'User registered successfully',
        'user': user.to_dict(),
        'access_token': access_token
    }), 201

@app.route('/login', methods=['POST'])
def login():
    """Authenticate user and return access token.
    
    Expects JSON payload with username and password.
    
    Returns:
        JSON response with user data and access token on success,
        or error message on failure.
    """
    data = flask.request.get_json()
    
    if data is None or data.get('username') is None or data.get('username') == '' or data.get('password') is None or data.get('password') == '':
        return flask.jsonify({
            'error': 'Username and password are required'
        }), 400
    
    user = User.query.filter_by(username=data['username']).first()
    
    if user is None or user.check_password(data['password']) is False:
        return flask.jsonify({'error': 'Invalid username or password'}), 401
    
    access_token = flask_jwt_extended.create_access_token(identity=str(user.id))
    
    return flask.jsonify({
        'message': 'Login successful',
        'user': user.to_dict(),
        'access_token': access_token
    }), 200

@app.route('/profile', methods=['GET'])
@flask_jwt_extended.jwt_required()
def get_profile():
    """Get current user's profile information.
    
    Requires valid JWT token in Authorization header.
    
    Returns:
        JSON response with user profile data.
    """
    current_user_id = flask_jwt_extended.get_jwt_identity()
    user = User.query.get(int(current_user_id))
    
    if user is None:
        return flask.jsonify({'error': 'User not found'}), 404
    
    return flask.jsonify({'user': user.to_dict()}), 200

@app.route('/change-password', methods=['POST'])
@flask_jwt_extended.jwt_required()
def change_password():
    """Change user's password.
    
    Requires valid JWT token and current password for verification.
    Expects JSON payload with current_password and new_password.
    
    Returns:
        JSON response with success message or error.
    """
    current_user_id = flask_jwt_extended.get_jwt_identity()
    data = flask.request.get_json()
    
    print(f"Change password request - User ID: {current_user_id}")
    print(f"Request data: {data}")
    
    required_fields = ['current_password', 'new_password']
    if data is None or not all(data.get(field) is not None and data.get(field) != '' for field in required_fields):
        print("Missing required fields")
        return flask.jsonify({
            'error': 'Current password and new password are required'
        }), 400
    
    user = User.query.get(int(current_user_id))
    if user is None:
        return flask.jsonify({'error': 'User not found'}), 404
    
    if user.check_password(data['current_password']) is False:
        return flask.jsonify({'error': 'Current password is incorrect'}), 401
    
    if len(data['new_password']) < 6:
        return flask.jsonify({
            'error': 'New password must be at least 6 characters long'
        }), 400
    
    user.set_password(data['new_password'])
    db.session.commit()
    
    return flask.jsonify({'message': 'Password changed successfully'}), 200

@app.route('/protected', methods=['GET'])
@flask_jwt_extended.jwt_required()
def protected():
    """Protected endpoint that requires valid JWT token.
    
    Returns:
        JSON response with greeting message for authenticated user.
    """
    current_user_id = flask_jwt_extended.get_jwt_identity()
    return flask.jsonify({
        'message': (
            f'Hello user {current_user_id}! '
            'This is a protected endpoint.'
        )
    }), 200

def create_default_user():
    """Create a default user if no users exist in the database.
    
    Creates an admin user with credentials from environment variables
    or default values if no users exist in the database.
    """
    if User.query.count() == 0:
        default_user = User(
            username='admin',
            email='admin@example.com'
        )
        default_user.set_password('admin123')
        
        db.session.add(default_user)
        db.session.commit()
        
        print(f"Default user created: {default_user.username}")
    else:
        print("Users already exist, skipping default user creation")

@app.route('/clear-users', methods=['DELETE'])
def clear_users():
    """Clear all users from the database.
    
    WARNING: This permanently deletes all user data.
    
    Returns:
        JSON response with count of deleted users or error message.
    """
    try:
        user_count = User.query.count()
        User.query.delete()
        db.session.commit()
        return flask.jsonify({
            'message': (
                f'Successfully cleared {user_count} users '
                'from the database'
            ),
            'users_deleted': user_count
        }), 200
    except Exception as e:
        db.session.rollback()
        return flask.jsonify({
            'error': f'Failed to clear users: {str(e)}'
        }), 500

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint.
    
    Returns:
        JSON response indicating server status.
    """
    return flask.jsonify({
        'status': 'healthy',
        'message': 'Authentication server is running'
    }), 200

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
        create_default_user()
    app.run(debug=True, host='0.0.0.0', port=5000)