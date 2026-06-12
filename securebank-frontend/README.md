# SecureBank Frontend

A modern React dashboard for the SecureBank banking API with clean UI, responsive design, and secure user authentication.

## 🚀 Features

- **Modern UI**: Clean, professional design with Tailwind CSS
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Secure Authentication**: JWT-based login system
- **Dashboard**: Overview of accounts, transactions, and quick actions
- **Account Management**: View accounts, balances, and statements
- **Transaction System**: Deposit, withdraw, and transfer funds
- **Admin Panel**: Administrative features for user and account management

## 🛠️ Tech Stack

- **React 18** - Modern React with hooks
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client for API calls
- **Lucide React** - Beautiful icons
- **React Query** - Data fetching and caching

## 📋 Prerequisites

- Node.js 16+ 
- npm or yarn
- SecureBank backend running on http://localhost:8080

## 🚀 Quick Start

1. **Install dependencies**
   ```bash
   npm install
   ```

2. **Start development server**
   ```bash
   npm run dev
   ```

3. **Open browser**
   Navigate to http://localhost:3000

## 🔧 Environment Variables

Create a `.env` file in the root directory:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🏗️ Project Structure

```
src/
├── components/
│   ├── layout/          # Layout components (Navbar, Sidebar)
│   ├── auth/            # Authentication components
│   ├── accounts/        # Account-related components
│   ├── transactions/    # Transaction components
│   ├── admin/           # Admin panel components
│   └── common/          # Reusable UI components
├── pages/               # Page components
├── services/            # API service layers
├── context/             # React contexts
├── hooks/               # Custom hooks
├── utils/               # Utility functions
└── App.jsx              # Main app component
```

## 🔐 Authentication

The app uses JWT tokens for authentication:
- Tokens are stored in localStorage
- Automatic token refresh on API calls
- Redirect to login on 401 errors
- Protected routes require authentication

## 🎨 Design System

### Colors
- **Primary**: Blue (#3b82f6)
- **Success**: Green (#10b981) 
- **Danger**: Red (#ef4444)
- **Warning**: Orange (#f59e0b)

### Components
- **Button**: Multiple variants and sizes
- **Input**: Form inputs with validation
- **Card**: Content containers
- **Modal**: Overlay dialogs

## 📱 Responsive Design

- **Mobile**: < 640px - Collapsible sidebar, touch-friendly
- **Tablet**: 640px - 1024px - Adapted layout
- **Desktop**: > 1024px - Full sidebar navigation

## 🧪 Testing Credentials

Use these credentials to test the application:

- **Username**: `newuser_6b6408ad`
- **Password**: `StrongPass!23`

## 🚀 Build for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## 📄 License

This project is licensed under the MIT License.