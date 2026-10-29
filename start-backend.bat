@echo off
echo Starting SecureBank backend...
cd /d "%~dp0"
mvn spring-boot:run
pause