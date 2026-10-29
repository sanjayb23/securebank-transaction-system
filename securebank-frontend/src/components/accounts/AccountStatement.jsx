import { useState, useEffect } from 'react';
import { Calendar, Filter, Download } from 'lucide-react';
import Card from '../common/Card';
import Button from '../common/Button';
import Input from '../common/Input';
import LoadingSpinner from '../common/LoadingSpinner';
import TransactionCard from '../transactions/TransactionCard';
import { accountService } from '../../services/accountService';

const AccountStatement = ({ accountId }) => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    type: ''
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0
  });

  useEffect(() => {
    fetchStatement();
  }, [accountId, pagination.page, filters]);

  const fetchStatement = async () => {
    try {
      setLoading(true);
      const params = {
        page: pagination.page,
        size: pagination.size,
        ...filters
      };
      const response = await accountService.getAccountStatement(accountId, params);
      const data = response.data.data;
      setTransactions(data.content || []);
      setPagination(prev => ({
        ...prev,
        totalPages: data.totalPages || 0,
        totalElements: data.totalElements || 0
      }));
    } catch (err) {
      console.error('Failed to fetch statement:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  return (
    <Card header={
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-medium">Account Statement</h3>
        <div className="flex space-x-2">
          <Button 
            variant="outline" 
            size="small"
            onClick={() => setShowFilters(!showFilters)}
          >
            <Filter size={16} className="mr-2" />
            Filters
          </Button>
          <Button variant="outline" size="small">
            <Download size={16} className="mr-2" />
            Export
          </Button>
        </div>
      </div>
    }>
      {showFilters && (
        <div className="mb-6 p-4 bg-gray-50 rounded-lg">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
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
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Transaction Type
              </label>
              <select
                value={filters.type}
                onChange={(e) => handleFilterChange('type', e.target.value)}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="">All Types</option>
                <option value="DEPOSIT">Deposit</option>
                <option value="WITHDRAW">Withdraw</option>
                <option value="TRANSFER">Transfer</option>
              </select>
            </div>
          </div>
        </div>
      )}

      {loading ? (
        <LoadingSpinner text="Loading transactions..." />
      ) : transactions.length === 0 ? (
        <div className="text-center py-8">
          <p className="text-gray-500">No transactions found</p>
        </div>
      ) : (
        <>
          <div className="space-y-3">
            {transactions.map((transaction) => (
              <TransactionCard key={transaction.id} transaction={transaction} />
            ))}
          </div>
          
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
                  onClick={() => handlePageChange(pagination.page - 1)}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="small"
                  disabled={pagination.page >= pagination.totalPages - 1}
                  onClick={() => handlePageChange(pagination.page + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </>
      )}
    </Card>
  );
};

export default AccountStatement;