import { useState, useEffect } from 'react';
import { Search, Filter, Download, Eye } from 'lucide-react';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { adminService } from '../services/adminService';
import { formatCurrency, formatAccountNumber } from '../utils/formatters';

const AdminTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    type: '',
    status: '',
    username: '',
    accountNumber: '',
    minAmount: '',
    maxAmount: ''
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalPages: 0,
    totalElements: 0
  });

  useEffect(() => {
    fetchTransactions();
  }, [pagination.page]);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const params = {
        page: pagination.page,
        size: pagination.size,
        ...Object.fromEntries(Object.entries(filters).filter(([_, v]) => v !== ''))
      };
      
      const response = await adminService.getAllTransactions(params);
      const data = response.data.data;
      
      setTransactions(data.content || []);
      setPagination(prev => ({
        ...prev,
        totalPages: data.totalPages || 0,
        totalElements: data.totalElements || 0
      }));
    } catch (err) {
      console.error('Failed to fetch transactions:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const applyFilters = () => {
    setPagination(prev => ({ ...prev, page: 0 }));
    fetchTransactions();
  };

  const resetFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      type: '',
      status: '',
      username: '',
      accountNumber: '',
      minAmount: '',
      maxAmount: ''
    });
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const getTransactionIcon = (type) => {
    const iconClass = "h-4 w-4";
    switch (type) {
      case 'DEPOSIT': return <div className={`${iconClass} bg-green-500 rounded-full`}></div>;
      case 'WITHDRAW': return <div className={`${iconClass} bg-red-500 rounded-full`}></div>;
      case 'TRANSFER': return <div className={`${iconClass} bg-blue-500 rounded-full`}></div>;
      default: return <div className={`${iconClass} bg-gray-500 rounded-full`}></div>;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'FAILED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading && transactions.length === 0) {
    return <LoadingSpinner size="large" text="Loading transactions..." />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">All Transactions</h1>
        <div className="flex space-x-2">
          <Button variant="outline" onClick={() => {}}>
            <Download size={16} className="mr-2" />
            Export CSV
          </Button>
          <Button onClick={fetchTransactions}>Refresh</Button>
        </div>
      </div>

      {/* Advanced Filters */}
      <Card header={<h3 className="text-lg font-medium">Filters</h3>}>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Input
            label="Start Date"
            type="date"
            value={filters.startDate}
            onChange={(e) => handleFilterChange('startDate', e.target.value)}
          />
          <Input
            label="End Date"
            type="date"
            value={filters.endDate}
            onChange={(e) => handleFilterChange('endDate', e.target.value)}
          />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select
              value={filters.type}
              onChange={(e) => handleFilterChange('type', e.target.value)}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="">All Types</option>
              <option value="DEPOSIT">Deposit</option>
              <option value="WITHDRAW">Withdraw</option>
              <option value="TRANSFER">Transfer</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select
              value={filters.status}
              onChange={(e) => handleFilterChange('status', e.target.value)}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="">All Status</option>
              <option value="COMPLETED">Completed</option>
              <option value="PENDING">Pending</option>
              <option value="FAILED">Failed</option>
            </select>
          </div>
          <Input
            label="Username"
            type="text"
            placeholder="Search by username"
            value={filters.username}
            onChange={(e) => handleFilterChange('username', e.target.value)}
          />
          <Input
            label="Account Number"
            type="text"
            placeholder="Account number"
            value={filters.accountNumber}
            onChange={(e) => handleFilterChange('accountNumber', e.target.value)}
          />
          <Input
            label="Min Amount"
            type="number"
            placeholder="0.00"
            value={filters.minAmount}
            onChange={(e) => handleFilterChange('minAmount', e.target.value)}
          />
          <Input
            label="Max Amount"
            type="number"
            placeholder="0.00"
            value={filters.maxAmount}
            onChange={(e) => handleFilterChange('maxAmount', e.target.value)}
          />
        </div>
        <div className="flex space-x-3 mt-4">
          <Button onClick={applyFilters}>
            <Filter size={16} className="mr-2" />
            Apply Filters
          </Button>
          <Button variant="secondary" onClick={resetFilters}>
            Reset
          </Button>
        </div>
      </Card>

      {/* Transactions Table */}
      <Card>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Transaction</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">From Account</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">To Account</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {transactions.map((transaction) => (
                <tr key={transaction.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="font-mono text-sm font-medium">
                      {transaction.transactionId}
                    </div>
                    {transaction.description && (
                      <div className="text-sm text-gray-500 truncate max-w-xs">
                        {transaction.description}
                      </div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      {getTransactionIcon(transaction.transactionType)}
                      <span className="ml-2 text-sm font-medium">
                        {transaction.transactionType}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {transaction.fromAccountNumber ? 
                      formatAccountNumber(transaction.fromAccountNumber) : 
                      '-'
                    }
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {transaction.toAccountNumber ? 
                      formatAccountNumber(transaction.toAccountNumber) : 
                      '-'
                    }
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium">
                      {formatCurrency(transaction.amount)}
                    </div>
                    {transaction.fee > 0 && (
                      <div className="text-xs text-gray-500">
                        Fee: {formatCurrency(transaction.fee)}
                      </div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(transaction.status)}`}>
                      {transaction.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(transaction.createdAt).toLocaleString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <Button variant="outline" size="small">
                      <Eye size={14} className="mr-1" />
                      View
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {transactions.length === 0 && !loading && (
          <div className="text-center py-8">
            <p className="text-gray-500">No transactions found</p>
          </div>
        )}

        {/* Pagination */}
        {pagination.totalPages > 1 && (
          <div className="flex items-center justify-between mt-6 pt-4 border-t border-gray-200">
            <p className="text-sm text-gray-700">
              Showing {pagination.page * pagination.size + 1} to{' '}
              {Math.min((pagination.page + 1) * pagination.size, pagination.totalElements)} of{' '}
              {pagination.totalElements} transactions
            </p>
            <div className="flex space-x-2">
              <Button
                variant="outline"
                size="small"
                disabled={pagination.page === 0}
                onClick={() => setPagination(prev => ({ ...prev, page: prev.page - 1 }))}
              >
                Previous
              </Button>
              <Button
                variant="outline"
                size="small"
                disabled={pagination.page >= pagination.totalPages - 1}
                onClick={() => setPagination(prev => ({ ...prev, page: prev.page + 1 }))}
              >
                Next
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default AdminTransactions;