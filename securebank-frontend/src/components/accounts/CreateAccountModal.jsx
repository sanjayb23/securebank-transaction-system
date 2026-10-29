import { useState } from 'react';
import Modal from '../common/Modal';
import Button from '../common/Button';
import Input from '../common/Input';
import { accountService } from '../../services/accountService';

const CreateAccountModal = ({ isOpen, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    accountType: 'SAVINGS',
    initialDeposit: '',
    acceptTerms: false
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.acceptTerms) {
      setError('Please accept the terms and conditions');
      return;
    }
    
    setLoading(true);
    setError('');

    try {
      await accountService.createAccount({
        accountType: formData.accountType,
        initialDeposit: formData.initialDeposit ? parseFloat(formData.initialDeposit) : 0
      });
      onSuccess();
      onClose();
      setFormData({ accountType: 'SAVINGS', initialDeposit: '', acceptTerms: false });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create account');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Create New Account"
      footer={
        <div className="flex space-x-3">
          <Button variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" form="create-account-form" loading={loading}>
            Create Account
          </Button>
        </div>
      }
    >
      <form id="create-account-form" onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Account Type
          </label>
          <div className="space-y-2">
            <label className="flex items-center">
              <input
                type="radio"
                name="accountType"
                value="SAVINGS"
                checked={formData.accountType === 'SAVINGS'}
                onChange={(e) => setFormData({...formData, accountType: e.target.value})}
                className="mr-2"
              />
              Savings Account
            </label>
            <label className="flex items-center">
              <input
                type="radio"
                name="accountType"
                value="CURRENT"
                checked={formData.accountType === 'CURRENT'}
                onChange={(e) => setFormData({...formData, accountType: e.target.value})}
                className="mr-2"
              />
              Current Account
            </label>
          </div>
        </div>

        <Input
          label="Initial Deposit (Optional)"
          type="number"
          min="0"
          step="0.01"
          value={formData.initialDeposit}
          onChange={(e) => setFormData({...formData, initialDeposit: e.target.value})}
          placeholder="Enter initial deposit amount"
        />

        <div className="flex items-start space-x-2">
          <input
            type="checkbox"
            id="acceptTerms"
            checked={formData.acceptTerms}
            onChange={(e) => setFormData({...formData, acceptTerms: e.target.checked})}
            className="mt-1 h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label htmlFor="acceptTerms" className="text-sm text-gray-700">
            I accept the terms and conditions for opening a bank account
          </label>
        </div>

        {error && (
          <div className="text-red-600 text-sm">{error}</div>
        )}
      </form>
    </Modal>
  );
};

export default CreateAccountModal;