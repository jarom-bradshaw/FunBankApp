# Manual GitHub Issue Creation Guide

Since GitHub CLI installation had permission issues, here's how to create the critical issues manually through the GitHub web interface.

## 🚀 **Quick Start**

1. Go to: https://github.com/jarom-bradshaw/FunBankApp/issues
2. Click "New issue"
3. Use the templates below to create the critical issues

## 🔴 **Critical Security Issues to Create**

### Issue #1: Database Credentials Exposed

**Title:** `[SECURITY] Database credentials exposed in application.properties`

**Labels:** `security`, `critical`, `high-priority`

**Body:**
```
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
```

---

### Issue #2: JWT Secret Hardcoded

**Title:** `[SECURITY] JWT secret hardcoded in application properties`

**Labels:** `security`, `critical`, `high-priority`

**Body:**
```
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
```

---

### Issue #3: CORS Configuration Too Permissive

**Title:** `[SECURITY] CORS configuration allows all origins`

**Labels:** `security`, `high-priority`

**Body:**
```
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
```

---

### Issue #4: Missing Rate Limiting

**Title:** `[SECURITY] Authentication endpoints lack rate limiting`

**Labels:** `security`, `medium-priority`

**Body:**
```
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
```

---

### Issue #5: Missing Unit Tests

**Title:** `[BUG] No unit tests for application components`

**Labels:** `bug`, `testing`, `medium-priority`

**Body:**
```
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
```

## 🏷️ **Create Labels First**

Before creating issues, create these labels in your repository:

1. Go to: https://github.com/jarom-bradshaw/FunBankApp/labels
2. Click "New label"
3. Create these labels:

| Label Name | Color | Description |
|------------|-------|-------------|
| `security` | `#d73a4a` (red) | Security-related issues |
| `critical` | `#d73a4a` (red) | Critical priority issues |
| `high-priority` | `#fbca04` (orange) | High priority issues |
| `medium-priority` | `#fef2c0` (yellow) | Medium priority issues |
| `low-priority` | `#0e8a16` (green) | Low priority issues |
| `bug` | `#d73a4a` (red) | Bug reports |
| `enhancement` | `#1d76db` (blue) | Feature enhancements |
| `testing` | `#5319e7` (purple) | Testing-related issues |
| `code-quality` | `#666666` (gray) | Code quality improvements |
| `architecture` | `#1d76db` (blue) | Architecture changes |
| `performance` | `#fbca04` (orange) | Performance improvements |
| `database` | `#1d76db` (blue) | Database-related issues |
| `devops` | `#5319e7` (purple) | DevOps and deployment issues |

## 📋 **Step-by-Step Process**

### Step 1: Create Labels
1. Go to https://github.com/jarom-bradshaw/FunBankApp/labels
2. Create all the labels listed above

### Step 2: Create Critical Issues
1. Go to https://github.com/jarom-bradshaw/FunBankApp/issues
2. Click "New issue"
3. Copy and paste the content for each issue above
4. Add the appropriate labels
5. Submit the issue
6. Repeat for all 5 critical issues

### Step 3: Create Project Board (Optional)
1. Go to https://github.com/jarom-bradshaw/FunBankApp/projects
2. Click "New project"
3. Name it "FunBankApp Backend Issues"
4. Add columns: "To Do", "In Progress", "Review", "Done"
5. Add the created issues to the "To Do" column

## 🎯 **Priority Order**

Create the issues in this order:

1. **Database Credentials Exposed** (Critical)
2. **JWT Secret Hardcoded** (Critical)
3. **CORS Configuration Too Permissive** (High)
4. **Missing Rate Limiting** (Medium)
5. **Missing Unit Tests** (Medium)

## 🔧 **Alternative: Use the PowerShell Script**

If you can install GitHub CLI later, you can run the `create-issues.ps1` script I created:

```powershell
# Install GitHub CLI first
# Then run:
.\create-issues.ps1
```

## 📊 **Expected Results**

After creating these issues, you should have:
- ✅ 5 critical issues created
- ✅ 13 labels created
- ✅ Proper categorization and prioritization
- ✅ Clear action items for improvement

## 🚀 **Next Steps**

After creating the issues:
1. Start working on the critical security issues first
2. Set up environment variables for sensitive data
3. Implement proper CORS configuration
4. Add rate limiting to authentication endpoints
5. Begin writing unit tests

---

**Note:** This manual process ensures you have proper issue tracking even without GitHub CLI. The issues will help you systematically improve the security and quality of your FunBankApp backend. 