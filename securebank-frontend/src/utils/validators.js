export const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

export const isValidPhone = (phone) => {
  return /^[6-9]\d{9}$/.test(phone);
};

export const isValidAccountNumber = (accountNumber) => {
  return /^\d{16}$/.test(accountNumber);
};

export const isValidAmount = (amount) => {
  return amount > 0 && /^\d+(\.\d{1,2})?$/.test(amount);
};

export const isStrongPassword = (password) => {
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/.test(password);
};

export const getPasswordStrength = (password) => {
  let score = 0;
  if (password.length >= 8) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[@$!%*?&#]/.test(password)) score++;
  
  if (score < 3) return { strength: 'weak', color: 'red' };
  if (score < 5) return { strength: 'medium', color: 'yellow' };
  return { strength: 'strong', color: 'green' };
};