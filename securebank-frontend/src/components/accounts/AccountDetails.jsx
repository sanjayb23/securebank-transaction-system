import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Download, Plus, Minus, ArrowLeftRight } from 'lucide-react';
import Card from '../common/Card';
import Button from '../common/Button';
import LoadingSpinner from '../common/LoadingSpinner';
import AccountStatement from './AccountStatement';
import { accountService } from '../../services/accountService';
import { formatCurrency, formatAccountNumber } from '../../utils/formatters';

const AccountDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAccountDetails();
  }, [id]);

  const fetchAccountDetails = async () => {
    try {
      setLoading(true);
      const response = await accountService.getAccountById(id);
      setAccount(response.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch account details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner size="large" text="Loading account details..." />;
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600 mb-4">{error}</p>
        <Button onClick={() => navigate('/accounts')}>Back to Accounts</Button>
      </div>
    );
  }

  if (!account) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 mb-4">Account not found</p>
        <Button onClick={() => navigate('/accounts')}>Back to Accounts</Button>
      </div>
    );
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'FROZEN': return 'bg-red-100 text-red-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button 
            variant="outline" 
            onClick={() => navigate('/accounts')}
            className="p-2"
          >
            <ArrowLeft size={16} />
          </Button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Account Details</h1>
            <p className="text-gray-600">{account.accountType} Account</p>
          </div>
        </div>
        <span className={`px-3 py-1 text-sm font-medium rounded-full ${getStatusColor(account.status)}`}>
          {account.status}
        </span>
      </div>

      {/* Account Overview */}
      <Card>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4">Account Information</h3>
            <div className="space-y-3">
              <div>
                <p className="text-sm text-gray-500">Account Number</p>
                <p className="font-mono text-lg">{formatAccountNumber(account.accountNumber)}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Account Type</p>
                <p className="font-medium">{account.accountType}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Currency</p>
                <p className="font-medium">{account.currency}</p>
              </div>
            </div>
          </div>
          
          <div>
            <h3 className="text-lg font-medium text-gray-900 mb-4">Current Balance</h3>
            <div className="text-center p-6 bg-primary-50 rounded-lg">
              <p className="text-3xl font-bold text-primary-600">
                {formatCurrency(account.balance)}
              </p>
              <p className="text-sm text-gray-500 mt-2">Available Balance</p>
            </div>
          </div>
        </div>
      </Card>

      {/* Quick Actions */}
      <Card header={<h3 className="text-lg font-medium">Quick Actions</h3>}>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Button 
            onClick={() => navigate('/new-transaction?type=deposit&account=' + account.id)}
            className="flex items-center justify-center"
          >
            <Plus size={16} className="mr-2" />
            Deposit Money
          </Button>
          <Button 
            variant="secondary"
            onClick={() => navigate('/new-transaction?type=withdraw&account=' + account.id)}
            className="flex items-center justify-center"
          >
            <Minus size={16} className="mr-2" />
            Withdraw Money
          </Button>
          <Button 
            variant="outline"
            onClick={() => navigate('/new-transaction?type=transfer&account=' + account.id)}
            className="flex items-center justify-center"
          >
            <ArrowLeftRight size={16} className="mr-2" />
            Transfer Funds
          </Button>
        </div>
      </Card>

      {/* Account Statement */}
      <AccountStatement accountId={account.id} />
    </div>
  );
};

export default AccountDetails;