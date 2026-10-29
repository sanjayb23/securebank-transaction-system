import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorMessage from '../common/ErrorMessage';
import { isValidEmail, isValidPhone, isStrongPassword, getPasswordStrength } from '../../utils/validators';

const RegisterForm = ({ onSubmit, loading, error }) => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    username: '',
    password: '',
    confirmPassword: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.fullName.trim()) newErrors.fullName = 'Full name is required';
    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!isValidEmail(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }
    if (!formData.phone.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!isValidPhone(formData.phone)) {
      newErrors.phone = 'Phone must be 10 digits starting with 6-9';
    }
    if (!formData.username.trim()) {
      newErrors.username = 'Username is required';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Username must be at least 3 characters';
    }
    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (!isStrongPassword(formData.password)) {
      newErrors.password = 'Password must contain at least 8 characters with uppercase, lowercase, digit and special character';
    }
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <Input
        label="Full Name"
        type="text"
        required
        value={formData.fullName}
        onChange={(e) => setFormData({...formData, fullName: e.target.value})}
        error={errors.fullName}
      />
      
      <Input
        label="Email"
        type="email"
        required
        value={formData.email}
        onChange={(e) => setFormData({...formData, email: e.target.value})}
        error={errors.email}
      />
      
      <Input
        label="Phone"
        type="tel"
        required
        value={formData.phone}
        onChange={(e) => setFormData({...formData, phone: e.target.value})}
        error={errors.phone}
      />
      
      <Input
        label="Username"
        type="text"
        required
        value={formData.username}
        onChange={(e) => setFormData({...formData, username: e.target.value})}
        error={errors.username}
      />
      
      <div className="relative">
        <Input
          label="Password"
          type={showPassword ? 'text' : 'password'}
          required
          value={formData.password}
          onChange={(e) => setFormData({...formData, password: e.target.value})}
          error={errors.password}
        />
        <button
          type="button"
          className="absolute right-3 top-8 text-gray-400 hover:text-gray-600"
          onClick={() => setShowPassword(!showPassword)}
        >
          {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
        </button>
        {formData.password && (
          <div className="mt-2">
            <div className="flex items-center space-x-2">
              <div className="flex-1 bg-gray-200 rounded-full h-2">
                <div 
                  className={`h-2 rounded-full transition-all ${
                    getPasswordStrength(formData.password).color === 'red' ? 'bg-red-500 w-1/3' :
                    getPasswordStrength(formData.password).color === 'yellow' ? 'bg-yellow-500 w-2/3' :
                    'bg-green-500 w-full'
                  }`}
                ></div>
              </div>
              <span className={`text-sm font-medium ${
                getPasswordStrength(formData.password).color === 'red' ? 'text-red-600' :
                getPasswordStrength(formData.password).color === 'yellow' ? 'text-yellow-600' :
                'text-green-600'
              }`}>
                {getPasswordStrength(formData.password).strength}
              </span>
            </div>
          </div>
        )}
      </div>
      
      <Input
        label="Confirm Password"
        type="password"
        required
        value={formData.confirmPassword}
        onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
        error={errors.confirmPassword}
      />

      <ErrorMessage message={error} />

      <Button type="submit" loading={loading} className="w-full" size="large">
        Register
      </Button>
    </form>
  );
};

export default RegisterForm;