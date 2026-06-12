import { useState } from 'react';
import TransactionCard from './TransactionCard';
import TransactionFilters from './TransactionFilters';
import LoadingSpinner from '../common/LoadingSpinner';
import { useTransactions } from '../../hooks/useTransactions';

const TransactionHistory = () => {
  const [filters, setFilters] = useState({});
  const { transactions, loading, refetch } = useTransactions(filters);

  const handleFilter = (newFilters) => {
    setFilters(newFilters);
  };

  const handleResetFilters = () => {
    setFilters({});
  };

  if (loading) {
    return <LoadingSpinner size="large" text="Loading transactions..." />;
  }

  return (
    <div className="space-y-6">
      <TransactionFilters onFilter={handleFilter} onReset={handleResetFilters} />
      
      {transactions.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500">No transactions found</p>
        </div>
      ) : (
        <div className="space-y-4">
          {transactions.map((transaction) => (
            <TransactionCard key={transaction.id} transaction={transaction} />
          ))}
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;