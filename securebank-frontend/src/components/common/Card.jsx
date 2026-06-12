const Card = ({ children, header, className = '' }) => {
  return (
    <div className={`bg-white rounded-lg shadow-sm border border-gray-200 ${className}`}>
      {header && (
        <div className="px-6 py-4 border-b border-gray-200">
          {typeof header === 'string' ? (
            <h3 className="text-lg font-medium text-gray-900">{header}</h3>
          ) : (
            header
          )}
        </div>
      )}
      <div className="p-6">
        {children}
      </div>
    </div>
  );
};

export default Card;