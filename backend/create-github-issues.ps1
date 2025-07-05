# PowerShell script to create GitHub issues for FunBankApp backend
# This script creates issues for all identified problems in plan.md

param(
    [string]$GitHubToken,
    [string]$Repository = "jarom-bradshaw/FunBankApp"
)

Write-Host "Creating GitHub issues for FunBankApp backend..." -ForegroundColor Green

if (-not $GitHubToken) {
    Write-Host "Please provide a GitHub token using -GitHubToken parameter" -ForegroundColor Red
    Write-Host "You can create a token at: https://github.com/settings/tokens" -ForegroundColor Yellow
    exit 1
}

$headers = @{
    "Authorization" = "token $GitHubToken"
    "Accept" = "application/vnd.github.v3+json"
    "Content-Type" = "application/json"
}

# Function to create an issue
function Create-GitHubIssue {
    param(
        [string]$Title,
        [string]$Body,
        [string[]]$Labels
    )
    
    $issueData = @{
        title = $Title
        body = $Body
        labels = $Labels
    } | ConvertTo-Json -Depth 10
    
    try {
        $response = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repository/issues" -Method Post -Headers $headers -Body $issueData
        Write-Host "Created issue: $($response.title)" -ForegroundColor Green
        return $response
    }
    catch {
        Write-Host "Failed to create issue '$Title': $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Create labels first
Write-Host "Creating labels..." -ForegroundColor Yellow

$labels = @(
    @{name="security"; color="d73a4a"; description="Security-related issues"},
    @{name="critical"; color="d73a4a"; description="Critical priority issues"},
    @{name="high-priority"; color="fbca04"; description="High priority issues"},
    @{name="medium-priority"; color="fef2c0"; description="Medium priority issues"},
    @{name="low-priority"; color="0e8a16"; description="Low priority issues"},
    @{name="bug"; color="d73a4a"; description="Bug reports"},
    @{name="enhancement"; color="1d76db"; description="Feature enhancements"},
    @{name="testing"; color="5319e7"; description="Testing-related issues"},
    @{name="code-quality"; color="666666"; description="Code quality improvements"},
    @{name="architecture"; color="1d76db"; description="Architecture changes"},
    @{name="performance"; color="fbca04"; description="Performance improvements"},
    @{name="database"; color="1d76db"; description="Database-related issues"},
    @{name="devops"; color="5319e7"; description="DevOps and deployment issues"}
)

foreach ($label in $labels) {
    $labelData = $label | ConvertTo-Json
    try {
        Invoke-RestMethod -Uri "https://api.github.com/repos/$Repository/labels" -Method Post -Headers $headers -Body $labelData | Out-Null
        Write-Host "Created label: $($label.name)" -ForegroundColor Green
    }
    catch {
        Write-Host "Label $($label.name) might already exist, continuing..." -ForegroundColor Yellow
    }
}

# Critical Security Issues
Write-Host "Creating critical security issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[SECURITY] Database credentials exposed in application.properties" -Labels @("security", "critical", "high-priority") -Body @"
Database credentials are hardcoded in `backend/src/main/resources/application.properties`. This is a critical security vulnerability that exposes sensitive database access information.

**Impact:**
- Database credentials are visible in source code
- Potential unauthorized database access
- Violation of security best practices

**Solution:**
- Move credentials to environment variables
- Create separate configuration files for different environments
- Use Spring Boot's configuration properties

**Files Affected:** `backend/src/main/resources/application.properties`
"@

Create-GitHubIssue -Title "[SECURITY] JWT secret hardcoded in application properties" -Labels @("security", "critical", "high-priority") -Body @"
JWT signing secret is hardcoded in the application properties file, making it vulnerable to exposure.

**Impact:**
- JWT tokens could be forged
- Authentication bypass possible
- Security compromise of user sessions

**Solution:**
- Externalize JWT secret to environment variables
- Generate cryptographically secure secrets
- Rotate secrets regularly

**Files Affected:** `backend/src/main/resources/application.properties`
"@

Create-GitHubIssue -Title "[SECURITY] CORS configuration allows all origins" -Labels @("security", "high-priority") -Body @"
CORS configuration allows all origins with `*` pattern, which is too permissive for production.

**Impact:**
- Potential cross-origin attacks
- Security vulnerabilities in production
- Violation of security best practices

**Solution:**
- Restrict allowed origins to specific domains
- Implement proper CORS policies
- Use environment-specific configurations

**Files Affected:** `backend/src/main/java/com/jarom/funbankapp/config/CorsConfig.java`
"@

Create-GitHubIssue -Title "[SECURITY] Weak password validation" -Labels @("security", "high-priority") -Body @"
Password validation only requires 6 characters minimum, which is insufficient for security.

**Impact:**
- Weak passwords increase security risk
- Vulnerable to brute force attacks
- Poor security posture

**Solution:**
- Implement stronger password requirements
- Add complexity requirements (uppercase, lowercase, numbers, symbols)
- Increase minimum length to 8+ characters

**Files Affected:** User registration and password validation logic
"@

Create-GitHubIssue -Title "[SECURITY] Authentication endpoints lack rate limiting" -Labels @("security", "medium-priority") -Body @"
Authentication endpoints (`/api/auth/login`, `/api/auth/register`) do not have rate limiting, making them vulnerable to brute force attacks.

**Impact:**
- Brute force attacks on login
- Account enumeration attacks
- Potential DoS attacks

**Solution:**
- Implement rate limiting for auth endpoints
- Use Spring Security rate limiting
- Add CAPTCHA for repeated failures

**Files Affected:** `backend/src/main/java/com/jarom/funbankapp/security/SecurityConfig.java`
"@

Create-GitHubIssue -Title "[SECURITY] Session management using in-memory storage" -Labels @("security", "medium-priority") -Body @"
Application uses in-memory session storage instead of persistent storage, which can cause issues in production.

**Impact:**
- Sessions lost on application restart
- Poor scalability
- Security concerns with session management

**Solution:**
- Implement persistent session storage (Redis, database)
- Configure proper session timeout
- Add session security features

**Files Affected:** Session configuration and management
"@

# Code Quality Issues
Write-Host "Creating code quality issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[BUG] No unit tests for application components" -Labels @("bug", "testing", "medium-priority") -Body @"
The application has test directories but lacks comprehensive unit tests for controllers, services, and repositories.

**Impact:**
- No confidence in code changes
- Potential regressions
- Difficult to maintain code quality

**Solution:**
- Write comprehensive unit tests
- Add integration tests
- Implement test coverage reporting

**Files Affected:** All test directories
"@

Create-GitHubIssue -Title "[CODE-QUALITY] Inconsistent error handling across controllers" -Labels @("code-quality", "medium-priority") -Body @"
Some controllers use try-catch blocks while others rely on global exception handler, creating inconsistent error responses.

**Impact:**
- Inconsistent API responses
- Poor user experience
- Difficult to debug issues

**Solution:**
- Standardize error handling approach
- Use consistent exception handling
- Implement uniform error response format

**Files Affected:** All controller classes
"@

Create-GitHubIssue -Title "[CODE-QUALITY] Missing validation annotations on models" -Labels @("code-quality", "medium-priority") -Body @"
Models lack proper validation constraints, leading to potential data integrity issues.

**Impact:**
- Invalid data can be processed
- Potential security vulnerabilities
- Poor data quality

**Solution:**
- Add Bean Validation annotations
- Implement custom validators where needed
- Add validation error handling

**Files Affected:** Model classes in `backend/src/main/java/com/jarom/funbankapp/model/`
"@

Create-GitHubIssue -Title "[CODE-QUALITY] Inconsistent response formats" -Labels @("code-quality", "medium-priority") -Body @"
Different controllers return different response structures, making API usage inconsistent.

**Impact:**
- Poor API design
- Difficult for frontend integration
- Inconsistent user experience

**Solution:**
- Standardize response format
- Create consistent DTOs
- Implement uniform response wrapper

**Files Affected:** All controller classes
"@

# Architecture Issues
Write-Host "Creating architecture issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[ARCHITECTURE] Mixed DAO/Repository pattern usage" -Labels @("architecture", "medium-priority") -Body @"
Application uses both DAO and Repository interfaces inconsistently, creating architectural confusion.

**Impact:**
- Inconsistent data access patterns
- Difficult to maintain
- Poor architectural design

**Solution:**
- Standardize on Repository pattern
- Remove DAO interfaces
- Implement consistent data access layer

**Files Affected:** Repository and DAO classes
"@

Create-GitHubIssue -Title "[ARCHITECTURE] Business logic in controllers" -Labels @("architecture", "medium-priority") -Body @"
Some business logic is implemented directly in controllers instead of service layer.

**Impact:**
- Poor separation of concerns
- Difficult to test
- Violation of MVC pattern

**Solution:**
- Move business logic to service layer
- Keep controllers thin
- Implement proper layering

**Files Affected:** Controller classes with business logic
"@

Create-GitHubIssue -Title "[ARCHITECTURE] Missing DTOs for some endpoints" -Labels @("architecture", "low-priority") -Body @"
Some endpoints return raw entities instead of DTOs, exposing internal data structure.

**Impact:**
- API coupling to internal models
- Potential security issues
- Poor API design

**Solution:**
- Create DTOs for all endpoints
- Implement proper data transformation
- Hide internal implementation details

**Files Affected:** Endpoints returning raw entities
"@

Create-GitHubIssue -Title "[ARCHITECTURE] Missing transaction management" -Labels @("architecture", "medium-priority") -Body @"
Missing @Transactional annotations on service methods that perform multiple database operations.

**Impact:**
- Potential data inconsistency
- Database integrity issues
- Poor error handling

**Solution:**
- Add @Transactional annotations
- Implement proper transaction boundaries
- Add rollback strategies

**Files Affected:** Service classes with multiple database operations
"@

# Performance Issues
Write-Host "Creating performance issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[PERFORMANCE] No caching strategy implemented" -Labels @("performance", "medium-priority") -Body @"
Application lacks caching for frequently accessed data, leading to unnecessary database queries.

**Impact:**
- Poor performance
- Increased database load
- Slow response times

**Solution:**
- Implement caching strategy
- Use Spring Cache
- Cache frequently accessed data

**Files Affected:** Service classes and data access layer
"@

Create-GitHubIssue -Title "[PERFORMANCE] Potential N+1 query issues" -Labels @("performance", "medium-priority") -Body @"
Some queries might cause N+1 query problems, especially with related entities.

**Impact:**
- Poor performance with large datasets
- Excessive database queries
- Slow application response

**Solution:**
- Optimize queries with joins
- Use eager loading where appropriate
- Implement query optimization

**Files Affected:** Repository queries and service methods
"@

Create-GitHubIssue -Title "[PERFORMANCE] Missing pagination for large result sets" -Labels @("performance", "low-priority") -Body @"
Endpoints that return large datasets lack pagination, potentially causing performance issues.

**Impact:**
- Memory issues with large datasets
- Slow response times
- Poor user experience

**Solution:**
- Implement pagination
- Add page size limits
- Use Spring Data pagination

**Files Affected:** Endpoints returning large datasets
"@

# Database Issues
Write-Host "Creating database issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[DATABASE] Schema management not integrated" -Labels @("database", "medium-priority") -Body @"
SQL files present but not integrated with application startup or deployment process.

**Impact:**
- Manual database setup required
- Deployment complexity
- Potential schema inconsistencies

**Solution:**
- Integrate Flyway or Liquibase
- Automate schema management
- Version control database schema

**Files Affected:** SQL files and application configuration
"@

Create-GitHubIssue -Title "[DATABASE] Missing database health checks" -Labels @("database", "medium-priority") -Body @"
No database connectivity monitoring or health checks implemented.

**Impact:**
- No visibility into database health
- Difficult to diagnose issues
- Poor monitoring capabilities

**Solution:**
- Implement health checks
- Add database monitoring
- Create alerting for database issues

**Files Affected:** Health check endpoints and monitoring
"@

# DevOps Issues
Write-Host "Creating DevOps issues..." -ForegroundColor Yellow

Create-GitHubIssue -Title "[DEVOPS] No Docker configuration" -Labels @("devops", "medium-priority") -Body @"
Missing containerization setup for consistent deployment across environments.

**Impact:**
- Deployment inconsistencies
- Environment-specific issues
- Difficult deployment process

**Solution:**
- Create Dockerfile
- Add docker-compose.yml
- Implement containerization strategy

**Files Affected:** Deployment configuration
"@

Create-GitHubIssue -Title "[DEVOPS] No CI/CD pipeline" -Labels @("devops", "medium-priority") -Body @"
Missing automated testing and deployment pipeline.

**Impact:**
- Manual testing required
- Deployment errors
- Poor development workflow

**Solution:**
- Implement GitHub Actions
- Add automated testing
- Create deployment pipeline

**Files Affected:** CI/CD configuration
"@

Create-GitHubIssue -Title "[DEVOPS] Single environment configuration" -Labels @("devops", "low-priority") -Body @"
Single application.properties file for all environments.

**Impact:**
- Environment-specific issues
- Configuration management problems
- Security concerns

**Solution:**
- Create environment-specific configs
- Use Spring profiles
- Implement proper configuration management

**Files Affected:** Application configuration files
"@

Write-Host "All issues have been created successfully!" -ForegroundColor Green
Write-Host "You can view them at: https://github.com/$Repository/issues" -ForegroundColor Cyan 