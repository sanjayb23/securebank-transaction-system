import api from './api';

export const transactionService = {
  deposit: (data) => api.post('/transactions/deposit', {
    accountId: data.accountId,
    amount: data.amount,
    description: data.description
  }),
  withdraw: (data) => api.post('/transactions/withdraw', {
    accountId: data.accountId,
    amount: data.amount,
    description: data.description
  }),
  transfer: (data) => api.post('/transactions/transfer', {
    fromAccountId: data.fromAccountId,
    toAccountNumber: data.toAccountNumber,
    amount: data.amount,
    description: data.description
  }),
  getMyTransactions: (params) => api.get('/transactions', { params }),
  getTransactionById: (id) => api.get(`/transactions/${id}`),
};