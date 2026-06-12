import { useState, useEffect } from 'react';
import { Search, Filter, Lock, Unlock } from 'lucide-react';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ConfirmModal from '../components/common/ConfirmModal';
import { adminService } from '../services/adminService';
import { formatCurrency, formatAccountNumber } from '../utils/formatters';

const AdminAccounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [actionType, setActionType] = useState('');

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const response = await adminService.getAllAccounts({
        search: searchTerm,
        status: statusFilter
      });
      setAccounts(response.data.data.content || response.data.data || []);
    } catch (err) {
      console.error('Failed to fetch accounts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFreezeAccount = (account) => {
    setSelectedAccount(account);
    setActionType('freeze');
    setShowConfirmModal(true);
  };

  const handleUnfreezeAccount = (account) => {
    setSelectedAccount(account);
    setActionType('unfreeze');
    setShowConfirmModal(true);
  };

  const confirmAction = async () => {
    try {
      if (actionType === 'freeze') {
        await adminService.freezeAccount(selectedAccount.id);
      } else {
        await adminService.unfreezeAccount(selectedAccount.id);
      }
      
      // Update local state
      setAccounts(accounts.map(acc => 
        acc.id === selectedAccount.id 
          ? { ...acc, status: actionType === 'freeze' ? 'FROZEN' : 'ACTIVE' }
          : acc
      ));
      
      setShowConfirmModal(false);
      setSelectedAccount(null);
    } catch (err) {
      console.error(`Failed to ${actionType} account:`, err);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'FROZEN': return 'bg-red-100 text-red-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const filteredAccounts = accounts.filter(account => {
    const matchesSearch = account.accountNumber.includes(searchTerm) || 
                         account.user?.fullName?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = !statusFilter || account.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  if (loading) {
    return <LoadingSpinner size="large" text="Loading accounts..." />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Account Management</h1>
        <Button onClick={fetchAccounts}>Refresh</Button>
      </div>

      {/* Filters */}
      <Card>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Input
            label="Search"
            type="text"
            placeholder="Account number or user name"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            icon={Search}
          />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="">All Status</option>
              <option value="ACTIVE">Active</option>
              <option value="FROZEN">Frozen</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>
          <div className="flex items-end">
            <Button onClick={fetchAccounts} className="w-full">
              <Filter size={16} className="mr-2" />
              Apply Filters
            </Button>
          </div>
        </div>
      </Card>

      {/* Accounts Table */}
      <Card>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Account</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">User</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Balance</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredAccounts.map((account) => (
                <tr key={account.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="font-mono text-sm">
                      {formatAccountNumber(account.accountNumber)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">
                      {account.user?.fullName || 'N/A'}
                    </div>
                    <div className="text-sm text-gray-500">
                      {account.user?.email || 'N/A'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {account.accountType}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    {formatCurrency(account.balance)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(account.status)}`}>
                      {account.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                    {account.status === 'ACTIVE' && (
                      <Button
                        variant="outline"
                        size="small"
                        onClick={() => handleFreezeAccount(account)}
                        className="text-red-600 hover:text-red-700"
                      >
                        <Lock size={14} className="mr-1" />
                        Freeze
                      </Button>
                    )}
                    {account.status === 'FROZEN' && (
                      <Button
                        variant="outline"
                        size="small"
                        onClick={() => handleUnfreezeAccount(account)}
                        className="text-green-600 hover:text-green-700"
                      >
                        <Unlock size={14} className="mr-1" />
                        Unfreeze
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {filteredAccounts.length === 0 && (
          <div className="text-center py-8">
            <p className="text-gray-500">No accounts found</p>
          </div>
        )}
      </Card>

      {/* Confirmation Modal */}
      <ConfirmModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={confirmAction}
        title={`${actionType === 'freeze' ? 'Freeze' : 'Unfreeze'} Account`}
        message={`Are you sure you want to ${actionType} account ${selectedAccount?.accountNumber}? ${
          actionType === 'freeze' 
            ? 'The user will not be able to perform any transactions.' 
            : 'The user will be able to perform transactions again.'
        }`}
        confirmText={actionType === 'freeze' ? 'Freeze Account' : 'Unfreeze Account'}
        confirmVariant={actionType === 'freeze' ? 'danger' : 'primary'}
      />
    </div>
  );
};

export default AdminAccounts;