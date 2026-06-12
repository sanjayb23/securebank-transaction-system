import api from './api';

export const adminService = {
  getDashboardMetrics: () => api.get('/admin/dashboard'),
  getAllUsers: (params) => api.get('/admin/users', { params }),
  getAllAccounts: (params) => api.get('/admin/accounts', { params }),
  getAllTransactions: (params) => api.get('/admin/transactions', { params }),
  freezeAccount: (accountId) => api.post(`/admin/accounts/${accountId}/freeze`),
  unfreezeAccount: (accountId) => api.post(`/admin/accounts/${accountId}/unfreeze`),
  getAuditLogs: (params) => api.get('/admin/audit-logs', { params }),
  getDailyReport: (date) => api.get('/admin/reports/daily', { params: { date } }),
};