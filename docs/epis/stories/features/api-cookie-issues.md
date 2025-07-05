# Possible API Auth Issues: Cookie/Session Not Set or Not Sent

## Problem

- The frontend appears to log in successfully (shows success in the UI), but the backend does not log any API requests except for auth requests which do not require cookies.
- No session cookie is present in subsequent API requests, so the backend treats the user as unauthenticated.
- API calls (accounts, transactions, etc.) are not reaching the backend as authenticated requests.

---

## Possible Causes & Solutions

### 1. **Frontend: `withCredentials` Not Set on All Requests**
- **Problem:** Axios/fetch requests must include `withCredentials: true` to send cookies.
- **Solution:**
  - Ensure your API utility (e.g., `api.js`) sets `withCredentials: true` globally for all requests.
  - Double-check that no individual request overrides this setting.

### 2. **Frontend: Wrong API Base URL or Port**
- **Problem:** If the frontend is calling a different host/port than the backend, cookies will not be sent.
- **Solution:**
  - Make sure all API calls use the exact same host/port as the backend (e.g., `http://localhost:8080`).
  - Check for typos or mismatches in `VITE_API_BASE_URL` or similar env variables.

### 3. **Frontend: CORS Misconfiguration**
- **Problem:** If the backend CORS config does not allow credentials, cookies will not be set or sent.
- **Solution:**
  - Backend must set `Access-Control-Allow-Origin` to the exact frontend origin (not `*`).
  - Backend must set `Access-Control-Allow-Credentials: true`.
  - Example (Spring Boot):
    ```java
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true);
            }
        };
    }
    ```

### 4. **Frontend: Using HTTP and Backend on HTTPS (or vice versa)**
- **Problem:** Cookies are not shared between HTTP and HTTPS.
- **Solution:**
  - Make sure both frontend and backend use the same protocol (both HTTP or both HTTPS) during development.

### 5. **Frontend: Browser Blocking Third-Party Cookies**
- **Problem:** Some browsers block third-party cookies by default, especially in incognito/private mode.
- **Solution:**
  - Test in a regular browser window.
  - Check browser settings for cookie policies.

### 6. **Backend: Session Cookie Path/Domain Misconfiguration**
- **Problem:** Backend may set the cookie for a different path or domain, so browser does not send it.
- **Solution:**
  - Ensure the backend sets the cookie for the correct domain and path (usually `/`).
  - Check the `Set-Cookie` header in the login response.

### 7. **Backend: CORS Preflight Not Handled Properly**
- **Problem:** If the backend does not respond correctly to OPTIONS preflight requests, cookies may not be set.
- **Solution:**
  - Ensure backend handles OPTIONS requests and returns correct CORS headers.

### 8. **Backend: Session Not Persisted or Lost**
- **Problem:** Backend may not persist the session, or session is lost between requests (e.g., stateless server, misconfigured session store).
- **Solution:**
  - Check backend session configuration (Spring Boot: default is in-memory, but can be changed).
  - Ensure session is not lost between requests.

### 9. **Frontend: Not Using the Same Browser Tab/Window**
- **Problem:** Logging in in one tab and making API calls in another may not share cookies.
- **Solution:**
  - Always use the same tab/window for login and subsequent API calls.

### 10. **Frontend: Proxy Configuration Issues (Vite/React Dev Server)**
- **Problem:** If using a proxy, it may strip cookies or not forward them correctly.
- **Solution:**
  - Try disabling the proxy and calling the backend directly.
  - Check `vite.config.js` or `setupProxy.js` for correct configuration.

---

## How to Debug

1. **Check Network Tab:**
   - After login, inspect the response to `/api/auth/login`.
   - Look for a `Set-Cookie` header in the response.
   - For subsequent requests, check if the `Cookie` header is sent.
2. **Check Console for CORS Errors:**
   - Any CORS errors will prevent cookies from being set/sent.
3. **Check Backend Logs:**
   - Confirm if requests are reaching the backend and if the session is recognized.
4. **Test with Curl/Postman:**
   - Try logging in and making authenticated requests with cookies manually to isolate frontend vs backend issues.

---

## Summary Table

| Problem Area | Symptom | Solution |
|--------------|---------|----------|
| Frontend | No `withCredentials` | Set globally in API utility |
| Frontend | Wrong base URL | Match backend exactly |
| Frontend/Backend | CORS config | Allow credentials, set correct origin |
| Backend | Cookie path/domain | Set to `/` and correct domain |
| Backend | Session lost | Check session store/config |
| Browser | Third-party cookies blocked | Use regular window, check settings |
| Proxy | Proxy strips cookies | Call backend directly |

---

If you check each of these areas, you should be able to identify why cookies/sessions are not working between your frontend and backend. 