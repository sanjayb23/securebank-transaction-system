import { useState } from 'react';
import { ArrowDownLeft, ArrowUpRight, ArrowLeftRight } from 'lucide-react';
import Card from '../components/common/Card';
import TransactionForm from '../components/transactions/TransactionForm';

const NewTransaction = () => {
  const [activeTab, setActiveTab] = useState('deposit');
  const [success, setSuccess] = useState(null);

  const tabs = [
    { id: 'deposit', label: 'Deposit', icon: ArrowDownLeft, color: 'text-green-600' },
    { id: 'withdraw', label: 'Withdraw', icon: ArrowUpRight, color: 'text-red-600' },
    { id: 'transfer', label: 'Transfer', icon: ArrowLeftRight, color: 'text-blue-600' }
  ];

  const handleSuccess = (response) => {
    setSuccess(`Transaction successful! Transaction ID: ${response.transactionId}`);
    setTimeout(() => setSuccess(null), 5000);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">New Transaction</h1>
        <p className="text-gray-600">Deposit, withdraw, or transfer funds</p>
      </div>

      {success && (
        <div className="bg-green-50 border border-green-200 rounded-md p-4">
          <p className="text-green-800">{success}</p>
        </div>
      )}

      <div className="bg-white rounded-lg shadow">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8 px-6">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`py-4 px-1 border-b-2 font-medium text-sm flex items-center space-x-2 ${
                  activeTab === tab.id
                    ? 'border-primary-500 text-primary-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <tab.icon size={16} className={activeTab === tab.id ? tab.color : ''} />
                <span>{tab.label}</span>
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          <TransactionForm type={activeTab} onSuccess={handleSuccess} />
        </div>
      </div>
    </div>
  );
};

export default NewTransaction;