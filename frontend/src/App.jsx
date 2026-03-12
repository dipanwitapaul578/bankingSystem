import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Deposit from './pages/Deposit';
import Withdraw from './pages/Withdraw';
import Transfer from './pages/Transfer';
import Transactions from './pages/Transactions';
import Admin from './pages/Admin';

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" />;
};

const AdminRoute = ({ children }) => {
  const role = localStorage.getItem('role');
  return role === 'ADMIN' ? children : <Navigate to="/dashboard" />;
};

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
      <Route path="/deposit" element={<PrivateRoute><Deposit /></PrivateRoute>} />
      <Route path="/withdraw" element={<PrivateRoute><Withdraw /></PrivateRoute>} />
      <Route path="/transfer" element={<PrivateRoute><Transfer /></PrivateRoute>} />
      <Route path="/transactions" element={<PrivateRoute><Transactions /></PrivateRoute>} />
      <Route path="/admin" element={<PrivateRoute><AdminRoute><Admin /></AdminRoute></PrivateRoute>} />
    </Routes>
  );
}