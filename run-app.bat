@echo off
echo ==============================================
echo       Starting Spring Boot E-Commerce
echo ==============================================

set JAVA_HOME=C:\tools\jdk-21.0.7+6
set PATH=%JAVA_HOME%\bin;%PATH%

echo Java Version:
java -version
echo.
echo Starting application...
mvn spring-boot:run
pause
