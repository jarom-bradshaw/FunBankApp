# Backend Development Plan

## Database Schema

### Core Tables
- [x] Users
  - id (PK)
  - username
  - password_hash
  - email
  - created_at
  - last_login
  - two_factor_enabled
  - preferences (JSON)

- [x] Accounts
  - id (PK)
  - user_id (FK)
  - name
  - type (enum: checking, savings, investment, credit)
  - balance
  - currency
  - interest_rate
  - created_at
  - last_updated

- [x] Transactions
  - id (PK)
  - account_id (FK)
  - amount
  - type (enum: income, expense, transfer)
  - category
  - description
  - date
  - recurring_id (FK, nullable)
  - created_at

- [ ] Categories
  - id (PK)
  - user_id (FK)
  - name
  - type (income/expense)
  - parent_id (FK, nullable)
  - color
  - icon

- [x] Budgets
  - id (PK)
  - user_id (FK)
  - category_id (FK)
  - amount
  - period (monthly/yearly)
  - start_date
  - end_date

- [x] Goals
  - id (PK)
  - user_id (FK)
  - name
  - target_amount
  - current_amount
  - deadline
  - type (short-term/long-term)
  - status

- [ ] Investments
  - id (PK)
  - user_id (FK)
  - name
  - type (stock, bond, crypto, etc.)
  - quantity
  - purchase_price
  - current_price
  - purchase_date

- [ ] Debts
  - id (PK)
  - user_id (FK)
  - name
  - amount
  - interest_rate
  - minimum_payment
  - due_date
  - type (credit, loan, mortgage)

## API Endpoints

### Authentication
- [x] POST /api/users/register
- [x] POST /api/users/login
- [x] POST /api/users/logout
- [ ] POST /api/auth/refresh-token
- [ ] POST /api/auth/2fa/enable
- [ ] POST /api/auth/2fa/verify

### User Management
- [x] GET /api/users/profile
- [x] PUT /api/users/profile
- [x] PUT /api/users/password
- [ ] PUT /api/users/preferences

### Account Management
- [x] GET /api/accounts
- [x] POST /api/accounts
- [ ] GET /api/accounts/{id}
- [ ] PUT /api/accounts/{id}
- [ ] DELETE /api/accounts/{id}
- [ ] GET /api/accounts/{id}/transactions
- [ ] GET /api/accounts/{id}/balance-history

### Transaction Management
- [x] GET /api/transactions
- [x] POST /api/transactions
- [x] GET /api/transactions/{id}
- [x] PUT /api/transactions/{id}
- [x] DELETE /api/transactions/{id}
- [x] POST /api/transactions/import
- [x] GET /api/transactions/categories

### Budget Management
- [x] GET /api/budgets
- [x] POST /api/budgets
- [x] GET /api/budgets/{id}
- [x] PUT /api/budgets/{id}
- [x] DELETE /api/budgets/{id}
- [x] GET /api/budgets/summary

### Goal Management
- [x] GET /api/goals
- [x] POST /api/goals
- [x] GET /api/goals/{id}
- [x] PUT /api/goals/{id}
- [x] DELETE /api/goals/{id}
- [x] GET /api/goals/progress

### Investment Management
- [ ] GET /api/investments
- [ ] POST /api/investments
- [ ] GET /api/investments/{id}
- [ ] PUT /api/investments/{id}
- [ ] DELETE /api/investments/{id}
- [ ] GET /api/investments/portfolio

### Debt Management
- [ ] GET /api/debts
- [ ] POST /api/debts
- [ ] GET /api/debts/{id}
- [ ] PUT /api/debts/{id}
- [ ] DELETE /api/debts/{id}
- [ ] GET /api/debts/summary

### Analytics
- [x] GET /api/analytics/spending
- [x] GET /api/analytics/income
- [x] GET /api/analytics/net-worth
- [x] GET /api/analytics/cash-flow
- [x] GET /api/analytics/investment-performance

### AI/ML Features
- [x] POST /api/ai/chat
- [x] GET /api/ai/recommendations
- [x] GET /api/ai/spending-patterns
- [x] GET /api/ai/anomaly-detection

## Implementation Phases

### Phase 1: Core Infrastructure
1. [x] Set up Spring Boot project
2. [x] Configure database connection
3. [x] Implement basic security
4. [x] Set up CORS
5. [x] Create base entity classes
6. [x] Implement repository interfaces
7. [x] Set up service layer structure

### Phase 2: Authentication & User Management
1. [x] Implement JWT authentication
2. [x] Create user registration
3. [x] Implement login/logout
4. [x] Add password hashing
5. [x] Set up user preferences (basic profile endpoints)

### Phase 3: Account & Transaction Management
1. [x] Implement account CRUD (basic create/view, update/delete optional)
2. [x] Create transaction system
3. [ ] Add category management
4. [ ] Implement transaction import (basic import endpoint present)
5. [x] Add balance tracking

### Phase 4: Budgeting & Goals
1. [x] Create budget system
2. [x] Implement goal tracking
3. [x] Add progress monitoring
4. [ ] Create budget vs actual tracking

### Phase 5: Investment & Debt Management
1. [ ] Implement investment tracking
2. [ ] Create debt management system
3. [ ] Add portfolio tracking
4. [ ] Implement payment scheduling

### Phase 6: Analytics & Reporting
1. [x] Create spending analysis
2. [x] Implement net worth tracking
3. [x] Add cash flow analysis
4. [ ] Create custom reports

### Phase 7: AI/ML Integration
1. [x] Set up AI chat system
2. [x] Implement recommendations
3. [x] Add pattern detection
4. [x] Create anomaly detection

### Phase 8: Security & Optimization
1. [ ] Implement 2FA
2. [ ] Add rate limiting
3. [ ] Set up caching
4. [ ] Optimize queries
5. [ ] Add monitoring

## Handoff Points to Frontend

1. After Phase 1: Frontend can start building authentication UI
2. After Phase 2: Frontend can implement full authentication flow
3. After Phase 3: Frontend can build account and transaction management
4. After Phase 4: Frontend can implement budgeting and goals features
5. After Phase 5: Frontend can add investment and debt tracking
6. After Phase 6: Frontend can implement analytics dashboard
7. After Phase 7: Frontend can integrate AI features
8. After Phase 8: Frontend can implement final security features

## Testing Requirements

### Unit Tests
- [ ] Repository layer tests
- [ ] Service layer tests
- [ ] Controller layer tests
- [ ] Security tests

### Integration Tests
- [ ] API endpoint tests
- [ ] Database integration tests
- [ ] Authentication flow tests
- [ ] Transaction flow tests

### Performance Tests
- [ ] Load testing
- [ ] Stress testing
- [ ] Database performance
- [ ] API response times

## Documentation

### API Documentation
- [ ] OpenAPI/Swagger documentation
- [ ] Endpoint descriptions
- [ ] Request/response examples
- [ ] Error codes

### Technical Documentation
- [ ] Database schema
- [ ] Security measures
- [ ] Deployment guide
- [ ] Maintenance procedures 