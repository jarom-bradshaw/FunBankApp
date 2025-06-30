import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const Dashboard = () => {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newAccount, setNewAccount] = useState({ name: '', balance: 0 });
  const [transaction, setTransaction] = useState({
    accountId: '',
    amount: '',
    description: ''
  });
  const [transfer, setTransfer] = useState({
    fromAccountId: '',
    toAccountId: '',
    amount: '',
    description: ''
  });

  useEffect(() => {
    console.log('Dashboard - useEffect triggered, isAuthenticated:', isAuthenticated());
    
    // Check if user is authenticated
    if (!isAuthenticated()) {
      console.log('Dashboard - user not authenticated, redirecting to login');
      navigate('/login');
      return;
    }

    console.log('Dashboard - user authenticated, fetching accounts');
    fetchAccounts();
  }, [isAuthenticated, navigate]);

  const fetchAccounts = async () => {
    try {
      console.log('Dashboard - making API call to fetch accounts');
      const res = await api.get('/accounts');
      console.log('Dashboard - accounts fetched successfully:', res.data);
      setAccounts(res.data);
      setError(''); // Clear any previous errors
    } catch (err) {
      console.log('Dashboard - fetchAccounts error:', err.response?.status, err.response?.data);
      if (err.response?.status === 401) {
        setError('Your session has expired. Please log in again.');
      } else {
      setError('Failed to load accounts');
      console.error(err);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAccount = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/accounts', newAccount);
      await fetchAccounts();
      setNewAccount({ name: '', balance: 0 });
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Your session has expired. Please log in again.');
      } else {
      setError('Failed to create account');
      console.error(err);
      }
    }
  };

  const handleDeposit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/accounts/deposit', transaction);
      await fetchAccounts();
      setTransaction({ accountId: '', amount: '', description: '' });
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Your session has expired. Please log in again.');
      } else {
      setError('Failed to deposit');
      console.error(err);
      }
    }
  };

  const handleWithdraw = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/accounts/withdraw', transaction);
      await fetchAccounts();
      setTransaction({ accountId: '', amount: '', description: '' });
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Your session has expired. Please log in again.');
      } else {
      setError('Failed to withdraw');
      console.error(err);
      }
    }
  };

  const handleTransfer = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/accounts/transfer', transfer);
      await fetchAccounts();
      setTransfer({ fromAccountId: '', toAccountId: '', amount: '', description: '' });
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Your session has expired. Please log in again.');
      } else {
      setError('Failed to transfer');
      console.error(err);
      }
    }
  };

  const handleLogout = async () => {
    try {
      // Call logout endpoint to clear cookie
      await api.post('/users/logout');
      console.log('Dashboard - logout successful, cookie cleared');
    } catch (err) {
      console.log('Dashboard - logout error:', err);
    } finally {
      // Clear local state
      logout();
    navigate('/login');
    }
  };

  if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold">FunBank Dashboard</h1>
            </div>
            <div className="flex items-center">
              <button
                onClick={handleLogout}
                className="ml-4 px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded flex justify-between items-center">
            <span>{error}</span>
            {error.includes('session has expired') && (
              <button
                onClick={() => navigate('/login')}
                className="px-3 py-1 bg-red-600 text-white rounded text-sm hover:bg-red-700"
              >
                Log In Again
              </button>
            )}
          </div>
        )}

        {/* Account List */}
        <div className="bg-white shadow rounded-lg p-6 mb-6">
          <h2 className="text-2xl font-bold mb-4">Your Accounts</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {accounts.map(account => (
              <div key={account.id} className="border rounded-lg p-4">
                <h3 className="font-semibold">{account.name}</h3>
                <p className="text-2xl font-bold">${account.balance.toFixed(2)}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Create Account Form */}
        <div className="bg-white shadow rounded-lg p-6 mb-6">
          <h2 className="text-2xl font-bold mb-4">Create New Account</h2>
          <form onSubmit={handleCreateAccount} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Account Name</label>
              <input
                type="text"
                value={newAccount.name}
                onChange={(e) => setNewAccount({ ...newAccount, name: e.target.value })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Initial Balance</label>
              <input
                type="number"
                step="0.01"
                value={newAccount.balance}
                onChange={(e) => setNewAccount({ ...newAccount, balance: parseFloat(e.target.value) })}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                required
              />
            </div>
            <button
              type="submit"
              className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Create Account
            </button>
          </form>
        </div>

        {/* Transaction Forms */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Deposit Form */}
          <div className="bg-white shadow rounded-lg p-6">
            <h2 className="text-2xl font-bold mb-4">Deposit</h2>
            <form onSubmit={handleDeposit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Account</label>
                <select
                  value={transaction.accountId}
                  onChange={(e) => setTransaction({ ...transaction, accountId: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
                  <option value="">Select Account</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>{account.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  value={transaction.amount}
                  onChange={(e) => setTransaction({ ...transaction, amount: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <input
                  type="text"
                  value={transaction.description}
                  onChange={(e) => setTransaction({ ...transaction, description: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
                <button
                  type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                >
                  Deposit
                </button>
            </form>
          </div>

          {/* Withdraw Form */}
          <div className="bg-white shadow rounded-lg p-6">
            <h2 className="text-2xl font-bold mb-4">Withdraw</h2>
            <form onSubmit={handleWithdraw} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Account</label>
                <select
                  value={transaction.accountId}
                  onChange={(e) => setTransaction({ ...transaction, accountId: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
                  <option value="">Select Account</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>{account.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  value={transaction.amount}
                  onChange={(e) => setTransaction({ ...transaction, amount: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <input
                  type="text"
                  value={transaction.description}
                  onChange={(e) => setTransaction({ ...transaction, description: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
              >
                Withdraw
              </button>
            </form>
          </div>
          </div>

          {/* Transfer Form */}
        <div className="bg-white shadow rounded-lg p-6 mt-6">
            <h2 className="text-2xl font-bold mb-4">Transfer Between Accounts</h2>
            <form onSubmit={handleTransfer} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">From Account</label>
                <select
                  value={transfer.fromAccountId}
                  onChange={(e) => setTransfer({ ...transfer, fromAccountId: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
                  <option value="">Select Account</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>{account.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">To Account</label>
                <select
                  value={transfer.toAccountId}
                  onChange={(e) => setTransfer({ ...transfer, toAccountId: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                >
                  <option value="">Select Account</option>
                  {accounts.map(account => (
                    <option key={account.id} value={account.id}>{account.name}</option>
                  ))}
                </select>
              </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  value={transfer.amount}
                  onChange={(e) => setTransfer({ ...transfer, amount: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <input
                  type="text"
                  value={transfer.description}
                  onChange={(e) => setTransfer({ ...transfer, description: e.target.value })}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                  required
                />
              </div>
              <button
                type="submit"
              className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Transfer
              </button>
            </form>
        </div>
      </main>
    </div>
  );
};

export default Dashboard; 