-- =======================================================
-- FILE KHOI TAO CO SO DU LIEU THU VIEN TRI THUC
-- Chuan UML Class Diagram (Ban sua 15 Lop + 5 Enum)
-- Chuan mo hinh MVC - Murach's Java Servlets and JSP
-- Cach import: Mo http://localhost/phpmyadmin -> Chon tab "Import" -> Chon file setup.sql -> Bam Go
-- =======================================================

CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS danh_gia_sach;
DROP TABLE IF EXISTS phieu_phat;
DROP TABLE IF EXISTS chi_tiet_phieu_muon;
DROP TABLE IF EXISTS ban_sao_sach;
DROP TABLE IF EXISTS phieu_muon;
DROP TABLE IF EXISTS quy_dinh_muon;
DROP TABLE IF EXISTS sach;
DROP TABLE IF EXISTS the_thu_vien;
DROP TABLE IF EXISTS doc_gia;
DROP TABLE IF EXISTS thu_thu;
DROP TABLE IF EXISTS nguoi_dung;
DROP TABLE IF EXISTS nha_xuat_ban;
DROP TABLE IF EXISTS the_loai;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Bang THELOAI
CREATE TABLE the_loai (
    ma_the_loai INT AUTO_INCREMENT PRIMARY KEY,
    ten_the_loai VARCHAR(100) NOT NULL,
    mo_ta TEXT,
    icon VARCHAR(50) DEFAULT 'bi-book'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bang NHAXUATBAN
CREATE TABLE nha_xuat_ban (
    ma_nxb INT AUTO_INCREMENT PRIMARY KEY,
    ten_nxb VARCHAR(150) NOT NULL,
    email VARCHAR(100),
    so_dien_thoai VARCHAR(20),
    dang_hop_tac BOOLEAN DEFAULT TRUE,
    dia_chi VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bang NGUOIDUNG (Lop cha)
CREATE TABLE nguoi_dung (
    ma_nguoi_dung INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai VARCHAR(20),
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro ENUM('QUAN_TRI', 'DOC_GIA') DEFAULT 'DOC_GIA',
    trang_thai VARCHAR(30) DEFAULT 'HOAT_DONG',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bang THUTHU (Ke thua NGUOIDUNG)
CREATE TABLE thu_thu (
    ma_thu_thu INT PRIMARY KEY,
    ngay_vao_lam DATE DEFAULT '2022-01-01',
    FOREIGN KEY (ma_thu_thu) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bang DOCGIA (Ke thua NGUOIDUNG, co Enum LOAIDOCGIA)
CREATE TABLE doc_gia (
    ma_doc_gia INT PRIMARY KEY,
    loai_doc_gia ENUM('SINH_VIEN', 'GIANG_VIEN', 'NGHIEN_CUU_SINH') DEFAULT 'SINH_VIEN',
    ngay_het_han DATE,
    FOREIGN KEY (ma_doc_gia) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bang THETHUVIEN (DocGia 1 -> 1 TheThuVien)
CREATE TABLE the_thu_vien (
    ma_the INT AUTO_INCREMENT PRIMARY KEY,
    ma_doc_gia INT UNIQUE NOT NULL,
    khoa_vien VARCHAR(100),
    lop_hoc VARCHAR(50),
    ngay_cap DATE DEFAULT '2023-09-01',
    ngay_het_han DATE,
    dang_hoat_dong BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ma_doc_gia) REFERENCES doc_gia(ma_doc_gia) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bang QUYDINHMUON (Enum LOAIQUYDINH & LOAIDOCGIA)
CREATE TABLE quy_dinh_muon (
    ma_quy_dinh INT AUTO_INCREMENT PRIMARY KEY,
    ten_quy_dinh VARCHAR(150) NOT NULL,
    loai_quy_dinh ENUM('MUON_NGAN_HAN', 'MUON_DAI_HAN_HOC_KY') DEFAULT 'MUON_NGAN_HAN',
    ap_dung_cho ENUM('SINH_VIEN', 'GIANG_VIEN', 'NGHIEN_CUU_SINH') DEFAULT 'SINH_VIEN',
    so_sach_toi_da INT DEFAULT 3,
    so_ngay_muon_toi_da INT DEFAULT 14,
    muc_phat_moi_ngay DECIMAL(12,2) DEFAULT 5000.00,
    trang_thai BOOLEAN DEFAULT TRUE,
    mo_ta TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bang SACH (Chua thong tin dau muc, ISBN, gia bia)
CREATE TABLE sach (
    ma_sach INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(50) DEFAULT '978-604-0000-0',
    tieu_de VARCHAR(255) NOT NULL,
    tac_gia VARCHAR(150) NOT NULL,
    ma_the_loai INT,
    ma_nxb INT DEFAULT 1,
    nam_xuat_ban INT,
    gia_bia DECIMAL(12,2) DEFAULT 0,
    so_luong_tong INT DEFAULT 1,
    so_luong_con INT DEFAULT 1,
    vi_tri_ke VARCHAR(50) DEFAULT 'Kệ A1',
    tinh_trang VARCHAR(30) DEFAULT 'TOT',
    mo_ta TEXT,
    hinh_anh VARCHAR(255),
    FOREIGN KEY (ma_the_loai) REFERENCES the_loai(ma_the_loai) ON DELETE SET NULL,
    FOREIGN KEY (ma_nxb) REFERENCES nha_xuat_ban(ma_nxb) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bang BANSAOSACH (Cac cuon sach vat ly thuc te tren ke, Enum TINHTRANGSACH)
CREATE TABLE ban_sao_sach (
    ma_vach VARCHAR(50) PRIMARY KEY,
    ma_sach INT NOT NULL,
    tinh_trang ENUM('MOI', 'TOT', 'CU', 'HU_HONG') DEFAULT 'TOT',
    san_sang BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ma_sach) REFERENCES sach(ma_sach) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bang PHIEUMUON (Enum TRANGTHAIPHIEU gom ca TU_CHOI)
CREATE TABLE phieu_muon (
    ma_phieu_muon INT AUTO_INCREMENT PRIMARY KEY,
    ma_doc_gia INT NOT NULL,
    ma_thu_thu INT DEFAULT NULL,
    ma_quy_dinh INT DEFAULT 1,
    ngay_muon DATETIME NOT NULL,
    han_tra DATETIME NOT NULL,
    ngay_tra_thuc_te DATETIME DEFAULT NULL,
    trang_thai ENUM('CHO_DUYET', 'DANG_MUON', 'DA_TRA', 'QUA_HAN', 'DA_HUY', 'TU_CHOI') DEFAULT 'CHO_DUYET',
    ghi_chu TEXT,
    FOREIGN KEY (ma_doc_gia) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE CASCADE,
    FOREIGN KEY (ma_thu_thu) REFERENCES thu_thu(ma_thu_thu) ON DELETE SET NULL,
    FOREIGN KEY (ma_quy_dinh) REFERENCES quy_dinh_muon(ma_quy_dinh) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bang CHITIETPHIEUMUON (Lien ket BanSaoSach, ghi nhan ngayTra va tinhTrangKhiTra)
CREATE TABLE chi_tiet_phieu_muon (
    ma_chi_tiet INT AUTO_INCREMENT PRIMARY KEY,
    ma_phieu_muon INT NOT NULL,
    ma_sach INT NOT NULL,
    ma_vach VARCHAR(50) DEFAULT NULL,
    so_luong INT DEFAULT 1,
    ngay_tra DATETIME DEFAULT NULL,
    tinh_trang_khi_muon ENUM('MOI', 'TOT', 'CU', 'HU_HONG') DEFAULT 'TOT',
    tinh_trang_khi_tra ENUM('MOI', 'TOT', 'CU', 'HU_HONG') DEFAULT NULL,
    FOREIGN KEY (ma_phieu_muon) REFERENCES phieu_muon(ma_phieu_muon) ON DELETE CASCADE,
    FOREIGN KEY (ma_sach) REFERENCES sach(ma_sach) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bang PHIEUPHAT (Enum TRANGTHAIPHAT)
CREATE TABLE phieu_phat (
    ma_phieu_phat INT AUTO_INCREMENT PRIMARY KEY,
    ma_phieu_muon INT NOT NULL,
    so_tien_phat DECIMAL(12,2) DEFAULT 0,
    ngay_lap DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai ENUM('DA_NOP_PHAT', 'CHUA_NOP_PHAT', 'MIEN_PHAT') DEFAULT 'CHUA_NOP_PHAT',
    ly_do VARCHAR(255),
    FOREIGN KEY (ma_phieu_muon) REFERENCES phieu_muon(ma_phieu_muon) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Bang DANHGIA (DANHGIA SACH)
CREATE TABLE danh_gia_sach (
    ma_danh_gia INT AUTO_INCREMENT PRIMARY KEY,
    ma_sach INT NOT NULL,
    ma_nguoi_dung INT NOT NULL,
    so_sao INT DEFAULT 5,
    noi_dung TEXT,
    ngay_dang TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_sach) REFERENCES sach(ma_sach) ON DELETE CASCADE,
    FOREIGN KEY (ma_nguoi_dung) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =======================================================
-- DU LIEU KHOI TAO (INITIAL DATA)
-- =======================================================

-- The Loai (8 chuyen muc)
INSERT INTO the_loai (ma_the_loai, ten_the_loai, mo_ta, icon) VALUES
(1, 'Công Nghệ Thông Tin', 'Lập trình web, phần mềm, giải thuật', 'bi-laptop'),
(2, 'AI & Khoa Học Dữ Liệu', 'Trí tuệ nhân tạo, Machine Learning', 'bi-cpu'),
(3, 'Khoa Học Tự Nhiên', 'Vật lý lý thuyết, toán học, vũ trụ', 'bi-atom'),
(4, 'Kinh Tế - Quản Trị', 'Kinh doanh, đầu tư, khởi nghiệp', 'bi-graph-up-arrow'),
(5, 'Kỹ Năng - Tâm Lý', 'Tâm lý học, phát triển bản thân', 'bi-person-heart'),
(6, 'Văn Học Kinh Điển', 'Tiểu thuyết, tác phẩm bất hủ', 'bi-feather'),
(7, 'Lịch Sử - Xã Hội', 'Lịch sử nhân loại, triết học', 'bi-hourglass-split'),
(8, 'Ngoại Ngữ', 'Tiếng Anh, ngôn ngữ học', 'bi-translate');

-- Nha Xuat Ban
INSERT INTO nha_xuat_ban (ma_nxb, ten_nxb, email, so_dien_thoai, dang_hop_tac, dia_chi) VALUES
(1, 'NXB OReilly Media', 'contact@oreilly.com', '0123456789', TRUE, 'USA'),
(2, 'NXB Prentice Hall', 'support@pearson.com', '0123456790', TRUE, 'USA'),
(3, 'NXB MIT Press', 'mitpress@mit.edu', '0123456791', TRUE, 'Cambridge, USA'),
(4, 'NXB Trẻ', 'hopthu@nxbtre.com.vn', '02839316289', TRUE, 'TP. Hồ Chí Minh'),
(5, 'NXB Nhã Nam', 'contact@nhanam.vn', '02435146875', TRUE, 'Hà Nội'),
(6, 'NXB Kim Đồng', 'cskh@nxbkimdong.com.vn', '02439434730', TRUE, 'Hà Nội'),
(7, 'Cambridge University Press', 'info@cambridge.org', '0123456792', TRUE, 'UK');

-- Nguoi Dung (Mat khau mac dinh: 123456)
INSERT INTO nguoi_dung (ma_nguoi_dung, ho_ten, email, so_dien_thoai, mat_khau, vai_tro) VALUES
(1, 'Thủ Thư Quản Trị', 'admin@library.edu.vn', '0901234567', '123456', 'QUAN_TRI'),
(2, 'Nguyễn Văn An', 'an.nv@student.edu.vn', '0912345678', '123456', 'DOC_GIA'),
(3, 'Trần Văn Phong', 'phong.tv@student.edu.vn', '0923456789', '123456', 'DOC_GIA');

-- Thu Thu
INSERT INTO thu_thu (ma_thu_thu, ngay_vao_lam) VALUES
(1, '2022-01-01');

-- Doc Gia
INSERT INTO doc_gia (ma_doc_gia, loai_doc_gia, ngay_het_han) VALUES
(1, 'GIANG_VIEN', '2026-12-31'),
(2, 'SINH_VIEN', '2026-12-31'),
(3, 'SINH_VIEN', '2026-12-31');

-- The Thu Vien
INSERT INTO the_thu_vien (ma_the, ma_doc_gia, khoa_vien, lop_hoc, ngay_cap, ngay_het_han, dang_hoat_dong) VALUES
(1, 1, 'Khoa CNTT', 'GV-CNTT', '2022-01-01', '2026-12-31', TRUE),
(2, 2, 'Khoa CNTT', 'K47-CNTT01', '2023-09-01', '2026-12-31', TRUE),
(3, 3, 'Khoa Ngoại Ngữ', 'K47-AV02', '2023-09-01', '2026-12-31', TRUE);

-- Quy Dinh Muon
INSERT INTO quy_dinh_muon (ma_quy_dinh, ten_quy_dinh, loai_quy_dinh, ap_dung_cho, so_sach_toi_da, so_ngay_muon_toi_da, muc_phat_moi_ngay, trang_thai, mo_ta) VALUES
(1, 'Quy định mượn chuẩn - Sinh viên', 'MUON_NGAN_HAN', 'SINH_VIEN', 3, 14, 5000.00, TRUE, 'Mượn tối đa 3 cuốn trong 14 ngày, phạt 5.000đ/ngày trễ'),
(2, 'Quy định ưu tiên - Giảng viên', 'MUON_DAI_HAN_HOC_KY', 'GIANG_VIEN', 5, 30, 2000.00, TRUE, 'Mượn tối đa 5 cuốn trong 30 ngày cho mục đích giảng dạy'),
(3, 'Quy định - Nghiên cứu sinh', 'MUON_NGAN_HAN', 'NGHIEN_CUU_SINH', 4, 21, 3000.00, TRUE, 'Mượn tối đa 4 cuốn trong 21 ngày phục vụ đề tài nghiên cứu');

-- 48 Dau Sach Chon Loc
INSERT INTO sach (ma_sach, isbn, tieu_de, tac_gia, ma_the_loai, ma_nxb, nam_xuat_ban, gia_bia, so_luong_tong, so_luong_con, vi_tri_ke, tinh_trang, mo_ta, hinh_anh) VALUES
(1, '978-604-1001-1', 'Murachs Java Servlets and JSP', 'Joel Murach', 1, 1, 2014, 180000, 6, 5, 'Kệ A1-01', 'TOT', 'Giáo trình chuẩn về lập trình Web với Java Servlet và JSP theo mô hình MVC 3 tầng chuyên sâu.', 'images/books/book_1.jpg'),
(2, '978-604-1002-2', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 1, 2, 2008, 220000, 5, 4, 'Kệ A1-02', 'TOT', 'Nghệ thuật viết mã nguồn sạch, dễ đọc, dễ bảo trì và mở rộng cho lập trình viên chuyên nghiệp.', 'images/books/book_2.jpg'),
(3, '978-604-1003-3', 'Design Patterns: Elements of Reusable Software', 'Erich Gamma et al.', 1, 1, 2016, 250000, 4, 3, 'Kệ A1-03', 'TOT', '23 mẫu thiết kế hướng đối tượng kinh điển giúp giải quyết các bài toán kiến trúc phần mềm phức tạp.', 'images/books/book_3.jpg'),
(4, '978-604-1004-4', 'Introduction to Algorithms (CLRS)', 'Thomas H. Cormen et al.', 1, 3, 2022, 350000, 4, 2, 'Kệ A1-04', 'TOT', 'Bách khoa toàn thư về cấu trúc dữ liệu và giải thuật từ cơ bản đến nâng cao toàn diện nhất thế giới.', 'images/books/book_4.jpg'),
(5, '978-604-1005-5', 'Artificial Intelligence: A Modern Approach', 'Stuart Russell & Peter Norvig', 2, 2, 2020, 390000, 5, 4, 'Kệ A2-01', 'TOT', 'Giáo trình nhập môn và chuyên sâu số 1 thế giới về trí tuệ nhân tạo, tác tử thông minh và học máy.', 'images/books/book_5.jpg'),
(6, '978-604-1006-6', 'Deep Learning', 'Ian Goodfellow et al.', 2, 3, 2016, 320000, 4, 3, 'Kệ A2-02', 'TOT', 'Tài liệu nền tảng toàn diện về mạng nơ-ron nhân tạo, tích chập, hồi quy và mô hình sinh sâu sắc.', 'images/books/book_6.jpg'),
(7, '978-604-1007-7', 'Hands-On Machine Learning with Scikit-Learn', 'Aurélien Géron', 2, 1, 2022, 280000, 5, 5, 'Kệ A2-03', 'TOT', 'Hướng dẫn thực hành xây dựng các hệ thống học máy thông minh từ ý tưởng tới môi trường production.', 'images/books/book_7.jpg'),
(8, '978-604-1008-8', 'A Brief History of Time (Lược Sử Thời Gian)', 'Stephen Hawking', 3, 1, 1998, 125000, 8, 7, 'Kệ B1-01', 'TOT', 'Khám phá các bí ẩn của vũ trụ, lỗ đen, thuyết tương đối và nguồn gốc không-thời gian kỳ vĩ.', 'images/books/book_8.jpg'),
(9, '978-604-1009-9', 'Cosmos (Vũ Trụ)', 'Carl Sagan', 3, 1, 2013, 165000, 6, 5, 'Kệ B1-02', 'TOT', 'Hành trình tráng lệ khám phá 15 tỷ năm tiến hóa của vũ trụ và tương lai của loài người.', 'images/books/book_9.jpg'),
(10, '978-604-1010-0', 'The Feynman Lectures on Physics', 'Richard P. Feynman', 3, 1, 2011, 450000, 3, 2, 'Kệ B1-03', 'TOT', 'Bộ bài giảng vật lý huyền thoại của nhà vật lý đoạt giải Nobel Richard Feynman dành cho mọi thế hệ.', 'images/books/book_10.jpg'),
(11, '978-604-1011-1', 'The Intelligent Investor (Nhà Đầu Tư Thông Minh)', 'Benjamin Graham', 4, 1, 2006, 195000, 7, 6, 'Kệ B2-01', 'TOT', 'Kinh thánh về đầu tư giá trị, nguyên tắc an toàn vốn và tư duy thị trường tài chính vững vàng.', 'images/books/book_11.jpg'),
(12, '978-604-1012-2', 'Principles: Life and Work (Những Nguyên Tắc)', 'Ray Dalio', 4, 1, 2017, 280000, 5, 4, 'Kệ B2-02', 'TOT', 'Những nguyên tắc sống và làm việc thực tế tạo nên thành công của quỹ đầu tư Bridgewater Associates.', 'images/books/book_12.jpg'),
(13, '978-604-1013-3', 'Zero to One: Notes on Startups', 'Peter Thiel & Blake Masters', 4, 1, 2014, 115000, 9, 8, 'Kệ B2-03', 'TOT', 'Tư duy đột phá về xây dựng tương lai, tạo ra giá trị độc quyền và khởi nghiệp công nghệ.', 'images/books/book_13.jpg'),
(14, '978-604-1014-4', 'Good to Great (Từ Tốt Đến Vĩ Đại)', 'Jim Collins', 4, 1, 2001, 145000, 6, 5, 'Kệ B2-04', 'TOT', 'Nghiên cứu khoa học giải mã cách các công ty bình thường bứt phá trở thành vĩ đại bền vững.', 'images/books/book_14.jpg'),
(15, '978-604-1015-5', 'Đắc Nhân Tâm (How to Win Friends)', 'Dale Carnegie', 5, 4, 2021, 88000, 12, 10, 'Kệ C1-01', 'TOT', 'Nghệ thuật giao tiếp, thấu hiểu tâm lý và tạo dựng mối quan hệ chân thành, tốt đẹp trọn đời.', 'images/books/book_15.jpg'),
(16, '978-604-1016-6', 'Atomic Habits (Thay Đổi Tí Hon)', 'James Clear', 5, 1, 2018, 139000, 10, 8, 'Kệ C1-02', 'TOT', 'Phương pháp khoa học xây dựng thói quen tốt và loại bỏ thói quen xấu từng bước nhỏ mỗi ngày.', 'images/books/book_16.jpg'),
(17, '978-604-1017-7', 'Thinking, Fast and Slow (Tư Duy Nhanh Và Chậm)', 'Daniel Kahneman', 5, 1, 2011, 210000, 6, 4, 'Kệ C1-03', 'TOT', 'Kiệt tác tâm lý học nhận thức giải mã hai hệ thống vận hành tư duy chi phối mọi quyết định.', 'images/books/book_17.jpg'),
(18, '978-604-1018-8', 'Deep Work (Làm Ra Làm, Chơi Ra Chơi)', 'Cal Newport', 5, 1, 2016, 128000, 7, 5, 'Kệ C1-04', 'TOT', 'Rèn luyện khả năng tập trung cao độ để làm chủ các kỹ năng khó và tạo ra thành quả đột phá.', 'images/books/book_18.jpg'),
(19, '978-604-1019-9', 'Nhà Giả Kim (The Alchemist)', 'Paulo Coelho', 6, 5, 2020, 79000, 15, 12, 'Kệ C2-01', 'TOT', 'Câu chuyện ngụ ngôn tuyệt đẹp về hành trình theo đuổi vận mệnh và lắng nghe tiếng gọi trái tim.', 'images/books/book_19.jpg'),
(20, '978-604-1020-0', 'Hoàng Tử Bé (Le Petit Prince)', 'Antoine de Saint-Exupéry', 6, 6, 2019, 65000, 10, 9, 'Kệ C2-02', 'TOT', 'Tác phẩm văn học bất hủ nhắc nhở người lớn về tình bạn, tình yêu và những điều cốt lõi vô hình.', 'images/books/book_20.jpg'),
(21, '978-604-1021-1', 'Trăm Năm Cô Đơn', 'Gabriel García Márquez', 6, 5, 2018, 175000, 5, 3, 'Kệ C2-03', 'TOT', 'Đỉnh cao của chủ nghĩa hiện thực huyền ảo về dòng họ Buendía và thị trấn Macondo huyền thoại.', 'images/books/book_21.jpg'),
(22, '978-604-1022-2', '1984 (Một Chín Tám Tư)', 'George Orwell', 6, 5, 2020, 95000, 8, 6, 'Kệ C2-04', 'TOT', 'Tiểu thuyết phản địa đàng kinh điển cảnh báo về tự do tư tưởng, sự thật và quyền lực xã hội.', 'images/books/book_22.jpg'),
(23, '978-604-1023-3', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 7, 4, 2017, 230000, 8, 7, 'Kệ D1-01', 'TOT', 'Cái nhìn bao quát đầy kinh ngạc về lịch sử tiến hóa của loài người từ vượn cổ đến thời hiện đại.', 'images/books/book_23.jpg'),
(24, '978-604-1024-4', 'Súng, Vi Trùng và Thép', 'Jared Diamond', 7, 4, 2019, 210000, 5, 4, 'Kệ D1-02', 'TOT', 'Tác phẩm đoạt giải Pulitzer lý giải số phận và sự chênh lệch phát triển giữa các xã hội loài người.', 'images/books/book_24.jpg'),
(25, '978-604-1025-5', 'Lịch Sử Thế Giới', 'H. G. Wells', 7, 4, 2016, 260000, 4, 3, 'Kệ D1-03', 'TOT', 'Bức tranh toàn cảnh về những bước ngoặt vĩ đại định hình nền văn minh nhân loại qua các thời kỳ.', 'images/books/book_25.jpg'),
(26, '978-604-1026-6', 'English Grammar in Use', 'Raymond Murphy', 8, 7, 2019, 160000, 12, 10, 'Kệ D2-01', 'TOT', 'Cuốn sách ngữ pháp tiếng Anh tự học kinh điển và được tin dùng nhiều nhất trên toàn cầu.', 'images/books/real_cover_26.jpg'),
(27, '978-604-1027-7', 'Oxford Advanced Learner Dictionary', 'Oxford University Press', 8, 7, 2020, 420000, 5, 4, 'Kệ D2-02', 'TOT', 'Từ điển Anh - Anh chuẩn mực dành cho người học ngôn ngữ chuyên sâu và nâng cao trình độ.', 'images/books/real_cover_27.jpg'),
(28, '978-604-1028-8', 'The Pragmatic Programmer (Lập Trình Viên Thực Dụng)', 'Andrew Hunt & David Thomas', 1, 2, 2019, 260000, 7, 6, 'Kệ A1-05', 'TOT', 'Hành trình từ thợ code trở thành bậc thầy phát triển phần mềm chuyên nghiệp.', 'images/books/real_cover_28.jpg'),
(29, '978-604-1029-9', 'Refactoring: Improving the Design of Existing Code', 'Martin Fowler', 1, 2, 2018, 290000, 6, 5, 'Kệ A1-06', 'TOT', 'Kỹ thuật tái cấu trúc mã nguồn chuẩn mực giúp phần mềm sạch và dễ mở rộng.', 'images/books/real_cover_29.jpg'),
(30, '978-604-1030-0', 'Head First Design Patterns', 'Eric Freeman & Elisabeth Robson', 1, 1, 2020, 240000, 8, 7, 'Kệ A1-07', 'TOT', 'Học các mẫu thiết kế hướng đối tượng qua hình ảnh trực quan, sinh động và thực tế.', 'images/books/real_cover_30.jpg'),
(31, '978-604-1031-1', 'Python for Data Analysis (Phân Tích Dữ Liệu Với Python)', 'Wes McKinney', 2, 1, 2022, 275000, 9, 8, 'Kệ A2-04', 'TOT', 'Cẩm nang xử lý và phân tích dữ liệu thực chiến với Pandas, NumPy và IPython.', 'images/books/real_cover_31.jpg'),
(32, '978-604-1032-2', 'Pattern Recognition and Machine Learning', 'Christopher M. Bishop', 2, 3, 2016, 360000, 5, 4, 'Kệ A2-05', 'TOT', 'Tác phẩm kinh điển về nhận dạng mẫu thống kê và các thuật toán học máy hiện đại.', 'images/books/real_cover_32.jpg'),
(33, '978-604-1033-3', 'Superintelligence (Siêu Trí Tuệ)', 'Nick Bostrom', 2, 7, 2016, 210000, 6, 5, 'Kệ A2-06', 'TOT', 'Phân tích chiến lược về tương lai khi trí tuệ nhân tạo vượt qua trí thông minh loài người.', 'images/books/real_cover_33.jpg'),
(34, '978-604-1034-4', 'The Selfish Gene (Gen Vị Kỷ)', 'Richard Dawkins', 3, 7, 2016, 185000, 7, 6, 'Kệ B1-04', 'TOT', 'Góc nhìn đột phá về tiến hóa sinh học lấy gen làm trung tâm của sự sống.', 'images/books/real_cover_34.jpg'),
(35, '978-604-1035-5', 'Astrophysics for People in a Hurry', 'Neil deGrasse Tyson', 3, 1, 2017, 155000, 10, 9, 'Kệ B1-05', 'TOT', 'Vật lý thiên văn học súc tích, hấp dẫn dành cho những người bận rộn yêu thích vũ trụ.', 'images/books/real_cover_35.jpg'),
(36, '978-604-1036-6', 'The Origin of Species (Nguồn Gốc Các Loài)', 'Charles Darwin', 3, 7, 2015, 190000, 6, 5, 'Kệ B1-06', 'TOT', 'Công trình khoa học vĩ đại đặt nền móng cho thuyết tiến hóa và sinh học hiện đại.', 'images/books/real_cover_36.jpg'),
(37, '978-604-1037-7', 'Rich Dad Poor Dad (Dạy Con Làm Giàu)', 'Robert T. Kiyosaki', 4, 4, 2017, 110000, 14, 11, 'Kệ B2-05', 'TOT', 'Thay đổi tư duy tài chính cá nhân, đầu tư tài sản và làm chủ dòng tiền.', 'images/books/real_cover_37.jpg'),
(38, '978-604-1038-8', 'The Psychology of Money (Tâm Lý Học Về Tiền)', 'Morgan Housel', 4, 4, 2020, 148000, 12, 10, 'Kệ B2-06', 'TOT', '19 câu chuyện sắc sảo về cách con người suy nghĩ và ứng xử với tiền bạc, tài sản.', 'images/books/real_cover_38.jpg'),
(39, '978-604-1039-9', 'The Lean Startup (Khởi Nghiệp Tinh Gọn)', 'Eric Ries', 4, 1, 2015, 165000, 9, 8, 'Kệ B2-07', 'TOT', 'Phương pháp xây dựng sản phẩm đổi mới sáng tạo liên tục và tối ưu hóa nguồn lực.', 'images/books/real_cover_39.jpg'),
(40, '978-604-1040-0', 'The 7 Habits of Highly Effective People (7 Thói Quen Hiệu Quả)', 'Stephen R. Covey', 5, 4, 2020, 175000, 11, 9, 'Kệ C1-05', 'TOT', '7 nguyên tắc vàng giúp phát triển bản thân và lãnh đạo đội nhóm thành công bền vững.', 'images/books/real_cover_40.jpg'),
(41, '978-604-1041-1', 'Man''s Search for Meaning (Đi Tìm Lẽ Sống)', 'Viktor E. Frankl', 5, 4, 2018, 98000, 10, 8, 'Kệ C1-06', 'TOT', 'Cuốn sách truyền cảm hứng mãnh liệt về ý nghĩa cuộc sống và nghị lực vượt qua nghịch cảnh.', 'images/books/real_cover_41.jpg'),
(42, '978-604-1042-2', 'The Subtle Art of Not Giving a F*ck', 'Mark Manson', 5, 4, 2019, 135000, 9, 7, 'Kệ C1-07', 'TOT', 'Nghệ thuật tập trung vào những giá trị thực sự quan trọng để sống hạnh phúc và tự do.', 'images/books/real_cover_42.jpg'),
(43, '978-604-1043-3', 'To Kill a Mockingbird (Giết Con Chim Nhại)', 'Harper Lee', 6, 5, 2015, 125000, 8, 7, 'Kệ C2-05', 'TOT', 'Tiểu thuyết đoạt giải Pulitzer đầy nhân văn về công lý, lòng trắc ẩn và tuổi thơ.', 'images/books/real_cover_43.jpg'),
(44, '978-604-1044-4', 'The Great Gatsby (Đại Gia Gatsby)', 'F. Scott Fitzgerald', 6, 5, 2018, 105000, 9, 8, 'Kệ C2-06', 'TOT', 'Kiệt tác văn học Mỹ khắc họa giấc mơ phù hoa và bi kịch tình yêu thập niên 1920.', 'images/books/real_cover_44.jpg'),
(45, '978-604-1045-5', 'Crime and Punishment (Tội Ác Và Hình Phạt)', 'Fyodor Dostoevsky', 6, 5, 2017, 195000, 6, 5, 'Kệ C2-07', 'TOT', 'Đỉnh cao tiểu thuyết tâm lý triết học thế giới về lương tri và sự cứu rỗi tâm hồn.', 'images/books/real_cover_45.jpg'),
(46, '978-604-1046-6', 'Homo Deus: Lược Sử Tương Lai', 'Yuval Noah Harari', 7, 4, 2018, 240000, 8, 7, 'Kệ D1-04', 'TOT', 'Dự phóng táo bạo về tương lai nhân loại trong kỷ nguyên công nghệ sinh học và AI.', 'images/books/real_cover_46.jpg'),
(47, '978-604-1047-7', '21 Lessons for the 21st Century (21 Bài Học Thế Kỷ 21)', 'Yuval Noah Harari', 7, 4, 2019, 220000, 7, 6, 'Kệ D1-05', 'TOT', 'Những vấn đề cấp bách nhất của thế giới hiện đại từ công việc, giáo dục đến dữ liệu.', 'images/books/real_cover_47.jpg'),
(48, '978-604-1048-8', 'Word Power Made Easy', 'Norman Lewis', 8, 7, 2016, 150000, 10, 9, 'Kệ D2-03', 'TOT', 'Phương pháp mở rộng vốn từ vựng tiếng Anh thông minh và hiệu quả nhất thế giới.', 'images/books/real_cover_48.jpg');

-- Ban Sao Sach (Mau cho mot so dau sach tieu bieu)
INSERT INTO ban_sao_sach (ma_vach, ma_sach, tinh_trang, san_sang) VALUES
('BC-0001-01', 1, 'TOT', TRUE),
('BC-0001-02', 1, 'TOT', TRUE),
('BC-0001-03', 1, 'TOT', TRUE),
('BC-0001-04', 1, 'TOT', TRUE),
('BC-0001-05', 1, 'TOT', TRUE),
('BC-0001-06', 1, 'TOT', FALSE),
('BC-0002-01', 2, 'TOT', TRUE),
('BC-0002-02', 2, 'TOT', TRUE),
('BC-0002-03', 2, 'TOT', TRUE),
('BC-0002-04', 2, 'TOT', TRUE),
('BC-0002-05', 2, 'TOT', FALSE),
('BC-0003-01', 3, 'TOT', TRUE),
('BC-0003-02', 3, 'TOT', TRUE),
('BC-0003-03', 3, 'TOT', TRUE),
('BC-0004-01', 4, 'TOT', TRUE),
('BC-0004-02', 4, 'TOT', TRUE);

-- Phieu Muon Mau (1 Dang muon, 1 Cho duyet)
INSERT INTO phieu_muon (ma_phieu_muon, ma_doc_gia, ma_thu_thu, ma_quy_dinh, ngay_muon, han_tra, ngay_tra_thuc_te, trang_thai, ghi_chu) VALUES
(1, 2, 1, 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), NULL, 'DANG_MUON', 'Mượn tài liệu học tập môn Lập trình Web'),
(2, 2, NULL, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL, 'CHO_DUYET', 'Đăng ký mượn sách Clean Code để làm đồ án');

-- Chi Tiet Phieu Muon Mau
INSERT INTO chi_tiet_phieu_muon (ma_chi_tiet, ma_phieu_muon, ma_sach, ma_vach, so_luong, tinh_trang_khi_muon) VALUES
(1, 1, 1, 'BC-0001-06', 1, 'TOT'),
(2, 2, 2, 'BC-0002-05', 1, 'TOT');

-- Phieu Phat Mau (Khoi tao rong, chi tu dong tao khi qua han tra sach)
-- INSERT INTO phieu_phat ...

-- Danh Gia Mau
INSERT INTO danh_gia_sach (ma_sach, ma_nguoi_dung, so_sao, noi_dung) VALUES
(1, 2, 5, 'Sách rất hay và dễ hiểu, chuẩn kiến thức Java Servlet & JSP theo mô hình MVC!'),
(8, 3, 5, 'Cuốn sách kinh điển về vũ trụ và thời gian của Stephen Hawking, rất đáng đọc.');
