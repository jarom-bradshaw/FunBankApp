## Goal
Build a personal finance dashboard with a CFP-style AI chat advisor, allowing users to enter and visualize their accounts, chat for advice, and see their net worth.

## Task List
- [X] Backend API for user authentication (JWT, Spring Boot, SQL). (backend/)
- [X] Backend API for chat/advice endpoint (LLM/AI integration). (backend/)
- [X] Backend API for account CRUD and data storage. (backend/)
- [X] User registration and login (JWT authentication). (backend/, frontend/)
- [X] Secure password hashing (BCrypt). (backend/)
- [ ] Implement chat interface for behavioral financial advice (CFP-style). (frontend/src/pages/Chat.jsx, components/)
- [ ] Store and display chat history for each user. (backend/, frontend/)
- [ ] Manual account entry and editing (balance, type, name). (frontend/src/pages/Accounts.jsx, backend/)
- [ ] Dashboard page to view accounts and net worth. (frontend/src/pages/Dashboard.jsx, backend/)
- [ ] Project documentation (README, setup, requirements, docs/)

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
- Fixed CORS issues and implemented proper authentication flow with protected routes
- Added AuthContext and ProtectedRoute components for secure navigation 