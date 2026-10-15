@echo off
title CUA SO DONG LENH SQL - LIBRARY_DB

echo ========================================================
echo   KET NOI CO SO DU LIEU SQL (PORT 3307 - LIBRARY_DB)
echo ========================================================
echo.
echo Cac cau lenh tham khao:
echo   - Xem danh sach bang  : SHOW TABLES;
echo   - Xem danh sach sach  : SELECT ma_sach, tieu_de, tac_gia FROM sach LIMIT 10;
echo   - Xem phieu muon      : SELECT * FROM phieu_muon;
echo   - Thoat               : EXIT;
echo.

"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 --default-character-set=utf8mb4 library_db
