import React from 'react';

function AppDebug() {
  console.log('App component is rendering');
  
  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Debug Mode - App is Working!</h1>
      <p>If you can see this, React is working correctly.</p>
      <p>Current time: {new Date().toLocaleString()}</p>
    </div>
  );
}

export default AppDebug;