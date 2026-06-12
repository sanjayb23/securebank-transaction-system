export const formatCurrency = (amount, currency = 'INR') => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: currency,
  }).format(amount);
};

export const formatAccountNumber = (accountNumber) => {
  if (!accountNumber) return '';
  const visibleDigits = 4;
  const masked = accountNumber.slice(0, -visibleDigits).replace(/\d/g, 'X');
  const visible = accountNumber.slice(-visibleDigits);
  return `${masked}${visible}`;
};

export const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

export const formatTransactionId = (id) => {
  return `...${id.slice(-8)}`;
};