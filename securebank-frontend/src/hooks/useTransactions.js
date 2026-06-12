import { useState, useEffect } from 'react';
import { transactionService } from '../services/transactionService';

export const useTransactions = (params = {}) => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const queryParams = { ...params };
      
      // Convert filter object to query parameters
      if (params.startDate) queryParams.startDate = params.startDate;
      if (params.endDate) queryParams.endDate = params.endDate;
      if (params.type) queryParams.type = params.type;
      if (params.status) queryParams.status = params.status;
      if (params.minAmount) queryParams.minAmount = params.minAmount;
      if (params.maxAmount) queryParams.maxAmount = params.maxAmount;
      
      const response = await transactionService.getMyTransactions(queryParams);
      const data = response.data.data;
      // Handle paginated response
      setTransactions(data.content || data || []);
      setError(null);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch transactions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, []);

  return { transactions, loading, error, refetch: fetchTransactions };
};