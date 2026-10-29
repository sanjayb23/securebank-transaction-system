import { AlertTriangle, X, Phone, Mail } from 'lucide-react';
import Button from './Button';

const ErrorModal = ({ 
  isOpen, 
  onClose, 
  title, 
  message, 
  errorCode,
  showContactInfo = false
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={onClose}></div>

        <span className="hidden sm:inline-block sm:align-middle sm:h-screen">&#8203;</span>

        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div className="sm:flex sm:items-start">
              <div className="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10">
                <AlertTriangle className="h-6 w-6 text-red-600" />
              </div>
              <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left flex-1">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  {title}
                </h3>
                <div className="mt-2">
                  <p className="text-sm text-gray-500">
                    {message}
                  </p>
                  
                  {errorCode === 'ACCOUNT_FROZEN' && showContactInfo && (
                    <div className="mt-4 p-3 bg-blue-50 rounded-md">
                      <h4 className="text-sm font-medium text-blue-900 mb-2">Contact Support</h4>
                      <div className="space-y-1 text-sm text-blue-700">
                        <div className="flex items-center">
                          <Phone className="h-4 w-4 mr-2" />
                          <span>1-800-SECURE-BANK</span>
                        </div>
                        <div className="flex items-center">
                          <Mail className="h-4 w-4 mr-2" />
                          <span>support@securebank.com</span>
                        </div>
                      </div>
                    </div>
                  )}
                  
                  {errorCode === 'DAILY_LIMIT_EXCEEDED' && (
                    <div className="mt-4 p-3 bg-yellow-50 rounded-md">
                      <h4 className="text-sm font-medium text-yellow-900 mb-2">Suggestions</h4>
                      <ul className="text-sm text-yellow-700 space-y-1">
                        <li>• Try a smaller amount</li>
                        <li>• Wait until tomorrow for limit reset</li>
                        <li>• Contact support to increase your limit</li>
                      </ul>
                    </div>
                  )}
                </div>
              </div>
              <button
                onClick={onClose}
                className="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
          </div>
          <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <Button onClick={onClose} className="w-full sm:w-auto">
              Close
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ErrorModal;