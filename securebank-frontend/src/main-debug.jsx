import React from 'react'
import ReactDOM from 'react-dom/client'
import AppDebug from './App-debug.jsx'

console.log('main.jsx is executing');

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AppDebug />
  </React.StrictMode>,
)