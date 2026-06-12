import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorMessage from '../common/ErrorMessage';

const LoginForm = ({ onSubmit, loading, error }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Input
        label="Username"
        type="text"
        required
        value={formData.username}
        onChange={(e) => setFormData({...formData, username: e.target.value})}
        placeholder="Enter your username"
      />
      
      <div className="relative">
        <Input
          label="Password"
          type={showPassword ? 'text' : 'password'}
          required
          value={formData.password}
          onChange={(e) => setFormData({...formData, password: e.target.value})}
          placeholder="Enter your password"
        />
        <button
          type="button"
          className="absolute right-3 top-8 text-gray-400 hover:text-gray-600"
          onClick={() => setShowPassword(!showPassword)}
        >
          {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
        </button>
      </div>

      <ErrorMessage message={error} />

      <Button type="submit" loading={loading} className="w-full" size="large">
        Sign In
      </Button>
    </form>
  );
};

export default LoginForm;