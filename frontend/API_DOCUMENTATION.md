# FunBank API Documentation

## Base URL
```
/api/users
```

## Authentication Endpoints

### 1. Register New User
**Endpoint:** `POST /api/users/register`

**Request Body:**
```json
{
    "username": "string",
    "password": "string"
}
```

**Response:**
- Success (200 OK):
```json
"User registered successfully!"
```
- Error (400 Bad Request):
```json
"Username already exists."
```

**Example Usage:**
```javascript
const register = async (username, password) => {
    const response = await fetch('/api/users/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username,
            password
        })
    });
    
    const data = await response.text();
    if (!response.ok) {
        throw new Error(data);
    }
    return data;
};
```

### 2. Login
**Endpoint:** `POST /api/users/login`

**Request Body:**
```json
{
    "username": "string",
    "password": "string"
}
```

**Response:**
- Success (200 OK):
```json
"Bearer <jwt_token>"
```
- Error (401 Unauthorized):
```json
"Invalid credentials"
```

**Example Usage:**
```javascript
const login = async (username, password) => {
    const response = await fetch('/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username,
            password
        })
    });
    
    const data = await response.text();
    if (!response.ok) {
        throw new Error(data);
    }
    return data; // Returns "Bearer <token>"
};
```

## Important Notes for Frontend Implementation

### 1. Token Storage
- Store the JWT token securely (preferably in memory or secure HTTP-only cookies)
- Include the token in subsequent API requests in the Authorization header:
```javascript
headers: {
    'Authorization': 'Bearer <token>'
}
```

### 2. Error Handling
- Implement proper error handling for both endpoints
- Show user-friendly error messages
- Handle network errors

### 3. Form Validation
- Validate username and password on the client side before sending
- Ensure password meets security requirements
- Prevent empty submissions

### 4. Security Considerations
- Use HTTPS for all API calls
- Implement CSRF protection
- Consider implementing rate limiting on the frontend
- Clear sensitive data from memory after use

## Example React Components

### Login Form Component
```typescript
import { useState } from 'react';

interface LoginFormData {
    username: string;
    password: string;
}

const LoginForm = () => {
    const [formData, setFormData] = useState<LoginFormData>({
        username: '',
        password: ''
    });
    const [error, setError] = useState<string>('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await fetch('/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const data = await response.text();
            if (!response.ok) {
                throw new Error(data);
            }

            // Store token
            localStorage.setItem('token', data);
            // Redirect or update app state
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            {error && <div className="error">{error}</div>}
            <input
                type="text"
                value={formData.username}
                onChange={(e) => setFormData({...formData, username: e.target.value})}
                placeholder="Username"
                required
            />
            <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                placeholder="Password"
                required
            />
            <button type="submit">Login</button>
        </form>
    );
};
```

### Registration Form Component
```typescript
import { useState } from 'react';

interface RegisterFormData {
    username: string;
    password: string;
    confirmPassword: string;
}

const RegisterForm = () => {
    const [formData, setFormData] = useState<RegisterFormData>({
        username: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState<string>('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        try {
            const response = await fetch('/api/users/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: formData.username,
                    password: formData.password
                })
            });

            const data = await response.text();
            if (!response.ok) {
                throw new Error(data);
            }

            // Redirect to login or show success message
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            {error && <div className="error">{error}</div>}
            <input
                type="text"
                value={formData.username}
                onChange={(e) => setFormData({...formData, username: e.target.value})}
                placeholder="Username"
                required
            />
            <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                placeholder="Password"
                required
            />
            <input
                type="password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                placeholder="Confirm Password"
                required
            />
            <button type="submit">Register</button>
        </form>
    );
};
```

## Implementation Checklist

1. [ ] Add proper styling to forms
2. [ ] Implement loading states
3. [ ] Add proper form validation
4. [ ] Handle token storage securely
5. [ ] Implement proper routing after successful login/registration
6. [ ] Add proper error messages and user feedback
7. [ ] Implement CSRF protection
8. [ ] Add rate limiting
9. [ ] Set up proper error boundaries
10. [ ] Add proper TypeScript types for API responses 