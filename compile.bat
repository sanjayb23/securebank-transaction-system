@echo off
echo Attempting to compile the project...
cd /d "C:\Users\RahilN\Desktop\Banking Transaction API System"

REM Try different Maven commands
if exist "mvnw.cmd" (
    echo Using Maven wrapper...
    call mvnw.cmd clean compile
) else if exist "mvn.cmd" (
    echo Using Maven from PATH...
    call mvn.cmd clean compile
) else (
    echo Maven not found. Please install Maven or use an IDE to compile.
    echo.
    echo The following files have been updated with explicit getters/setters:
    echo - All entity classes (User, Account, Transaction, AuditLog)
    echo - All request DTOs (DepositRequest, WithdrawRequest, TransferRequest, CreateAccountRequest)
    echo - TransactionResponse class
    echo - ApiResponse class
    echo.
    echo Lombok configuration has been updated in pom.xml and lombok.config has been created.
)

pause