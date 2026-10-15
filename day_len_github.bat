@echo off
cls
echo ====================================================================
echo        DANG DAY DO AN DOANWEB LEN GITHUB: QUANLYTHUVIEN
echo ====================================================================
echo.
echo Link repository: https://github.com/HuuPhong1009/Quanlythuvien.git
echo.
echo Dang day 162 file len GitHub...
echo (Luu y: Neu co cua so nho hien len, ban chi can bam "Sign in with your browser")
echo.
"C:\Program Files\Git\cmd\git.exe" push -u origin main

echo.
if %ERRORLEVEL% EQU 0 (
    echo ====================================================================
    echo   CHUC MUNG! TOAN BO DO AN DA LEN GITHUB THANH CONG 100%%!
    echo   Link repo: https://github.com/HuuPhong1009/Quanlythuvien
    echo ====================================================================
) else (
    echo ====================================================================
    echo   Neu bao loi, hay kiem tra cua so dang nhap GitHub tren man hinh.
    echo ====================================================================
)
echo.
pause
