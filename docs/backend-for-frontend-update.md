# Backend for Frontend Update

## Project Status
✅ **Backend Implementation Complete** - All 97 tests passing
🔄 **Next Phase**: Frontend Development

## Backend Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.x
- **Database**: Aiven MySQL (cloud)
- **Authentication**: Cookie-based session authentication
- **Security**: Spring Security with custom configuration
- **Testing**: JUnit 5 with MockMvc (97 tests passing)

### Database Schema
The backend connects to an Aiven MySQL database with the following main tables:
- `users` - User accounts and authentication
- `accounts` - Financial accounts (checking, savings, etc.)
- `transactions` - Financial transactions
- `goals` - Financial goals and targets
- `budgets` - Budget categories and limits
- `account_balance_history` - Account balance tracking

## API Endpoints Reference

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints
```
POST /api/auth/register
POST /api/auth/login  
POST /api/auth/logout
GET  /api/auth/profile
PUT  /api/auth/profile
PUT  /api/auth/change-password
```

### Account Management
```
GET    /api/accounts
POST   /api/accounts
GET    /api/accounts/{id}
PUT    /api/accounts/{id}
DELETE /api/accounts/{id}
POST   /api/accounts/deposit
POST   /api/accounts/withdraw
POST   /api/accounts/transfer
POST   /api/accounts/analyze
```

### Transaction Management
```
GET  /api/transactions
POST /api/transactions
GET  /api/transactions/{id}
PUT  /api/transactions/{id}
DELETE /api/transactions/{id}
POST /api/transactions/import
GET  /api/transactions/categories
```

### Goal Management
```
GET  /api/goals
POST /api/goals
GET  /api/goals/{id}
PUT  /api/goals/{id}
DELETE /api/goals/{id}
GET  /api/goals/progress
```

### Budget Management
```
GET  /api/budgets
POST /api/budgets
GET  /api/budgets/{id}
PUT  /api/budgets/{id}
DELETE /api/budgets/{id}
GET  /api/budgets/summary
```

### Analytics & Dashboard
```
GET /api/analytics/spending
GET /api/analytics/income
GET /api/analytics/net-worth
GET /api/analytics/cash-flow
GET /api/analytics/investment-performance
GET /api/dashboard
GET /api/dashboard/accounts
GET /api/dashboard/transactions
GET /api/dashboard/spending
```

### AI Chat & Analysis
```
POST /api/chat
POST /api/accounts/analyze
```

### Health Check
```
GET /api/health
```

## Data Models

### User Model
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### Account Model
```json
{
  "id": 1,
  "userId": 1,
  "name": "Checking Account",
  "accountNumber": "1234567890",
  "balance": 5000.00,
  "accountType": "CHECKING",
  "color": "#FFFFFF",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### Transaction Model
```json
{
  "id": 1,
  "accountId": 1,
  "type": "expense",
  "amount": 100.00,
  "description": "Grocery shopping",
  "createdAt": "2024-01-01T12:00:00"
}
```

### Goal Model
```json
{
  "id": 1,
  "userId": 1,
  "name": "Save for vacation",
  "description": "Save money for summer vacation",
  "targetAmount": 5000.00,
  "currentAmount": 1000.00,
  "status": "active",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### Budget Model
```json
{
  "id": 1,
  "userId": 1,
  "name": "Monthly Groceries",
  "description": "Budget for monthly grocery expenses",
  "amount": 500.00,
  "category": "Food",
  "period": "monthly",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

## Authentication Flow

### Login Process
1. **POST /api/auth/login** with email/password
2. Backend validates credentials
3. Creates session and sets authentication cookie
4. Returns user data and success message

### Session Management
- Uses Spring Security with cookie-based sessions
- Sessions are automatically managed by Spring
- No JWT tokens - pure session-based authentication
- Logout invalidates the session

### Protected Routes
All API endpoints except `/api/auth/register`, `/api/auth/login`, and `/api/health` require authentication.

## Error Handling

### Standard Error Responses
```json
{
  "error": "Error message description"
}
```

### HTTP Status Codes
- `200` - Success
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (not authenticated)
- `403` - Forbidden (not authorized)
- `404` - Not Found
- `500` - Internal Server Error

## CORS Configuration
Backend is configured to accept requests from:
- `http://localhost:3000` (React dev server)
- `http://localhost:5173` (Vite dev server)

## Development Setup

### Running the Backend
```bash
cd backend
./gradlew bootRun
```

### Running Tests
```bash
cd backend
./gradlew test
```

### Database Connection
- **Host**: Aiven MySQL cloud instance
- **Port**: 3306
- **Database**: funbankapp
- **Connection**: Configured via `application.properties`

## Important Notes for Frontend Development

### 1. Authentication Context
- Use cookie-based authentication (no JWT tokens)
- Include credentials in all API requests
- Handle session expiration gracefully

### 2. API Request Format
```javascript
// Example API call
fetch('/api/accounts', {
  method: 'GET',
  credentials: 'include', // Important for cookies
  headers: {
    'Content-Type': 'application/json'
  }
})
```

### 3. Error Handling
- Always check for 401/403 responses
- Redirect to login on authentication failures
- Display user-friendly error messages

### 4. Data Types
- All monetary values are `BigDecimal` (numbers, not strings)
- Dates are in ISO format (`2024-01-01T12:00:00`)
- IDs are `Long` type (numbers)

### 5. Real-time Updates
- No WebSocket support currently
- Use polling for dashboard updates
- Consider implementing WebSocket for real-time features

## AI Integration Notes

### OpenAI Requirements
**Yes, you need OpenAI tokens for AI features!**

The backend has AI endpoints that require OpenAI API integration:
- `POST /api/chat` - General AI chat
- `POST /api/accounts/analyze` - Financial analysis

### AI Configuration Needed
1. **OpenAI API Key** - Required for AI features
2. **Environment Variables** - Set in `application.properties`
3. **Rate Limiting** - Consider implementing to control API costs
4. **Fallback Responses** - Handle API failures gracefully

### AI Features Available
- Financial advice and recommendations
- Spending pattern analysis
- Anomaly detection
- Goal planning assistance
- Budget optimization suggestions

## Next Steps for Frontend

### Phase 1: Authentication & Core Setup
1. Set up React/Vite project structure
2. Implement authentication context
3. Create login/register pages
4. Set up protected routes
5. Configure API client with cookie handling

### Phase 2: Dashboard & Navigation
1. Create main dashboard layout
2. Implement navigation and routing
3. Build account overview components
4. Add recent transactions display
5. Create basic charts and visualizations

### Phase 3: Core Features
1. Account management (CRUD operations)
2. Transaction management
3. Goal tracking
4. Budget management
5. Basic analytics display

### Phase 4: Advanced Features
1. AI chat integration (requires OpenAI setup)
2. Advanced analytics and charts
3. Data import/export
4. Mobile responsiveness
5. Performance optimization

## Testing Strategy

### Backend Testing (Complete)
- ✅ Unit tests for all controllers
- ✅ Integration tests for DAOs
- ✅ Authentication flow tests
- ✅ Error handling tests

### Frontend Testing (To Do)
- Unit tests for components
- Integration tests for API calls
- E2E tests for user flows
- Authentication flow testing

## Deployment Considerations

### Backend Deployment
- Spring Boot JAR file ready for deployment
- Database connection configured for cloud
- Environment-specific configurations available

### Frontend Deployment
- Build process needed
- Static file hosting required
- CORS configuration for production domains

## Support and Documentation

### API Documentation
- Swagger/OpenAPI documentation available at `/swagger-ui.html` (when running)
- All endpoints documented with request/response examples

### Code Quality
- All backend code follows Spring Boot best practices
- Comprehensive test coverage (97 tests)
- Clean architecture with separation of concerns

### Performance
- Database queries optimized
- Connection pooling configured
- Caching strategy ready for implementation

---

**Status**: Backend ready for frontend integration
**Test Coverage**: 97/97 tests passing
**API Coverage**: Complete for MVP requirements
**Next Action**: Start frontend development with authentication flow 