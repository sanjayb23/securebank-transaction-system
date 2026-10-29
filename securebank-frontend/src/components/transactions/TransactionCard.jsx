import { ArrowUpRight, ArrowDownLeft, ArrowLeftRight } from 'lucide-react';
import Card from '../common/Card';
import { formatCurrency, formatDate, formatTransactionId } from '../../utils/formatters';

const TransactionCard = ({ transaction, currentUserId }) => {
  const getTransactionIcon = (type) => {
    switch (type) {
      case 'DEPOSIT': return <ArrowDownLeft className="h-5 w-5 text-green-600" />;
      case 'WITHDRAW': return <ArrowUpRight className="h-5 w-5 text-red-600" />;
      case 'TRANSFER': return <ArrowLeftRight className="h-5 w-5 text-blue-600" />;
      default: return <ArrowLeftRight className="h-5 w-5 text-gray-600" />;
    }
  };

  const getAmountDisplay = () => {
    const { transactionType, amount, fromAccountNumber, toAccountNumber } = transaction;
    
    if (transactionType === 'DEPOSIT') {
      return { amount: `+${formatCurrency(amount)}`, color: 'text-green-600', label: 'Credit' };
    }
    
    if (transactionType === 'WITHDRAW') {
      return { amount: `-${formatCurrency(amount)}`, color: 'text-red-600', label: 'Debit' };
    }
    
    if (transactionType === 'TRANSFER') {
      // For transfers, show + for incoming, - for outgoing
      // This would need user context to determine direction
      return { amount: formatCurrency(amount), color: 'text-blue-600', label: 'Transfer' };
    }
    
    return { amount: formatCurrency(amount), color: 'text-gray-600', label: 'Transaction' };
  };

  const amountDisplay = getAmountDisplay();

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'FAILED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <Card>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-gray-100 rounded-lg">
            {getTransactionIcon(transaction.transactionType)}
          </div>
          <div>
            <p className="font-medium text-gray-900">{transaction.transactionType}</p>
            <p className="text-sm text-gray-500">
              {formatTransactionId(transaction.transactionId)}
            </p>
            <p className="text-xs text-gray-400">
              {formatDate(transaction.createdAt)}
            </p>
          </div>
        </div>
        
        <div className="text-right">
          <p className={`text-lg font-bold ${amountDisplay.color}`}>
            {amountDisplay.amount}
          </p>
          <p className="text-xs text-gray-500">{amountDisplay.label}</p>
          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(transaction.status)}`}>
            {transaction.status}
          </span>
        </div>
      </div>
      
      <div className="mt-3 pt-3 border-t border-gray-200">
        {transaction.description && (
          <p className="text-sm text-gray-600 mb-2">{transaction.description}</p>
        )}
        
        {transaction.transactionType === 'TRANSFER' && (
          <div className="text-xs text-gray-500">
            {transaction.fromAccountNumber && (
              <p>From: ****{transaction.fromAccountNumber.slice(-4)}</p>
            )}
            {transaction.toAccountNumber && (
              <p>To: ****{transaction.toAccountNumber.slice(-4)}</p>
            )}
          </div>
        )}
        
        {transaction.fee > 0 && (
          <p className="text-xs text-gray-500">Fee: {formatCurrency(transaction.fee)}</p>
        )}
      </div>
    </Card>
  );
};

export default TransactionCard;