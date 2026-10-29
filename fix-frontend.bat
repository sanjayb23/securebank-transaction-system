@echo off
echo Fixing Frontend Issues...

cd "securebank-frontend"

echo 1. Clearing node_modules and reinstalling...
if exist node_modules rmdir /s /q node_modules
if exist package-lock.json del package-lock.json

echo 2. Installing dependencies...
npm install

echo 3. Building Tailwind CSS...
npx tailwindcss -i ./src/index.css -o ./dist/output.css --watch

echo 4. Starting development server...
npm run dev

pause