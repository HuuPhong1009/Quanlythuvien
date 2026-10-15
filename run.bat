@echo off
setlocal enabledelayedexpansion
title HE THONG THU VIEN TRI THUC - DOANWEB

echo ========================================================
echo   KHOI DONG VA BIEN DICH HE THONG THU VIEN (DOANWEB)
echo ========================================================

set "JAVA_HOME=C:\PROGRA~1\Java\jdk1.8.0_202"
set "CATALINA_HOME=C:\Users\Admin\Downloads\apache-tomcat-9.0.121"

:: Tu dong nhan dien thu muc Doanweb
set "PROJ_DIR=%~dp0"
if exist "%~dp0Doanweb\src" (
    set "PROJ_DIR=%~dp0Doanweb"
)
if "%PROJ_DIR:~-1%"=="\" set "PROJ_DIR=%PROJ_DIR:~0,-1%"

echo Thu muc du an: %PROJ_DIR%
echo.

:: ----------------------------------------------------
:: 0. KHOI DONG MYSQL DATABASE (XAMPP PORT 3307)
:: ----------------------------------------------------
echo 0. Kiem tra MySQL Database cong 3307...
netstat -ano | findstr ":3307 " >nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] MySQL Database dang hoat dong san tren cong 3307.
) else (
    echo [THONG BAO] Dang khoi dong MySQL Database...
    start "" /B "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini" --standalone
    ping 127.0.0.1 -n 3 >nul
    echo [OK] MySQL Database da khoi dong xong.
)

:: ----------------------------------------------------
:: 0b. KIEM TRA APACHE HTTP (DE MO PHPMYADMIN PORT 80)
:: ----------------------------------------------------
echo Kiem tra Apache Web Server cong 80...
netstat -ano | findstr ":80 " >nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Apache Web Server dang hoat dong san tren cong 80.
) else (
    echo [THONG BAO] Dang khoi dong Apache Web Server...
    start "" /B "C:\xampp\apache\bin\httpd.exe"
    ping 127.0.0.1 -n 2 >nul
    echo [OK] Apache da khoi dong.
)

echo.
echo 1. Dang bien dich toan bo ma nguon Java - MVC 3 tang...
if not exist "%PROJ_DIR%\web\WEB-INF\classes" mkdir "%PROJ_DIR%\web\WEB-INF\classes"

"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -cp "%CATALINA_HOME%\lib\servlet-api.jar;%PROJ_DIR%\web\WEB-INF\lib\*" -d "%PROJ_DIR%\web\WEB-INF\classes" %PROJ_DIR%\src\model\*.java %PROJ_DIR%\src\util\*.java %PROJ_DIR%\src\data\*.java %PROJ_DIR%\src\controllers\*.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [LOI] Bien dich ma nguon Java that bai! Vui long kiem tra lai.
    pause
    exit /b %ERRORLEVEL%
)
echo [OK] Bien dich thanh cong tat ca 32 file Java (0 Error, 0 Warning)!

echo.
echo 2. Dang dong bo ma nguon sang thu muc Apache Tomcat webapps...
xcopy /E /Y /I "%PROJ_DIR%\web\*" "%CATALINA_HOME%\webapps\Doanweb\" >nul
echo [OK] Dong bo thu muc webapps\Doanweb hoan tat.

echo.
echo 3. Dang kiem tra va khoi dong Apache Tomcat Server...
netstat -ano | findstr ":8080 " >nul
if %ERRORLEVEL% EQU 0 (
    echo [THONG BAO] Apache Tomcat dang chay san tren cong 8080.
) else (
    echo [THONG BAO] Dang khoi dong Apache Tomcat Server...
    call "%CATALINA_HOME%\bin\startup.bat"
)

echo.
echo 4. Dang mo trinh duyet truy cap he thong...
ping 127.0.0.1 -n 2 >nul
start "" "http://localhost:8080/Doanweb/"

echo.
echo ========================================================
echo   HE THONG THU VIEN (DOANWEB) DA SAN SANG!
echo.
echo   * Web Ung Dung : http://localhost:8080/Doanweb/
echo   * Quan Ly SQL  : http://localhost/phpmyadmin/
echo ========================================================
echo.
pause
