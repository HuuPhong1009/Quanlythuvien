@echo off
cls
echo ====================================================================
echo        DAY DO AN DOANWEB LEN GITHUB (TU DONG)
echo ====================================================================
echo.
echo Hay dan duong link GitHub cua ban vao day
echo (Vi du: https://github.com/nguyenphong/Doanweb.git)
echo.
set /p REPO_URL="Link GitHub: "

if "%REPO_URL%"=="" (
    echo.
    echo [LOI] Ban chua nhap link! Vui long chay lai file.
    pause
    exit /b
)

echo.
echo [1/2] Dang ket noi toi GitHub...
"C:\Program Files\Git\cmd\git.exe" remote remove origin 2>nul
"C:\Program Files\Git\cmd\git.exe" remote add origin %REPO_URL%
"C:\Program Files\Git\cmd\git.exe" branch -M main

echo.
echo [2/2] Dang day toan bo 162 file len GitHub...
echo.
"C:\Program Files\Git\cmd\git.exe" push -u origin main

echo.
if %ERRORLEVEL% EQU 0 (
    echo ====================================================================
    echo   THANH CONG! TOAN BO DO AN DA LEN GITHUB 100%%!
    echo ====================================================================
) else (
    echo [THONG BAO] Neu GitHub yeu cau dang nhap, hay dang nhap tren trinh duyet roi thu lai.
)
echo.
pause
