import { useState } from 'react';
import { Filter } from 'lucide-react';
import Button from '../components/common/Button';
import TransactionCard from '../components/transactions/TransactionCard';
import TransactionFilters from '../components/transactions/TransactionFilters';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useTransactions } from '../hooks/useTransactions';

const Transactions = () => {
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({});
  const { transactions, loading, refetch } = useTransactions(filters);

  const handleFilter = (newFilters) => {
    setFilters(newFilters);
    refetch();
  };

  const handleResetFilters = () => {
    setFilters({});
    refetch();
  };

  if (loading) {
    return <LoadingSpinner size="large" text="Loading transactions..." />;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Transaction History</h1>
          <p className="text-gray-600">View and filter your transactions</p>
        </div>
        <Button 
          variant="outline" 
          onClick={() => setShowFilters(!showFilters)}
        >
          <Filter size={16} className="mr-2" />
          {showFilters ? 'Hide Filters' : 'Show Filters'}
        </Button>
      </div>

      {showFilters && (
        <TransactionFilters
          onFilter={handleFilter}
          onReset={handleResetFilters}
        />
      )}

      {transactions.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500">No transactions found</p>
        </div>
      ) : (
        <div className="space-y-4">
          {transactions.map((transaction) => (
            <TransactionCard
              key={transaction.id}
              transaction={transaction}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default Transactions;