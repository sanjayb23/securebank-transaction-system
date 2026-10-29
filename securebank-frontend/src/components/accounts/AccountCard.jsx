import { CreditCard, Eye } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Card from '../common/Card';
import Button from '../common/Button';
import { formatCurrency, formatAccountNumber } from '../../utils/formatters';

const AccountCard = ({ account }) => {
  const navigate = useNavigate();
  
  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'FROZEN': return 'bg-red-100 text-red-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <Card className="hover:shadow-lg transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-primary-100 rounded-lg">
            <CreditCard className="h-6 w-6 text-primary-600" />
          </div>
          <div>
            <p className="text-sm text-gray-500">{account.accountType} Account</p>
            <p className="font-mono text-sm text-gray-900">
              {formatAccountNumber(account.accountNumber)}
            </p>
          </div>
        </div>
        <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(account.status)}`}>
          {account.status}
        </span>
      </div>
      
      <div className="mt-4">
        <p className="text-2xl font-bold text-gray-900">
          {formatCurrency(account.balance)}
        </p>
        <p className="text-sm text-gray-500">Current Balance</p>
      </div>
      
      <div className="mt-4">
        <Button 
          variant="outline" 
          size="small" 
          onClick={() => navigate(`/accounts/${account.id}`)}
          className="w-full"
        >
          <Eye size={16} className="mr-2" />
          View Details
        </Button>
      </div>
    </Card>
  );
};

export default AccountCard;