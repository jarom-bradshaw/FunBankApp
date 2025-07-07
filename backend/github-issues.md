# GitHub Issues

## New Feature Requests

### 1. Front-Facing Marketing Website
- Create a public-facing website to market FunBankApp.
- Include product features, pricing, and signup links.
- Add a blog section with articles on using the product and saving money.

### 2. Blog with Comments
- Add a blog system to the marketing site.
- Allow users with accounts to comment on blog posts (future feature).

### 3. Free Tier and Pro Tier
- Implement a free tier with basic features.
- Implement a pro tier with advanced features (e.g., analytics, investment tools).
- Add pricing and upgrade/downgrade logic.

### 4. Monte Carlo Investment Tools
- Add Monte Carlo simulation tools for investment planning.
- Integrate with user accounts and analytics.

---

(These issues should be tracked and broken down further as development progresses.)

## 🔴 Critical Security Issues

### Issue #1: Hardcoded Database Credentials
**Title**: [SECURITY] Database credentials exposed in application.properties
**Labels**: security, critical, high-priority
**Description**: 
Database credentials are hardcoded in `src/main/resources/application.properties`. This is a critical security vulnerability that exposes sensitive database access information.

**Impact**: 
- Database credentials are visible in source code
- Potential unauthorized database access
- Violation of security best practices

**Solution**: 
- Move credentials to environment variables
- Create separate configuration files for different environments
- Use Spring Boot's configuration properties

**Files Affected**: `src/main/resources/application.properties`

---

### Issue #2: JWT Secret Hardcoded
**Title**: [SECURITY] JWT secret hardcoded in application properties
**Labels**: security, critical, high-priority
**Description**: 
JWT signing secret is hardcoded in the application properties file, making it vulnerable to exposure.

**Impact**: 
- JWT tokens could be forged
- Authentication bypass possible
- Security compromise of user sessions

**Solution**: 
- Externalize JWT secret to environment variables
- Generate cryptographically secure secrets
- Rotate secrets regularly

**Files Affected**: `src/main/resources/application.properties`

---

## Issue: Weak Password Validation (Addressed)
- [x] Passwords now require at least 8 characters, one uppercase, one lowercase, one digit, and one special character.

---

## Issue: Rate Limiting for Auth Endpoints (Addressed)
- [x] Rate limiting (5 requests/min/IP) added to /api/auth/login and /api/auth/register.
- [ ] Automated tests fail due to user already existing, not due to rate limiting.

---

## 🟡 Security Concerns

### Issue #6: Missing Input Validation
**Title**: [SECURITY] Some endpoints lack proper input validation
**Labels**: security, medium-priority
**Description**: 
Several controller endpoints lack comprehensive input validation, potentially allowing malicious input.

**Impact**: 
- Potential injection attacks
- Data corruption
- Application instability

**Solution**: 
- Add comprehensive input validation
- Use Bean Validation annotations
- Implement custom validators

**Files Affected**: Multiple controller files

---

### Issue #7: CSRF Protection Disabled
**Title**: [SECURITY] CSRF protection is disabled in SecurityConfig
**Labels**: security, medium-priority
**Description**: 
CSRF protection is explicitly disabled in the security configuration, leaving the application vulnerable to CSRF attacks.

**Impact**: 
- Cross-Site Request Forgery attacks
- Unauthorized actions on behalf of users
- Security vulnerabilities

**Solution**: 
- Enable CSRF protection
- Implement proper CSRF token handling
- Configure CSRF for REST APIs

**Files Affected**: `src/main/java/com/jarom/funbankapp/security/SecurityConfig.java`

---

### Issue #8: Missing Audit Logging
**Title**: [SECURITY] No audit logging for sensitive operations
**Labels**: security, medium-priority
**Description**: 
The application lacks audit logging for sensitive operations like login attempts, password changes, and financial transactions.

**Impact**: 
- No trail of suspicious activities
- Difficult to detect security incidents
- Compliance issues

**Solution**: 
- Implement comprehensive audit logging
- Log all sensitive operations
- Store logs securely

**Files Affected**: Multiple service and controller files

---

## 🟠 Code Quality Issues

### Issue #9: Missing Unit Tests
**Title**: [BUG] No unit tests for application components
**Labels**: bug, testing, medium-priority
**Description**: 
The application has test directories but lacks comprehensive unit tests for controllers, services, and repositories.

**Impact**: 
- No confidence in code changes
- Potential regressions
- Difficult to maintain code quality

**Solution**: 
- Write comprehensive unit tests
- Add integration tests
- Implement test coverage reporting

**Files Affected**: All test directories

---

### Issue #10: Inconsistent Error Handling
**Title**: [BUG] Inconsistent error handling across controllers
**Labels**: bug, code-quality, low-priority
**Description**: 
Different controllers use different error handling approaches - some use try-catch blocks while others rely on the global exception handler.

**Impact**: 
- Inconsistent API responses
- Poor user experience
- Difficult to debug issues

**Solution**: 
- Standardize error handling approach
- Use global exception handler consistently
- Implement consistent response formats

**Files Affected**: Multiple controller files

---

### Issue #11: Missing Validation Annotations
**Title**: [BUG] Models lack proper validation constraints
**Labels**: bug, code-quality, low-priority
**Description**: 
Entity models lack proper Bean Validation annotations for data integrity and input validation.

**Impact**: 
- Data integrity issues
- Poor input validation
- Potential data corruption

**Solution**: 
- Add validation annotations to models
- Implement custom validators where needed
- Add validation groups for different scenarios

**Files Affected**: `src/main/java/com/jarom/funbankapp/model/*.java`

---

### Issue #12: Inconsistent Response Formats
**Title**: [BUG] Different controllers return different response structures
**Labels**: bug, api-design, low-priority
**Description**: 
Different API endpoints return inconsistent response formats, making it difficult for frontend integration.

**Impact**: 
- Poor API consistency
- Difficult frontend integration
- Poor developer experience

**Solution**: 
- Standardize API response formats
- Create consistent DTOs
- Implement response wrappers

**Files Affected**: Multiple controller files

---

## 🔵 Architecture Issues

### Issue #13: Mixed DAO/Repository Pattern
**Title**: [ENHANCEMENT] Inconsistent use of DAO and Repository patterns
**Labels**: enhancement, architecture, medium-priority
**Description**: 
The application uses both DAO and Repository patterns inconsistently, creating confusion and maintenance issues.

**Impact**: 
- Inconsistent data access patterns
- Difficult to maintain
- Poor code organization

**Solution**: 
- Standardize on Repository pattern
- Refactor DAO classes to repositories
- Implement consistent data access layer

**Files Affected**: `src/main/java/com/jarom/funbankapp/repository/*.java`

---

### Issue #14: Missing Service Layer Abstraction
**Title**: [ENHANCEMENT] Some business logic in controllers
**Labels**: enhancement, architecture, medium-priority
**Description**: 
Some business logic is implemented directly in controllers instead of being abstracted to service layer.

**Impact**: 
- Poor separation of concerns
- Difficult to test
- Code duplication

**Solution**: 
- Move business logic to service layer
- Implement proper service abstractions
- Add service interfaces

**Files Affected**: Multiple controller files

---

### Issue #15: Missing Transaction Management
**Title**: [ENHANCEMENT] No transaction management annotations
**Labels**: enhancement, database, medium-priority
**Description**: 
Service methods lack proper transaction management annotations, potentially causing data consistency issues.

**Impact**: 
- Data inconsistency
- Partial operation failures
- Database integrity issues

**Solution**: 
- Add @Transactional annotations
- Implement proper transaction boundaries
- Add transaction rollback handling

**Files Affected**: Service layer files

---

## 🟢 Performance Issues

### Issue #16: No Caching Strategy
**Title**: [ENHANCEMENT] No caching implementation for frequently accessed data
**Labels**: enhancement, performance, medium-priority
**Description**: 
The application lacks caching for frequently accessed data like user profiles, account information, and static data.

**Impact**: 
- Poor performance
- Unnecessary database queries
- Slow response times

**Solution**: 
- Implement caching strategy
- Add Redis or in-memory caching
- Cache frequently accessed data

**Files Affected**: Service layer files

---

### Issue #17: Missing Pagination
**Title**: [ENHANCEMENT] Large result sets not paginated
**Labels**: enhancement, performance, low-priority
**Description**: 
API endpoints that return large datasets do not implement pagination, potentially causing performance issues.

**Impact**: 
- Slow response times
- High memory usage
- Poor user experience

**Solution**: 
- Implement pagination for list endpoints
- Add page size limits
- Implement cursor-based pagination

**Files Affected**: Controller files with list endpoints

---

## 🟣 Database Issues

### Issue #18: Missing Database Migrations
**Title**: [ENHANCEMENT] No database migration tool integration
**Labels**: enhancement, database, medium-priority
**Description**: 
The application lacks proper database migration tools like Flyway or Liquibase, making schema changes difficult to manage.

**Impact**: 
- Difficult schema management
- Manual database updates
- Version control issues

**Solution**: 
- Integrate Flyway or Liquibase
- Create migration scripts
- Implement versioned schema changes

**Files Affected**: Build configuration, SQL files

---

### Issue #19: No Database Health Checks
**Title**: [ENHANCEMENT] Missing database connectivity monitoring
**Labels**: enhancement, monitoring, low-priority
**Description**: 
The application lacks proper database health checks and connectivity monitoring.

**Impact**: 
- No early warning of database issues
- Difficult to diagnose problems
- Poor operational visibility

**Solution**: 
- Implement database health checks
- Add connection pool monitoring
- Create database metrics

**Files Affected**: Configuration files

---

## 🟡 DevOps Issues

### Issue #20: No Docker Configuration
**Title**: [ENHANCEMENT] Missing containerization setup
**Labels**: enhancement, devops, medium-priority
**Description**: 
The application lacks Docker configuration for containerization and deployment.

**Impact**: 
- Difficult deployment
- Environment inconsistencies
- Poor scalability

**Solution**: 
- Create Dockerfile
- Add docker-compose.yml
- Implement multi-stage builds

**Files Affected**: New files to be created

---

### Issue #21: No CI/CD Pipeline
**Title**: [ENHANCEMENT] Missing automated testing and deployment
**Labels**: enhancement, devops, medium-priority
**Description**: 
The application lacks automated CI/CD pipeline for testing and deployment.

**Impact**: 
- Manual testing and deployment
- Human error in releases
- Slow development cycle

**Solution**: 
- Set up GitHub Actions
- Implement automated testing
- Add deployment automation

**Files Affected**: `.github/workflows/` directory

---

### Issue #22: No Environment Configuration
**Title**: [ENHANCEMENT] Single application.properties file
**Labels**: enhancement, configuration, low-priority
**Description**: 
The application uses a single properties file instead of environment-specific configurations.

**Impact**: 
- Difficult environment management
- Configuration conflicts
- Poor deployment flexibility

**Solution**: 
- Create environment-specific properties
- Use Spring profiles
- Implement external configuration

**Files Affected**: `src/main/resources/application.properties`

---

## 📋 Issue Creation Commands

To create these issues in GitHub, you can use the GitHub CLI or create them manually through the GitHub web interface. Here are the commands for GitHub CLI:

```bash
# Critical Security Issues
gh issue create --title "[SECURITY] Database credentials exposed in application.properties" --body "$(cat issue1.md)" --label "security,critical,high-priority"
gh issue create --title "[SECURITY] JWT secret hardcoded in application properties" --body "$(cat issue2.md)" --label "security,critical,high-priority"
gh issue create --title "[SECURITY] CORS configuration allows all origins" --body "$(cat issue3.md)" --label "security,high-priority"

# Security Concerns
gh issue create --title "[SECURITY] Some endpoints lack proper input validation" --body "$(cat issue6.md)" --label "security,medium-priority"
gh issue create --title "[SECURITY] CSRF protection is disabled in SecurityConfig" --body "$(cat issue7.md)" --label "security,medium-priority"

# Code Quality Issues
gh issue create --title "[BUG] No unit tests for application components" --body "$(cat issue9.md)" --label "bug,testing,medium-priority"
gh issue create --title "[BUG] Inconsistent error handling across controllers" --body "$(cat issue10.md)" --label "bug,code-quality,low-priority"

# Architecture Issues
gh issue create --title "[ENHANCEMENT] Inconsistent use of DAO and Repository patterns" --body "$(cat issue13.md)" --label "enhancement,architecture,medium-priority"
gh issue create --title "[ENHANCEMENT] Some business logic in controllers" --body "$(cat issue14.md)" --label "enhancement,architecture,medium-priority"

# Performance Issues
gh issue create --title "[ENHANCEMENT] No caching implementation for frequently accessed data" --body "$(cat issue16.md)" --label "enhancement,performance,medium-priority"

# Database Issues
gh issue create --title "[ENHANCEMENT] No database migration tool integration" --body "$(cat issue18.md)" --label "enhancement,database,medium-priority"

# DevOps Issues
gh issue create --title "[ENHANCEMENT] Missing containerization setup" --body "$(cat issue20.md)" --label "enhancement,devops,medium-priority"
gh issue create --title "[ENHANCEMENT] Missing automated testing and deployment" --body "$(cat issue21.md)" --label "enhancement,devops,medium-priority"
```

## 🏷️ Labels to Create

Create these labels in your GitHub repository:

- `security` (red) - Security-related issues
- `critical` (red) - Critical priority issues
- `high-priority` (orange) - High priority issues
- `medium-priority` (yellow) - Medium priority issues
- `low-priority` (green) - Low priority issues
- `bug` (red) - Bug reports
- `enhancement` (blue) - Feature enhancements
- `testing` (purple) - Testing-related issues
- `code-quality` (gray) - Code quality improvements
- `architecture` (blue) - Architecture changes
- `performance` (orange) - Performance improvements
- `database` (blue) - Database-related issues
- `devops` (purple) - DevOps and deployment issues
- `api-design` (green) - API design improvements
- `monitoring` (blue) - Monitoring and observability
- `configuration` (gray) - Configuration management 

---

## Issue: Hardcoded Database Credentials and JWT Secret (Addressed)
- [x] Removed hardcoded DB credentials and JWT secret from application.properties. Now only environment variables are used.
- [ ] Ensure environment variables are set in deployment environments.

---

## Issue: Automated Test Suite and Transfer Functionality Blockers

### Description
- The automated test suite fails at the authentication step (login/registration) with a 400 Bad Request, even though manual registration via debug script works.
- The test suite attempts login, then registration if login fails, but both fail in the suite context.
- There are also compilation errors in `AccountService.java` related to missing `getFromAccountId()` and `getToAccountId()` methods on `TransferRequest`.

### Error Details
- **Test Suite Output:**
  - FAIL User Login: Login failed, attempting registration
  - FAIL User Registration: Request failed (400 Bad Request)
- **Manual debug script:** Registration endpoint works and returns a token.
- **Build Output:**
  - Compilation errors in `AccountService.java`:
    - Cannot find symbol: method `getFromAccountId()` or `getToAccountId()` on `TransferRequest`.

### Steps to Reproduce
1. Run `./automated_test_suite.ps1` in the backend directory.
2. Observe failures at authentication step.
3. Attempt to build the backend with `./gradlew bootRun` and observe compilation errors.

### What Needs to Be Fixed
- Investigate why the test suite cannot register/login users while manual requests work.
- Fix the `TransferRequest` class to include `getFromAccountId()` and `getToAccountId()` methods, or update usages in `AccountService.java` to match the actual field names.
- Ensure all endpoints and test scripts are in sync with the backend API contract.

### Priority
- **High**: Blocks all automated and CI testing, and transfer feature is broken due to compilation errors.

--- 