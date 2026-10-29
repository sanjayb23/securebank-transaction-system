import { useState, useEffect } from 'react';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorMessage from '../common/ErrorMessage';
import { useAccounts } from '../../hooks/useAccounts';
import { transactionService } from '../../services/transactionService';
import { formatCurrency } from '../../utils/formatters';

const TransactionForm = ({ type, onSuccess }) => {
  const { accounts } = useAccounts();
  const [formData, setFormData] = useState({
    accountId: '',
    toAccountNumber: '',
    amount: '',
    description: ''
  });
  const [fees, setFees] = useState({ withdrawFee: 5, transferFee: 10 });
  const [calculatedFee, setCalculatedFee] = useState(0);
  const [totalDeduction, setTotalDeduction] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (formData.amount && parseFloat(formData.amount) > 0) {
      const amount = parseFloat(formData.amount);
      let fee = 0;
      
      if (type === 'withdraw') {
        fee = fees.withdrawFee;
      } else if (type === 'transfer') {
        fee = fees.transferFee;
      }
      
      setCalculatedFee(fee);
      setTotalDeduction(amount + fee);
    } else {
      setCalculatedFee(0);
      setTotalDeduction(0);
    }
  }, [formData.amount, type, fees]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      let response;
      switch (type) {
        case 'deposit':
          response = await transactionService.deposit({
            accountId: formData.accountId,
            amount: parseFloat(formData.amount),
            description: formData.description
          });
          break;
        case 'withdraw':
          response = await transactionService.withdraw({
            accountId: formData.accountId,
            amount: parseFloat(formData.amount),
            description: formData.description
          });
          break;
        case 'transfer':
          response = await transactionService.transfer({
            fromAccountId: formData.accountId,
            toAccountNumber: formData.toAccountNumber,
            amount: parseFloat(formData.amount),
            description: formData.description
          });
          break;
        default:
          throw new Error('Invalid transaction type');
      }
      
      onSuccess(response.data.data);
      setFormData({ accountId: '', toAccountNumber: '', amount: '', description: '' });
    } catch (err) {
      setError(err.response?.data?.message || `${type} failed`);
    } finally {
      setLoading(false);
    }
  };

  const selectedAccount = accounts.find(acc => acc.id === parseInt(formData.accountId));

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          {type === 'transfer' ? 'From Account' : 'Account'}
        </label>
        <select
          required
          value={formData.accountId}
          onChange={(e) => setFormData({...formData, accountId: e.target.value})}
          className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
        >
          <option value="">Select Account</option>
          {accounts.map((account) => (
            <option key={account.id} value={account.id}>
              {account.accountNumber} - ₹{account.balance}
            </option>
          ))}
        </select>
        {selectedAccount && type === 'withdraw' && (
          <p className="text-sm text-gray-500 mt-1">
            Available Balance: ₹{selectedAccount.balance}
          </p>
        )}
      </div>

      {type === 'transfer' && (
        <Input
          label="To Account Number"
          type="text"
          required
          value={formData.toAccountNumber}
          onChange={(e) => setFormData({...formData, toAccountNumber: e.target.value})}
          placeholder="Enter recipient account number"
        />
      )}

      <Input
        label="Amount"
        type="number"
        min="0.01"
        step="0.01"
        required
        value={formData.amount}
        onChange={(e) => setFormData({...formData, amount: e.target.value})}
        placeholder="Enter amount"
      />

      <Input
        label="Description (Optional)"
        type="text"
        value={formData.description}
        onChange={(e) => setFormData({...formData, description: e.target.value})}
        placeholder="Enter description"
      />

      {/* Fee and Balance Calculation */}
      {formData.amount && parseFloat(formData.amount) > 0 && (
        <div className="bg-gray-50 p-4 rounded-lg space-y-2">
          <div className="flex justify-between text-sm">
            <span>Amount:</span>
            <span>{formatCurrency(parseFloat(formData.amount))}</span>
          </div>
          {calculatedFee > 0 && (
            <div className="flex justify-between text-sm">
              <span>Fee:</span>
              <span>{formatCurrency(calculatedFee)}</span>
            </div>
          )}
          {(type === 'withdraw' || type === 'transfer') && (
            <div className="flex justify-between text-sm font-medium border-t pt-2">
              <span>Total Deduction:</span>
              <span>{formatCurrency(totalDeduction)}</span>
            </div>
          )}
          {selectedAccount && (type === 'withdraw' || type === 'transfer') && (
            <div className="flex justify-between text-sm">
              <span>Balance After:</span>
              <span className={selectedAccount.balance - totalDeduction < 500 ? 'text-red-600' : 'text-green-600'}>
                {formatCurrency(selectedAccount.balance - totalDeduction)}
              </span>
            </div>
          )}
        </div>
      )}

      <ErrorMessage message={error} />

      <Button type="submit" loading={loading} className="w-full">
        {type.charAt(0).toUpperCase() + type.slice(1)} Money
      </Button>
    </form>
  );
};

export default TransactionForm;