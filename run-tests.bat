@echo off
echo Running SecureBank Test Suite...
echo.

echo ================================
echo Running Unit Tests with Mockito
echo ================================
mvn test -Dtest="*Test" -DfailIfNoTests=false

echo.
echo ================================
echo Running Integration Tests
echo ================================
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false

echo.
echo ================================
echo Generating Test Coverage Report
echo ================================
mvn jacoco:report

echo.
echo ================================
echo Test Results Summary
echo ================================
echo Unit Tests: Check target/surefire-reports/
echo Integration Tests: Check target/failsafe-reports/
echo Coverage Report: Check target/site/jacoco/index.html
echo.

pause