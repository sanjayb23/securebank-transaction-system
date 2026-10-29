# Frontend Troubleshooting Guide

## Steps to Fix Blank White Screen

### 1. Check Browser Console
Open browser developer tools (F12) and check for:
- JavaScript errors in Console tab
- Network errors in Network tab
- Failed API calls

### 2. Test Basic React Functionality
Temporarily replace the import in `src/main.jsx`:
```javascript
// Change this line:
import App from './App.jsx'
// To this:
import App from './App-debug.jsx'
```

### 3. Check if Backend is Running
- Ensure backend is running on http://localhost:8080
- Test API endpoint: http://localhost:8080/api/auth/login

### 4. Clear Browser Cache
- Clear localStorage: `localStorage.clear()`
- Hard refresh: Ctrl+Shift+R

### 5. Reinstall Dependencies
```bash
cd securebank-frontend
npm install
npm run dev
```

### 6. Check Environment Variables
Ensure `.env` file contains:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 7. Common Issues
- Tailwind CSS not compiling
- Missing dependencies
- CORS issues with backend
- Authentication loop