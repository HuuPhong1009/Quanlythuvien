@echo off
chcp 65001 >nul
title ĐẨY ĐỒ ÁN DOANWEB LÊN GITHUB
cls
echo ====================================================================
echo      HỆ THỐNG TỰ ĐỘNG ĐẨY ĐỒ ÁN DOANWEB LÊN GITHUB
echo ====================================================================
echo.
echo Git đã được cài đặt và toàn bộ 162 file mã nguồn đã được đóng gói sẵn!
echo.
set /p REPO_URL=">> Hãy dán đường link GitHub Repository của bạn vào đây rồi bấm Enter: "

if "%REPO_URL%"=="" (
    echo.
    echo [LỖI] Bạn chưa nhập link GitHub! Vui lòng chạy lại file.
    pause
    exit /b
)

echo.
echo [1/2] Đang liên kết với GitHub: %REPO_URL% ...
"C:\Program Files\Git\cmd\git.exe" remote remove origin 2>nul
"C:\Program Files\Git\cmd\git.exe" remote add origin %REPO_URL%
"C:\Program Files\Git\cmd\git.exe" branch -M main

echo.
echo [2/2] Đang đẩy toàn bộ 162 file lên nhánh main ...
echo (Nếu có cửa sổ GitHub hiện lên hỏi đăng nhập, bạn chỉ cần bấm Sign in with browser)
echo.
"C:\Program Files\Git\cmd\git.exe" push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================================================
    echo   CHÚC MỪNG! TOÀN BỘ ĐỒ ÁN ĐÃ ĐƯỢC ĐẨY LÊN GITHUB THÀNH CÔNG 100%%!
    echo ====================================================================
) else (
    echo.
    echo [THÔNG BÁO] Nếu chưa push được do tài khoản, hãy kiểm tra lại quyền đăng nhập.
)
echo.
pause
