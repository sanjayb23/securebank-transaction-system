import { useState, useEffect } from 'react';
import { Users, CreditCard, TrendingUp, Activity, DollarSign, AlertTriangle } from 'lucide-react';
import Card from '../components/common/Card';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { formatCurrency } from '../utils/formatters';
import { adminService } from '../services/adminService';

const AdminDashboard = () => {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardMetrics();
  }, []);

  const fetchDashboardMetrics = async () => {
    try {
      setLoading(true);
      const response = await adminService.getDashboardMetrics();
      setMetrics(response.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner size="large" text="Loading admin dashboard..." />;
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <AlertTriangle className="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p className="text-red-600 mb-4">{error}</p>
      </div>
    );
  }

  const StatCard = ({ title, value, icon: Icon, color = 'blue', subtitle }) => (
    <Card>
      <div className="flex items-center">
        <div className={`p-3 rounded-full bg-${color}-100`}>
          <Icon className={`h-6 w-6 text-${color}-600`} />
        </div>
        <div className="ml-4">
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="text-2xl font-bold text-gray-900">{value}</p>
          {subtitle && <p className="text-sm text-gray-500">{subtitle}</p>}
        </div>
      </div>
    </Card>
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
        <button
          onClick={fetchDashboardMetrics}
          className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
        >
          Refresh
        </button>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Users"
          value={metrics.totalUsers.toLocaleString()}
          icon={Users}
          color="blue"
          subtitle={`+${metrics.newUsersThisWeek} this week`}
        />
        <StatCard
          title="Total Accounts"
          value={metrics.totalAccounts.toLocaleString()}
          icon={CreditCard}
          color="green"
          subtitle={`${metrics.activeAccounts} active`}
        />
        <StatCard
          title="Today's Volume"
          value={formatCurrency(metrics.todayTransactionVolume)}
          icon={DollarSign}
          color="purple"
          subtitle={`${metrics.todayTransactionCount} transactions`}
        />
        <StatCard
          title="System Health"
          value={`${Math.round((metrics.activeAccounts / metrics.totalAccounts) * 100)}%`}
          icon={Activity}
          color="emerald"
          subtitle="Active accounts"
        />
      </div>

      {/* Account Status Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card header={<h3 className="text-lg font-medium">Account Status</h3>}>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-green-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Active</span>
              </div>
              <span className="text-sm text-gray-600">{metrics.activeAccounts}</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-red-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Frozen</span>
              </div>
              <span className="text-sm text-gray-600">{metrics.frozenAccounts}</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-gray-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Closed</span>
              </div>
              <span className="text-sm text-gray-600">{metrics.closedAccounts}</span>
            </div>
          </div>
        </Card>

        <Card header={<h3 className="text-lg font-medium">Today's Transactions</h3>}>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-green-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Deposits</span>
              </div>
              <div className="text-right">
                <div className="text-sm font-medium">{formatCurrency(metrics.todayDeposits)}</div>
                <div className="text-xs text-gray-500">{metrics.depositCount} transactions</div>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-red-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Withdrawals</span>
              </div>
              <div className="text-right">
                <div className="text-sm font-medium">{formatCurrency(metrics.todayWithdrawals)}</div>
                <div className="text-xs text-gray-500">{metrics.withdrawalCount} transactions</div>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="w-3 h-3 bg-blue-500 rounded-full mr-3"></div>
                <span className="text-sm font-medium">Transfers</span>
              </div>
              <div className="text-right">
                <div className="text-sm font-medium">{formatCurrency(metrics.todayTransfers)}</div>
                <div className="text-xs text-gray-500">{metrics.transferCount} transactions</div>
              </div>
            </div>
          </div>
        </Card>
      </div>

      {/* Daily Volume Chart */}
      <Card header={<h3 className="text-lg font-medium">Daily Transaction Volume (Last 7 Days)</h3>}>
        <div className="mt-4">
          <div className="flex items-end space-x-2 h-40">
            {Object.entries(metrics.dailyVolume).map(([date, volume]) => {
              const maxVolume = Math.max(...Object.values(metrics.dailyVolume));
              const height = maxVolume > 0 ? (volume / maxVolume) * 100 : 0;
              
              return (
                <div key={date} className="flex-1 flex flex-col items-center">
                  <div
                    className="w-full bg-primary-500 rounded-t"
                    style={{ height: `${height}%`, minHeight: '4px' }}
                    title={`${date}: ${formatCurrency(volume)}`}
                  ></div>
                  <div className="text-xs text-gray-500 mt-2 transform -rotate-45 origin-left">
                    {new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AdminDashboard;