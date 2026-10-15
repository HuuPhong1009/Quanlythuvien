@echo off
title NAP LAI DU LIEU DATABASE (UTF-8 CHUAN)

echo ========================================================
echo   DANG NAP LAI TOAN BO DATABASE LIBRARY_DB (UTF-8 CHUAN)
echo ========================================================

"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 --default-character-set=utf8mb4 library_db < "%~dp0Doanweb\setup.sql"
if %ERRORLEVEL% EQU 0 (
    echo [OK] Nap thanh cong toan bo 13 bang va du lieu tieng Viet chuan UTF-8!
) else (
    echo [LOI] Khong the nap du lieu. Vui long kiem tra MySQL da chay tren cong 3307 chua.
)

pause
