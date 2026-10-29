import api from './api';

export const accountService = {
  getMyAccounts: () => api.get('/accounts'),
  getAccountById: (id) => api.get(`/accounts/${id}`),
  getAccountBalance: (id) => api.get(`/accounts/${id}/balance`),
  getAccountStatement: (id, params) => api.get(`/accounts/${id}/statement`, { params }),
  createAccount: (data) => api.post('/accounts', data),
  updateAccountStatus: (id, status) => api.patch(`/accounts/${id}/status`, { status }),
};