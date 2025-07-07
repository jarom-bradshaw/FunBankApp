# Project Structure

FunBankApp uses a monorepo layout for both backend and frontend.

## Root
- `backend/` — All backend (Java/Spring Boot) code and config
- `frontend/` — All frontend (React) code and config
- `docs/` — Project documentation
- `plan.md` — Project plan and checklist
- `.gitignore`, `README.md`, etc.

## Backend
- `src/main/java/com/jarom/funbankapp/`
  - `controller/` — REST controllers (AccountController.java, UserController.java)
  - `service/` — Service classes (FinancialAnalysisService.java)
  - `repository/` — Data access (AccountRepository.java, AccountDAO.java, TransactionDAO.java, UserDAO.java)
  - `model/` — Entity/model classes (Account.java, Transaction.java, User.java, LoginRequest.java)
  - `dto/` — Data Transfer Objects (TransferRequest.java, WithdrawRequest.java, DepositRequest.java)
  - `security/` — Security config and filters (SecurityConfig.java, JwtAuthFilter.java, JwtService.java)
  - `config/` — App/config classes (CorsConfig.java, JwtProperties.java)
- `build.gradle`, `settings.gradle`, `gradlew*`, `gradle/` — Build tools

## Frontend
- `src/` — React source code
- `package.json`, `vite.config.js`, etc.

## Navigation
- Use the docs in `docs/` for detailed setup and usage
- See `README.md` for a project overview 