# Frontend Documentation

## Overview
This document covers the frontend of FunBankApp, built with React and Vite.

## Setup
1. Install Node.js (LTS) and npm
2. Navigate to `frontend/`
3. Install dependencies:
   ```sh
   npm install
   ```
4. Start the dev server:
   ```sh
   npm run dev
   ```
5. The app runs at `http://localhost:5173`

## Architecture
- **Framework:** React
- **Build Tool:** Vite
- **UI Library:** Material UI
- **Forms:** React Hook Form, Yup
- **Routing:** React Router
- **State:** React Context (optionally React Query)

## Key Files
- `src/pages/` — Page components
- `src/components/` — Reusable UI
- `src/api/` — API utilities
- `src/context/` — Auth context

## Useful Links
- [React Docs](https://react.dev/)
- [Vite Docs](https://vitejs.dev/guide/)
- [Material UI Docs](https://mui.com/)
- [React Hook Form Docs](https://react-hook-form.com/)
- [Yup Docs](https://github.com/jquense/yup)
- [React Router Docs](https://reactrouter.com/)

## Usage
- Register/login
- View/manage accounts
- Deposit, withdraw, transfer
- Financial analysis (planned)

## Testing
- Run tests: `npm test`
- E2E: `npx cypress open` 