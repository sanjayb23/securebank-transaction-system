import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import PrivateRoute from './components/auth/PrivateRoute';
import Layout from './components/layout/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Accounts from './pages/Accounts';
import AccountDetails from './components/accounts/AccountDetails';
import Transactions from './pages/Transactions';
import NewTransaction from './pages/NewTransaction';
import AdminDashboard from './pages/AdminDashboard';
import AdminAccounts from './pages/AdminAccounts';
import AdminTransactions from './pages/AdminTransactions';
import './index.css';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('App Error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '20px', textAlign: 'center' }}>
          <h1>Something went wrong.</h1>
          <p>{this.state.error?.message}</p>
          <button onClick={() => window.location.reload()}>Reload Page</button>
        </div>
      );
    }
    return this.props.children;
  }
}

function App() {
  console.log('App component rendering');
  
  return (
    <ErrorBoundary>
      <AuthProvider>
        <Router>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={<Navigate to="/dashboard" />} />
            <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Layout>
                    <Dashboard />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/accounts"
              element={
                <PrivateRoute>
                  <Layout>
                    <Accounts />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/accounts/:id"
              element={
                <PrivateRoute>
                  <Layout>
                    <AccountDetails />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/transactions"
              element={
                <PrivateRoute>
                  <Layout>
                    <Transactions />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/new-transaction"
              element={
                <PrivateRoute>
                  <Layout>
                    <NewTransaction />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <PrivateRoute>
                  <Layout>
                    <AdminDashboard />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/admin/accounts"
              element={
                <PrivateRoute>
                  <Layout>
                    <AdminAccounts />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/admin/transactions"
              element={
                <PrivateRoute>
                  <Layout>
                    <AdminTransactions />
                  </Layout>
                </PrivateRoute>
              }
            />
          </Routes>
        </Router>
      </AuthProvider>
    </ErrorBoundary>
  );
}

export default App;