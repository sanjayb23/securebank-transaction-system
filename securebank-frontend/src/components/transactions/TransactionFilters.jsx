import { useState } from 'react';
import Button from '../common/Button';
import Input from '../common/Input';

const TransactionFilters = ({ onFilter, onReset }) => {
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    type: '',
    status: '',
    minAmount: '',
    maxAmount: ''
  });

  const handleApplyFilters = () => {
    onFilter(filters);
  };

  const handleResetFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      type: '',
      status: '',
      minAmount: '',
      maxAmount: ''
    });
    onReset();
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow border border-gray-200">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Filter Transactions</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <Input
          label="Start Date"
          type="date"
          value={filters.startDate}
          onChange={(e) => setFilters({...filters, startDate: e.target.value})}
        />
        
        <Input
          label="End Date"
          type="date"
          value={filters.endDate}
          onChange={(e) => setFilters({...filters, endDate: e.target.value})}
        />
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Transaction Type
          </label>
          <select
            value={filters.type}
            onChange={(e) => setFilters({...filters, type: e.target.value})}
            className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="">All Types</option>
            <option value="DEPOSIT">Deposit</option>
            <option value="WITHDRAW">Withdraw</option>
            <option value="TRANSFER">Transfer</option>
          </select>
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Status
          </label>
          <select
            value={filters.status}
            onChange={(e) => setFilters({...filters, status: e.target.value})}
            className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="">All Status</option>
            <option value="COMPLETED">Completed</option>
            <option value="PENDING">Pending</option>
            <option value="FAILED">Failed</option>
          </select>
        </div>
        
        <Input
          label="Min Amount"
          type="number"
          min="0"
          step="0.01"
          value={filters.minAmount}
          onChange={(e) => setFilters({...filters, minAmount: e.target.value})}
          placeholder="0.00"
        />
        
        <Input
          label="Max Amount"
          type="number"
          min="0"
          step="0.01"
          value={filters.maxAmount}
          onChange={(e) => setFilters({...filters, maxAmount: e.target.value})}
          placeholder="0.00"
        />
      </div>
      
      <div className="flex flex-wrap gap-2 mb-4">
        <Button 
          variant="outline" 
          size="small"
          onClick={() => {
            const today = new Date();
            const lastWeek = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
            setFilters({
              ...filters,
              startDate: lastWeek.toISOString().split('T')[0],
              endDate: today.toISOString().split('T')[0]
            });
          }}
        >
          Last 7 Days
        </Button>
        <Button 
          variant="outline" 
          size="small"
          onClick={() => {
            const today = new Date();
            const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
            setFilters({
              ...filters,
              startDate: lastMonth.toISOString().split('T')[0],
              endDate: today.toISOString().split('T')[0]
            });
          }}
        >
          Last 30 Days
        </Button>
        <Button 
          variant="outline" 
          size="small"
          onClick={() => setFilters({...filters, type: 'TRANSFER'})}
        >
          Transfers Only
        </Button>
        <Button 
          variant="outline" 
          size="small"
          onClick={() => setFilters({...filters, type: 'DEPOSIT'})}
        >
          Deposits Only
        </Button>
      </div>
      
      <div className="flex space-x-3 mt-6">
        <Button onClick={handleApplyFilters}>
          Apply Filters
        </Button>
        <Button variant="secondary" onClick={handleResetFilters}>
          Reset
        </Button>
      </div>
    </div>
  );
};

export default TransactionFilters;