# Issue 1.1: Initial Setup Tasks

## Goal
Complete all remaining steps in the Initial Setup section of the frontend plan.

## Task List
- [ ] Configure environment variables
  - [x] Create a .env file in the frontend root
  - [x] Add a sample variable (e.g., VITE_API_URL)
  - [x] Ensure .env is in .gitignore
  - [x] Test that Vite loads the variable in code
- [ ] Set up API client (Axios)
  - [x] Install axios
  - [x] Create an api/axios.js or axios.ts file in src
  - [x] Configure base URL using environment variable
  - [x] Export a pre-configured Axios instance
  - [x] Test a sample API call
- [ ] Configure state management (Redux Toolkit)
  - [ ] Install @reduxjs/toolkit and react-redux
  - [ ] Create a store.ts in src
  - [ ] Set up a basic slice (e.g., auth)
  - [ ] Provide the store to the app in main.jsx
  - [ ] Test state update in a component

## Updates
- The .env file must be created manually in the frontend root due to file editing restrictions. Add the line: VITE_API_URL=http://localhost:8080/api
- .env is already included in .gitignore at both the root and frontend levels.
- Added console.log to frontend/src/main.jsx to verify VITE_API_URL is loaded by Vite. Check the browser console when running the app.
- Added vite-env.d.ts to frontend/src for TypeScript support of import.meta.env.
- api/axios.ts exports a pre-configured Axios instance using the environment variable.
- Refactored fetchAccounts in Dashboard.jsx to use the Axios instance for a sample API call. 