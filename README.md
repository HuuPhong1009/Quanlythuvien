# 📚 HỆ THỐNG TRA CỨU VÀ QUẢN LÝ MƯỢN TRẢ THƯ VIỆN (LIBRARY MANAGEMENT SYSTEM)

[![Java Version](https://img.shields.io/badge/Java-JDK%201.8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20Model%202-007396?style=for-the-badge&logo=java&logoColor=white)](#kiến-trúc-hệ-thống)
[![Server](https://img.shields.io/badge/Server-Apache%20Tomcat%209.x-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/Database-MySQL%208.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-Bootstrap%205.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)

---

## 📖 1. GIỚI THIỆU TỔNG QUAN ĐỒ ÁN

**Hệ thống Quản lý Thư viện** là ứng dụng web hoàn chỉnh phục vụ công tác số hóa quy trình quản lý, tra cứu, mượn trả và xử lý vi phạm trong thư viện trường học/tổ chức. Dự án được thiết kế và phát triển theo chuẩn kiến trúc công nghiệp **MVC Model 2**, tách bạch rõ ràng giữa giao diện hiển thị (View), bộ điều phối trung tâm (Controller) và tầng nghiệp vụ dữ liệu (Model/DAO).

* **Học phần:** Lập trình Web (Java Web Application)
* **Sinh viên thực hiện:** Nguyễn Hữu Phong
* **Môi trường phát triển:** VS Code / Eclipse / NetBeans, Apache Tomcat 9.x, MySQL (XAMPP).

---

## 🏛️ 2. KIẾN TRÚC HỆ THỐNG (MÔ HÌNH MVC 2)

Ứng dụng tuân thủ nghiêm ngặt nguyên lý **Separation of Concerns (Phân tách mối quan tâm)**:

```
[ Trình duyệt Web (Client) ]
          │    ▲
  (HTTP)  │    │  (HTML/CSS/JS)
          ▼    │
┌─────────────────────────────────────────────────────────────┐
│ TẦNG CONTROLLER (Java Servlets)                             │
│ - CatalogServlet: /catalog, /book                           │
│ - BorrowCartServlet: /cart                                  │
│ - BorrowServlet: /borrow                                    │
│ - UserServlet: /user                                        │
│ - AdminServlet: /admin                                      │
└──────────────┬───────────────────────────────▲──────────────┘
               │ (Gọi nghiệp vụ)               │ (Dữ liệu trả về)
┌──────────────▼───────────────────────────────┴──────────────┐
│ TẦNG MODEL & DAO (Data Access Object)                       │
│ - Models: Sach, NguoiDung, DocGia, ThuThu, GioMuon, ...     │
│ - DAOs: BookDB, BorrowDB, UserDB, CategoryDB, ReviewDB      │
│ - Utilities: DBUtil (JDBC Pooling & Failover), PasswordUtil │
└──────────────┬───────────────────────────────▲──────────────┘
               │ (Prepared Statements)         │ (ResultSets)
┌──────────────▼───────────────────────────────┴──────────────┐
│ HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (MySQL Database)                  │
│ Bảng: sach, the_loai, nguoi_dung, doc_gia, the_thu_vien,    │
│       phieu_muon, chi_tiet_phieu_muon, ban_sao_sach, ...    │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ 3. CÁC TÍNH NĂNG NỔI BẬT

### 👤 3.1. Phân hệ Độc giả (Reader / Sinh viên)
* **Trang chủ & Danh mục sách:** Hiển thị danh mục sách phong phú, hỗ trợ tìm kiếm theo từ khóa (tiêu đề, tác giả) và lọc động theo Thể loại.
* **Thuật toán phân trang (Pagination):** Phân chia danh mục sách 12 cuốn/trang, tính toán số trang tự động và giữ nguyên bộ lọc khi chuyển trang.
* **Chi tiết sách & Đánh giá (Reviews):** Xem chi tiết thông số xuất bản, số lượng tồn kho khả dụng, vị trí kệ sách và gửi đánh giá sao (1 - 5 ⭐).
* **Giỏ mượn sách thông minh (Session Cart):**
  * Quản lý hoàn toàn trong `HttpSession` độc lập cho từng người dùng.
  * Tự động đồng bộ số lượng khả dụng với CSDL MySQL (`syncCartWithLatestStock`), ngăn chặn việc mượn vượt quá số sách đang có trong kho.
* **Đồng hồ thời gian thực & Xem trước hạn trả:**
  * Tích hợp JavaScript real-time hiển thị ngày giờ thực tế của Việt Nam (`HH:mm:ss dd/MM/yyyy`).
  * Tự động tính toán và hiển thị trước ngày giờ hết hạn khi người dùng chọn mốc mượn (14 ngày tiêu chuẩn, 1 ngày, 7 ngày, tùy chỉnh ngày giờ hoặc test nhanh 1 phút/2 phút).
* **Lịch sử mượn & Phiếu phạt:** Theo dõi chi tiết các phiếu mượn, trạng thái (*Chờ duyệt*, *Đang mượn*, *Đã trả*), xem tiền phạt nếu trả trễ hạn.

### 🛡️ 3.2. Phân hệ Quản trị viên & Thủ thư (Admin / Librarian)
* **Bảng điều khiển (Dashboard):** Thống kê thời gian thực: Tổng số đầu sách, Tổng lượt mượn, Số yêu cầu chờ phê duyệt và bảng danh sách yêu cầu mới nhất.
* **Quản lý Kho sách (CRUD):** Thêm sách mới, cập nhật giá bìa, số lượng, thể loại, vị trí kệ, ảnh bìa và xóa sách.
* **Kiểm soát Tồn kho phòng thủ (Defensive Concurrency Control):**
  * Sử dụng các hàm MySQL `GREATEST(0, LEAST(so_luong_tong, so_luong_con + ?))` đảm bảo kho không bao giờ bị âm hoặc vượt tổng sách.
* **Quy trình Phê duyệt & Thu hồi sách:**
  * Phê duyệt yêu cầu mượn (`approve`) $\rightarrow$ Chuyển trạng thái sang `DANG_MUON` và ghi nhận mã thủ thư.
  * Từ chối yêu cầu (`reject`) $\rightarrow$ Hoàn trả lại số lượng tồn kho khả dụng.
  * Thu hồi sách (`return`) $\rightarrow$ Xác nhận trả, hoàn kho và tự động kích hoạt **Thuật toán tính phạt quá hạn**.
* **Động cơ tính phạt tự động (Automatic Overdue Engine):**
  * Tự động so sánh mốc thời gian thực trả với hạn trả.
  * Nếu quá hạn: Tự động tính tiền phạt theo số ngày trễ $\times$ 5.000 VNĐ (hoặc mức tối thiểu nếu test theo phút) và tạo bản ghi `phieu_phat`.

---

## 🔑 4. TÀI KHOẢN TRẢI NGHIỆM HỆ THỐNG

| Vai trò | Email đăng nhập | Mật khẩu | Quyền hạn |
| :--- | :--- | :---: | :--- |
| **Quản trị viên (Admin / Thủ thư)** | `admin@library.edu.vn` | `123456` | Toàn quyền Dashboard, duyệt mượn, thu sách, quản lý sách |
| **Quản trị viên dự phòng** | `admin@thuvien.edu.vn` | `admin123` | Tài khoản quản trị dự phòng |
| **Độc giả (Sinh viên mẫu 1)** | `an.nv@student.edu.vn` | `123456` | Mượn sách, quản lý giỏ hàng, xem lịch sử |
| **Độc giả (Sinh viên mẫu 2)** | `phong@student.edu.vn` | `123456` | Độc giả thực tế phục vụ báo cáo |

---

## 📂 5. CẤU TRÚC THƯ MỤC NGUỒN (PROJECT STRUCTURE)

```
Doanweb/
│
├── src/                               # Toàn bộ mã nguồn Java
│   ├── controllers/                   # Tầng Controller (Java Servlets)
│   │   ├── AdminServlet.java          # Điều phối chức năng Quản trị & Dashboard
│   │   ├── BorrowCartServlet.java     # Quản lý Giỏ mượn trong Session
│   │   ├── BorrowServlet.java         # Tạo phiếu mượn & Lịch sử mượn
│   │   ├── CatalogServlet.java        # Trang chủ, Danh mục, Tìm kiếm & Đánh giá
│   │   └── UserServlet.java           # Đăng nhập, Đăng ký, Phân quyền
│   ├── data/                          # Tầng DAO (Data Access Objects)
│   │   ├── BookDB.java                # Tương tác bảng sach & kho sách
│   │   ├── BorrowDB.java              # Giao dịch mượn/trả & tính phạt
│   │   ├── CategoryDB.java            # Tương tác bảng the_loai
│   │   ├── DBUtil.java                # Kết nối JDBC, Failover port & Pooling
│   │   ├── ReviewDB.java              # Tương tác bảng danh_gia
│   │   └── UserDB.java                # Tương tác bảng nguoi_dung, doc_gia
│   ├── model/                         # Tầng Model (Entities/POJO)
│   │   ├── Sach.java, NguoiDung.java, DocGia.java, ThuThu.java, ...
│   │   └── GioMuon.java, ChiTietGioMuon.java, PhieuMuon.java, ...
│   └── util/                          # Tiện ích bổ trợ
│       └── PasswordUtil.java          # Mã hóa băm & kiểm tra mật khẩu
│
├── web/                               # Tầng View & Tài nguyên web
│   ├── admin/                         # Giao diện dành riêng cho Admin
│   │   ├── dashboard.jsp              # Bảng điều khiển tổng quan
│   │   ├── books.jsp                  # Quản lý danh sách sách
│   │   ├── book_form.jsp              # Form thêm/sửa sách
│   │   └── borrows.jsp                # Quản lý duyệt mượn và thu sách
│   ├── images/                        # Hình ảnh bìa sách và giao diện
│   ├── includes/                      # Các thành phần giao diện dùng chung (header, navbar, footer)
│   ├── styles/                        # File CSS tùy biến (main.css)
│   ├── WEB-INF/                       # Cấu hình bảo mật ứng dụng
│   │   ├── lib/                       # Thư viện JAR (MySQL Driver, Servlet/JSP API)
│   │   └── web.xml                    # File điều hướng URL Servlet Mapping
│   ├── index.jsp                      # Trang chủ danh mục sách
│   ├── book_detail.jsp                # Trang chi tiết sách & đánh giá
│   ├── cart.jsp                       # Trang giỏ mượn sách & đồng hồ real-time
│   ├── history.jsp                    # Trang lịch sử mượn sách của độc giả
│   ├── login.jsp                      # Trang đăng nhập
│   └── register.jsp                   # Trang đăng ký độc giả mới
│
├── setup.sql                          # Script khởi tạo CSDL MySQL (bảng mã utf8mb4)
├── run.bat                            # Script khởi động dự án 1-click
├── stop.bat                           # Script dừng máy chủ an toàn
├── nap_lai_database.bat               # Script nạp lại CSDL mẫu nhanh chóng
├── HUONG_DAN_CHI_TIET_DEMO_SOURCE_CODE.md # Tài liệu tra cứu chi tiết từng dòng code
└── README.md                          # Tài liệu giới thiệu tổng quan đồ án
```

---

## 🚀 6. HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN

### Yêu cầu môi trường:
* **Java Development Kit (JDK):** 1.8 trở lên.
* **Web Server:** Apache Tomcat 9.0.x.
* **Hệ quản trị CSDL:** MySQL 5.7+ hoặc MySQL 8.x (khuyên dùng XAMPP / MySQL Server).

### Các bước thực hiện:
1. **Thiết lập Cơ sở dữ liệu:**
   * Mở MySQL (XAMPP).
   * Tạo cơ sở dữ liệu `library_db` với bảng mã `utf8mb4_unicode_ci`.
   * Import file [`setup.sql`](file:///C:/LapTrinhWeb/Doanweb/setup.sql) vào `library_db`.
   * *(Lưu ý: Lớp `DBUtil.java` đã được lập trình sẵn cơ chế tự động thử lần lượt cổng `3307` và `3306`, cùng danh sách mật khẩu phổ biến `""`, `"root"`, `"123456"` nên tương thích 100% với mọi máy chấm).*

2. **Triển khai ứng dụng lên Tomcat:**
   * Sao chép toàn bộ thư mục `Doanweb` vào thư mục `webapps/` của Apache Tomcat 9.0.
   * Khởi động máy chủ Tomcat (`bin/catalina.bat run` hoặc bấm file `run.bat`).

3. **Truy cập ứng dụng:**
   * Mở trình duyệt web và truy cập địa chỉ:  
     👉 **`http://localhost:8080/Doanweb/catalog`**
