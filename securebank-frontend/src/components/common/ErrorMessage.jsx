import { AlertCircle, X } from 'lucide-react';

const ErrorMessage = ({ message, onDismiss, className = '' }) => {
  if (!message) return null;

  return (
    <div className={`bg-red-50 border border-red-200 rounded-md p-4 ${className}`}>
      <div className="flex">
        <AlertCircle className="h-5 w-5 text-red-400" />
        <div className="ml-3 flex-1">
          <p className="text-sm text-red-800">{message}</p>
        </div>
        {onDismiss && (
          <button onClick={onDismiss} className="ml-auto text-red-400 hover:text-red-600">
            <X size={16} />
          </button>
        )}
      </div>
    </div>
  );
};

export default ErrorMessage;