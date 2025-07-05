# PowerShell script to create GitHub issues for FunBankApp backend
# Run this script after installing GitHub CLI

Write-Host "Creating GitHub issues for FunBankApp backend..." -ForegroundColor Green

# Check if GitHub CLI is installed
try {
    gh --version | Out-Null
    Write-Host "GitHub CLI is installed" -ForegroundColor Green
} catch {
    Write-Host "GitHub CLI is not installed. Please install it first:" -ForegroundColor Red
    Write-Host "Visit: https://cli.github.com/" -ForegroundColor Yellow
    exit 1
}

# Check authentication
Write-Host "Checking authentication..." -ForegroundColor Yellow
gh auth status

# Create critical security issues
Write-Host "Creating critical security issues..." -ForegroundColor Yellow

# Issue 1: Database credentials exposed
gh issue create `
  --repo jarom-bradshaw/FunBankApp `
  --title "[SECURITY] Database credentials exposed in application.properties" `
  --body "Database credentials are hardcoded in \`backend/src/main/resources/application.properties\`. This is a critical security vulnerability that exposes sensitive database access information.

**Impact:**
- Database credentials are visible in source code
- Potential unauthorized database access
- Violation of security best practices

**Solution:**
- Move credentials to environment variables
- Create separate configuration files for different environments
- Use Spring Boot's configuration properties

**Files Affected:** \`backend/src/main/resources/application.properties\`" `
  --label "security,critical,high-priority"

# Issue 2: JWT secret hardcoded
gh issue create `
  --repo jarom-bradshaw/FunBankApp `
  --title "[SECURITY] JWT secret hardcoded in application properties" `
  --body "JWT signing secret is hardcoded in the application properties file, making it vulnerable to exposure.

**Impact:**
- JWT tokens could be forged
- Authentication bypass possible
- Security compromise of user sessions

**Solution:**
- Externalize JWT secret to environment variables
- Generate cryptographically secure secrets
- Rotate secrets regularly

**Files Affected:** \`backend/src/main/resources/application.properties\`" `
  --label "security,critical,high-priority"

# Issue 3: CORS configuration too permissive
gh issue create `
  --repo jarom-bradshaw/FunBankApp `
  --title "[SECURITY] CORS configuration allows all origins" `
  --body "CORS configuration allows all origins with \`*\` pattern, which is too permissive for production.

**Impact:**
- Potential cross-origin attacks
- Security vulnerabilities in production
- Violation of security best practices

**Solution:**
- Restrict allowed origins to specific domains
- Implement proper CORS policies
- Use environment-specific configurations

**Files Affected:** \`backend/src/main/java/com/jarom/funbankapp/config/CorsConfig.java\`" `
  --label "security,high-priority"

# Issue 4: Missing rate limiting
gh issue create `
  --repo jarom-bradshaw/FunBankApp `
  --title "[SECURITY] Authentication endpoints lack rate limiting" `
  --body "Authentication endpoints (\`/api/auth/login\`, \`/api/auth/register\`) do not have rate limiting, making them vulnerable to brute force attacks.

**Impact:**
- Brute force attacks on login
- Account enumeration attacks
- Potential DoS attacks

**Solution:**
- Implement rate limiting for auth endpoints
- Use Spring Security rate limiting
- Add CAPTCHA for repeated failures

**Files Affected:** \`backend/src/main/java/com/jarom/funbankapp/security/SecurityConfig.java\`" `
  --label "security,medium-priority"

# Issue 5: Missing unit tests
gh issue create `
  --repo jarom-bradshaw/FunBankApp `
  --title "[BUG] No unit tests for application components" `
  --body "The application has test directories but lacks comprehensive unit tests for controllers, services, and repositories.

**Impact:**
- No confidence in code changes
- Potential regressions
- Difficult to maintain code quality

**Solution:**
- Write comprehensive unit tests
- Add integration tests
- Implement test coverage reporting

**Files Affected:** All test directories" `
  --label "bug,testing,medium-priority"

Write-Host "Creating labels..." -ForegroundColor Yellow

# Create essential labels
gh label create "security" --repo jarom-bradshaw/FunBankApp --color "d73a4a" --description "Security-related issues"
gh label create "critical" --repo jarom-bradshaw/FunBankApp --color "d73a4a" --description "Critical priority issues"
gh label create "high-priority" --repo jarom-bradshaw/FunBankApp --color "fbca04" --description "High priority issues"
gh label create "medium-priority" --repo jarom-bradshaw/FunBankApp --color "fef2c0" --description "Medium priority issues"
gh label create "low-priority" --repo jarom-bradshaw/FunBankApp --color "0e8a16" --description "Low priority issues"
gh label create "bug" --repo jarom-bradshaw/FunBankApp --color "d73a4a" --description "Bug reports"
gh label create "enhancement" --repo jarom-bradshaw/FunBankApp --color "1d76db" --description "Feature enhancements"
gh label create "testing" --repo jarom-bradshaw/FunBankApp --color "5319e7" --description "Testing-related issues"
gh label create "code-quality" --repo jarom-bradshaw/FunBankApp --color "666666" --description "Code quality improvements"
gh label create "architecture" --repo jarom-bradshaw/FunBankApp --color "1d76db" --description "Architecture changes"
gh label create "performance" --repo jarom-bradshaw/FunBankApp --color "fbca04" --description "Performance improvements"
gh label create "database" --repo jarom-bradshaw/FunBankApp --color "1d76db" --description "Database-related issues"
gh label create "devops" --repo jarom-bradshaw/FunBankApp --color "5319e7" --description "DevOps and deployment issues"

Write-Host "Listing created issues..." -ForegroundColor Yellow
gh issue list --repo jarom-bradshaw/FunBankApp

Write-Host "Done! Issues have been created successfully." -ForegroundColor Green
Write-Host "You can view them at: https://github.com/jarom-bradshaw/FunBankApp/issues" -ForegroundColor Cyan 