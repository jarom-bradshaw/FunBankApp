## Goal
Migrate from localStorage JWT tokens to HttpOnly cookies for stateless authentication, simplifying the frontend and improving security.

## Task List

### Backend Changes (Spring Boot)
- [ ] Update `UserController.java` to set HttpOnly cookies on login instead of returning JWT in response body
- [ ] Create new `CookieAuthFilter.java` to replace `JwtAuthFilter.java` for cookie-based authentication
- [ ] Update `SecurityConfig.java` to use cookie-based authentication and enable CSRF protection
- [ ] Add CSRF configuration and CSRF token endpoint in `SecurityConfig.java`
- [ ] Update `application.properties` to configure cookie settings (domain, secure, etc.)
- [ ] Add logout endpoint that clears the authentication cookie
- [ ] Update CORS configuration to allow credentials and handle cookie domains
- [ ] Test all protected endpoints work with cookie authentication

### Frontend Changes (React)
- [ ] Remove `AuthContext.jsx` - no longer needed for token management
- [ ] Simplify `ProtectedRoute.jsx` to only check if user is logged in (no token validation)
- [ ] Update `Login.jsx` to handle cookie-based authentication (remove token handling)
- [ ] Update `Dashboard.jsx` to remove token-related code and debug panel
- [ ] Update `axios.ts` to remove Authorization header interceptors and add `credentials: 'include'`
- [ ] Create new `AuthContext.jsx` for simple login state management (no tokens)
- [ ] Update all API calls to include `credentials: 'include'` for cookie transmission
- [ ] Add CSRF token handling for POST/PUT/DELETE requests
- [ ] Remove localStorage token management from all components
- [ ] Update error handling to work with cookie-based authentication

### Security Enhancements
- [ ] Configure HttpOnly, Secure, and SameSite cookie attributes
- [ ] Add CSRF protection with Spring Security
- [ ] Update CORS policy to restrict origins and allow credentials
- [ ] Add session timeout configuration
- [ ] Implement proper logout that clears cookies

### Testing & Validation
- [ ] Test login flow with cookies
- [ ] Test protected endpoints with cookie authentication
- [ ] Test logout functionality
- [ ] Test CSRF protection
- [ ] Test session expiration
- [ ] Verify no token management code remains in frontend
- [ ] Test cross-origin requests if needed

### Cleanup
- [ ] Remove unused JWT-related code from backend
- [ ] Remove token debugging and management code from frontend
- [ ] Update documentation to reflect cookie-based authentication
- [ ] Remove localStorage token storage and retrieval
- [ ] Clean up any remaining token-related error handling

## Implementation Order
1. Backend cookie authentication setup
2. Frontend authentication simplification
3. Security configuration
4. Testing and validation
5. Code cleanup

## Frontend Implementation Steps
- [ ] Build chat UI for advice (Chat.jsx, MessageList, MessageInput components). (frontend/src/pages/, components/)
- [ ] Integrate chat UI with backend chat endpoint. (frontend/src/api/)
- [ ] Display chat history for logged-in user. (frontend/src/pages/Chat.jsx)
- [ ] Build account management UI (add/edit/delete accounts, balances, types). (frontend/src/pages/Accounts.jsx, components/)
- [ ] Build dashboard page to visualize accounts and net worth. (frontend/src/pages/Dashboard.jsx, components/)
- [ ] Implement authentication context and protected routes. (frontend/src/context/, App.jsx)
- [ ] Build login and registration pages with form validation. (frontend/src/pages/Login.jsx, Register.jsx)
- [ ] Build profile page for account management. (frontend/src/pages/Profile.jsx)
- [ ] Ensure accessibility and responsive design. (frontend/src/components/)
- [ ] Add basic error handling and loading states. (frontend/src/components/)
- [ ] Write project documentation and update README. (README.md, docs/)

## Stretch Ideas
- Account linking/aggregation (Plaid, Yodlee, etc.)
- Monte Carlo simulations and financial projections
- Graphs/charts for net worth, spending, etc.
- Personalized insights based on account data
- Transaction import/history
- Goal tracking and planning
- Responsive, accessible UI with Material UI theming
- Dark/light mode toggle
- User profile management (change password, email, etc.)
- Email notifications for advice or reminders
- Admin dashboard for user/account management
- Multi-language support (i18n)
- End-to-end tests with Cypress
- CI/CD pipeline for automated testing and deployment
- OAuth/social login (Google, GitHub, etc.)
- Mobile app (React Native or similar)

## Updates
- MVP and plan updated to explicitly include backend API requirements for authentication, chat/advice, account CRUD, and data storage. 