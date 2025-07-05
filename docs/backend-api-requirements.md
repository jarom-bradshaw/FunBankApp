# Backend API Requirements for Personal Finance Dashboard

## 🎯 **Overview**

This document outlines the complete backend API requirements needed to support the personal finance dashboard frontend. The frontend is fully built and ready for backend integration.

## 🔐 **Authentication & Authorization**

### **User Management**
```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh-token
GET /api/auth/profile
PUT /api/auth/profile
PUT /api/auth/change-password
DELETE /api/auth/account
```

### **JWT Token Structure**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": "number",
  "user": {
    "id": "number",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "createdAt": "datetime"
  }
}
```

## 💰 **Account Management APIs**

### **Account CRUD Operations**
```http
GET /api/accounts
POST /api/accounts
GET /api/accounts/{id}
PUT /api/accounts/{id}
DELETE /api/accounts/{id}
```

### **Account Data Structure**
```json
{
  "id": "number",
  "name": "string",
  "accountNumber": "string",
  "accountType": "checking|savings|credit|investment|loan|cash",
  "balance": "number",
  "color": "string",
  "userId": "number",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### **Account Balance History**
```http
GET /api/accounts/{id}/balance-history
GET /api/accounts/{id}/transactions
```

### **Account Balance History Structure**
```json
{
  "id": "number",
  "accountId": "number",
  "balance": "number",
  "change": "number",
  "date": "datetime",
  "description": "string"
}
```

## 💳 **Transaction Management APIs**

### **Transaction CRUD Operations**
```http
GET /api/transactions
POST /api/transactions
GET /api/transactions/{id}
PUT /api/transactions/{id}
DELETE /api/transactions/{id}
```

### **Transaction Data Structure**
```json
{
  "id": "number",
  "description": "string",
  "amount": "number",
  "category": "string",
  "accountId": "number",
  "accountName": "string",
  "date": "datetime",
  "type": "income|expense",
  "notes": "string",
  "userId": "number",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### **Transaction Filtering & Search**
```http
GET /api/transactions?search={term}
GET /api/transactions?category={category}
GET /api/transactions?account={accountId}
GET /api/transactions?dateFrom={date}&dateTo={date}
GET /api/transactions?type={income|expense}
GET /api/transactions?sortBy={field}&sortOrder={asc|desc}
```

### **Bulk Operations**
```http
DELETE /api/transactions/bulk
POST /api/transactions/import
GET /api/transactions/export
```

## 🎯 **Goal Tracking APIs**

### **Goal CRUD Operations**
```http
GET /api/goals
POST /api/goals
GET /api/goals/{id}
PUT /api/goals/{id}
DELETE /api/goals/{id}
```

### **Goal Data Structure**
```json
{
  "id": "number",
  "name": "string",
  "targetAmount": "number",
  "currentAmount": "number",
  "targetDate": "datetime",
  "category": "string",
  "priority": "low|medium|high",
  "description": "string",
  "startDate": "datetime",
  "userId": "number",
  "milestones": [
    {
      "id": "number",
      "amount": "number",
      "description": "string",
      "completed": "boolean"
    }
  ],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### **Goal Progress Updates**
```http
PUT /api/goals/{id}/progress
GET /api/goals/{id}/milestones
PUT /api/goals/{id}/milestones/{milestoneId}
```

## 📊 **Analytics & Reporting APIs**

### **Dashboard Analytics**
```http
GET /api/analytics/dashboard
GET /api/analytics/spending-trends?period={week|month|quarter|year}
GET /api/analytics/budget-vs-actual?period={period}
GET /api/analytics/income-analysis?period={period}
GET /api/analytics/category-breakdown?period={period}
```

### **Financial Health Score**
```http
GET /api/analytics/financial-health
GET /api/analytics/recommendations
```

### **Export & Reporting**
```http
GET /api/analytics/export?type={pdf|csv}&period={period}
GET /api/analytics/reports/monthly
GET /api/analytics/reports/yearly
```

### **Analytics Data Structures**
```json
{
  "spendingTrends": {
    "labels": ["string"],
    "datasets": [
      {
        "label": "string",
        "data": ["number"],
        "borderColor": "string",
        "backgroundColor": "string"
      }
    ]
  },
  "financialHealth": {
    "score": "number",
    "savingsRate": "number",
    "budgetAdherence": "number",
    "diversityScore": "number",
    "recommendations": ["string"]
  }
}
```

## 💸 **Budget Management APIs**

### **Budget CRUD Operations**
```http
GET /api/budgets
POST /api/budgets
GET /api/budgets/{id}
PUT /api/budgets/{id}
DELETE /api/budgets/{id}
```

### **Budget Data Structure**
```json
{
  "id": "number",
  "name": "string",
  "category": "string",
  "amount": "number",
  "period": "monthly|yearly",
  "startDate": "datetime",
  "endDate": "datetime",
  "userId": "number",
  "alerts": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### **Budget Tracking**
```http
GET /api/budgets/{id}/progress
GET /api/budgets/alerts
PUT /api/budgets/{id}/alerts
```

## 🤖 **AI Chat APIs**

### **Chat Interface**
```http
POST /api/chat/message
GET /api/chat/history
DELETE /api/chat/history
```

### **Chat Data Structure**
```json
{
  "id": "number",
  "message": "string",
  "response": "string",
  "userId": "number",
  "timestamp": "datetime",
  "context": {
    "financialData": "object",
    "userPreferences": "object"
  }
}
```

## 📱 **PWA & Notifications APIs**

### **Push Notifications**
```http
POST /api/notifications/subscribe
DELETE /api/notifications/unsubscribe
GET /api/notifications/settings
PUT /api/notifications/settings
```

### **PWA Data**
```http
GET /api/pwa/manifest
GET /api/pwa/service-worker
```

## 🔧 **Utility APIs**

### **Categories Management**
```http
GET /api/categories
POST /api/categories
PUT /api/categories/{id}
DELETE /api/categories/{id}
```

### **Data Import/Export**
```http
POST /api/data/import
GET /api/data/export
GET /api/data/backup
POST /api/data/restore
```

### **System Health**
```http
GET /api/health
GET /api/health/database
GET /api/health/redis
```

## 🗄️ **Database Schema Requirements**

### **Users Table**
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### **Accounts Table**
```sql
CREATE TABLE accounts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  account_number VARCHAR(100),
  account_type ENUM('checking', 'savings', 'credit', 'investment', 'loan', 'cash') NOT NULL,
  balance DECIMAL(15,2) DEFAULT 0.00,
  color VARCHAR(7) DEFAULT '#3B82F6',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **Transactions Table**
```sql
CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  account_id BIGINT NOT NULL,
  description VARCHAR(255) NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  category VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  type ENUM('income', 'expense') NOT NULL,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);
```

### **Goals Table**
```sql
CREATE TABLE goals (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  target_amount DECIMAL(15,2) NOT NULL,
  current_amount DECIMAL(15,2) DEFAULT 0.00,
  target_date DATE NOT NULL,
  category VARCHAR(100) NOT NULL,
  priority ENUM('low', 'medium', 'high') DEFAULT 'medium',
  description TEXT,
  start_date DATE DEFAULT CURRENT_DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **Goal Milestones Table**
```sql
CREATE TABLE goal_milestones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  goal_id BIGINT NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  description VARCHAR(255) NOT NULL,
  completed BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);
```

### **Budgets Table**
```sql
CREATE TABLE budgets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(100) NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  period ENUM('monthly', 'yearly') DEFAULT 'monthly',
  start_date DATE NOT NULL,
  end_date DATE,
  alerts BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## 🔒 **Security Requirements**

### **Authentication**
- JWT-based authentication with refresh tokens
- Password hashing using bcrypt
- Token expiration and rotation
- Rate limiting on auth endpoints

### **Authorization**
- User-based data isolation
- Role-based access control (if needed)
- API key validation for external integrations

### **Data Protection**
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CORS configuration
- HTTPS enforcement

## 📈 **Performance Requirements**

### **Response Times**
- API responses < 200ms for simple operations
- Complex analytics < 2 seconds
- File uploads < 10 seconds

### **Caching**
- Redis for session storage
- Database query caching
- Static asset caching
- API response caching where appropriate

### **Database Optimization**
- Proper indexing on frequently queried fields
- Query optimization
- Connection pooling
- Read replicas for analytics

## 🧪 **Testing Requirements**

### **API Testing**
- Unit tests for all endpoints
- Integration tests for data flow
- Performance testing for analytics
- Security testing for authentication

### **Data Validation**
- Input validation tests
- Edge case handling
- Error response testing
- Rate limiting tests

## 🚀 **Deployment Requirements**

### **Environment Configuration**
- Environment-specific configuration
- Database migration scripts
- Health check endpoints
- Monitoring and logging

### **Scalability**
- Horizontal scaling capability
- Load balancing support
- Database sharding strategy
- Microservices architecture ready

## 📋 **Implementation Priority**

### **Phase 1 (Core Features)**
1. User authentication
2. Account CRUD operations
3. Transaction CRUD operations
4. Basic dashboard data

### **Phase 2 (Advanced Features)**
1. Goal tracking
2. Budget management
3. Analytics and reporting
4. AI chat integration

### **Phase 3 (Enhancement)**
1. PWA features
2. Push notifications
3. Advanced analytics
4. Data import/export

## 🎯 **Success Criteria**

- All frontend features work with real data
- API response times meet performance requirements
- Security requirements are fully implemented
- Comprehensive test coverage (>80%)
- Production-ready deployment configuration

The backend should be built to support all the frontend features that have been implemented, ensuring a seamless user experience with real data persistence and robust API functionality. 