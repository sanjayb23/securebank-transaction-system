import { useState } from 'react';
import { Plus, Wallet } from 'lucide-react';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import AccountCard from '../components/accounts/AccountCard';
import CreateAccountModal from '../components/accounts/CreateAccountModal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { useAccounts } from '../hooks/useAccounts';

const Accounts = () => {
  const { accounts, loading, refetch } = useAccounts();
  const [showCreateModal, setShowCreateModal] = useState(false);



  if (loading) {
    return <LoadingSpinner size="large" text="Loading accounts..." />;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Accounts</h1>
          <p className="text-gray-600">Manage your bank accounts</p>
        </div>
        <Button onClick={() => setShowCreateModal(true)}>
          <Plus size={16} className="mr-2" />
          Create Account
        </Button>
      </div>

      {accounts.length === 0 ? (
        <Card>
          <div className="text-center py-12">
            <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Wallet className="h-8 w-8 text-gray-400" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No accounts yet</h3>
            <p className="text-gray-500 mb-6">Create your first account to start banking with SecureBank</p>
            <Button onClick={() => setShowCreateModal(true)}>
              <Plus size={16} className="mr-2" />
              Create Your First Account
            </Button>
          </div>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {accounts.map((account) => (
            <AccountCard
              key={account.id}
              account={account}
            />
          ))}
        </div>
      )}

      <CreateAccountModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={refetch}
      />
    </div>
  );
};

export default Accounts;