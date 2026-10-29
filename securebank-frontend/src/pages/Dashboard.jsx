import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { CreditCard, TrendingUp, DollarSign, Clock } from 'lucide-react';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import LoadingSpinner from '../components/common/LoadingSpinner';
import Modal from '../components/common/Modal';
import TransactionForm from '../components/transactions/TransactionForm';
import { useAccounts } from '../hooks/useAccounts';
import { useTransactions } from '../hooks/useTransactions';
import { formatCurrency } from '../utils/formatters';

const Dashboard = () => {
  const navigate = useNavigate();
  const { accounts, loading: accountsLoading, refetch: refetchAccounts } = useAccounts();
  const { transactions, loading: transactionsLoading, refetch: refetchTransactions } = useTransactions();
  const [showTransactionModal, setShowTransactionModal] = useState(false);
  const [transactionType, setTransactionType] = useState('deposit');
  const [success, setSuccess] = useState(null);

  // Calculate dashboard stats from real data
  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);
  const totalAccounts = accounts.length;
  const recentTransactions = transactions.slice(0, 5).length;
  const pendingTransactions = transactions.filter(t => t.status === 'PENDING').length;

  // Auto-refresh data every 30 seconds for real-time updates
  useEffect(() => {
    const interval = setInterval(() => {
      refetchAccounts();
      refetchTransactions();
    }, 30000);

    return () => clearInterval(interval);
  }, [refetchAccounts, refetchTransactions]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Welcome back! Here's your account overview.</p>
      </div>

      {/* Stats Cards */}
      {accountsLoading || transactionsLoading ? (
        <div className="flex justify-center py-8">
          <LoadingSpinner />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card>
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-blue-100">
                <DollarSign className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Balance</p>
                <p className="text-2xl font-bold text-gray-900">
                  {formatCurrency(totalBalance)}
                </p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-green-100">
                <CreditCard className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Accounts</p>
                <p className="text-2xl font-bold text-gray-900">{totalAccounts}</p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-yellow-100">
                <TrendingUp className="h-6 w-6 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Recent Transactions</p>
                <p className="text-2xl font-bold text-gray-900">{recentTransactions}</p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-red-100">
                <Clock className="h-6 w-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Pending</p>
                <p className="text-2xl font-bold text-gray-900">{pendingTransactions}</p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* Success Message */}
      {success && (
        <div className="bg-green-50 border border-green-200 rounded-md p-4">
          <p className="text-green-800">{success}</p>
        </div>
      )}

      {/* Quick Actions */}
      <Card header={<h3 className="text-lg font-medium">Quick Actions</h3>}>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Button 
            className="w-full"
            onClick={() => {
              setTransactionType('deposit');
              setShowTransactionModal(true);
            }}
          >
            Deposit Money
          </Button>
          <Button 
            variant="secondary" 
            className="w-full"
            onClick={() => {
              setTransactionType('withdraw');
              setShowTransactionModal(true);
            }}
          >
            Withdraw Money
          </Button>
          <Button 
            variant="outline" 
            className="w-full"
            onClick={() => {
              setTransactionType('transfer');
              setShowTransactionModal(true);
            }}
          >
            Transfer Funds
          </Button>
        </div>
      </Card>

      {/* Recent Transactions */}
      <Card header={
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-medium">Recent Transactions</h3>
          <Button 
            variant="outline" 
            size="sm"
            onClick={() => navigate('/transactions')}
          >
            View All
          </Button>
        </div>
      }>
        {transactionsLoading ? (
          <div className="flex justify-center py-8">
            <LoadingSpinner />
          </div>
        ) : transactions.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">No transactions found</p>
          </div>
        ) : (
          <div className="space-y-3">
            {transactions.slice(0, 5).map((transaction) => (
              <div key={transaction.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">
                    {transaction.transactionType.charAt(0).toUpperCase() + transaction.transactionType.slice(1).toLowerCase()}
                  </p>
                  <p className="text-sm text-gray-500">
                    {new Date(transaction.createdAt).toLocaleDateString()}
                  </p>
                </div>
                <div className="text-right">
                  <p className={`font-medium ${
                    transaction.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                  }`}>
                    {transaction.transactionType === 'DEPOSIT' ? '+' : '-'}{formatCurrency(transaction.amount)}
                  </p>
                  <p className="text-sm text-gray-500">{transaction.status}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* Transaction Modal */}
      <Modal
        isOpen={showTransactionModal}
        onClose={() => setShowTransactionModal(false)}
        title={`${transactionType.charAt(0).toUpperCase() + transactionType.slice(1)} Money`}
      >
        <TransactionForm 
          type={transactionType}
          onSuccess={(response) => {
            setSuccess(`Transaction successful! Transaction ID: ${response.transactionId}`);
            setShowTransactionModal(false);
            // Refresh data
            refetchAccounts();
            refetchTransactions();
            setTimeout(() => setSuccess(null), 5000);
          }}
        />
      </Modal>
    </div>
  );
};

export default Dashboard;