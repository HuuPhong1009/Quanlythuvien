# 📚 CẨM NANG BÁO CÁO & DEMO CHI TIẾT SOURCE CODE ĐỒ ÁN WEB
> **Dự án:** Hệ thống Tra cứu & Quản lý Mượn Trả Thư viện  
> **Kiến trúc:** Mô hình MVC 2 (Model - View - Controller)  
> **Nền tảng:** Java EE (Servlet 3.1, JSP, JDBC) + MySQL 8.x + Bootstrap 5.3  

---

## 🗺️ 1. BẢN ĐỒ ĐIỀU HƯỚNG URL & CẤU TRÚC SERLVET (`web.xml`)

File cấu hình gốc: [`web/WEB-INF/web.xml`](file:///C:/LapTrinhWeb/Doanweb/web/WEB-INF/web.xml)

| URL Pattern | Tên Servlet | File Controller Vật Lý | Chức năng nghiệp vụ chính |
| :--- | :--- | :--- | :--- |
| `/catalog`, `/book` | `CatalogServlet` | [`src/controllers/CatalogServlet.java`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/CatalogServlet.java) | Trang chủ, danh mục sách, tìm kiếm từ khóa, lọc thể loại, phân trang, xem chi tiết & đánh giá sao |
| `/cart` | `BorrowCartServlet` | [`src/controllers/BorrowCartServlet.java`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/BorrowCartServlet.java) | Quản lý giỏ mượn trong Session (`add`, `update`, `remove`, `clear`), kiểm tra tồn kho khả dụng |
| `/borrow` | `BorrowServlet` | [`src/controllers/BorrowServlet.java`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/BorrowServlet.java) | Tạo phiếu mượn (Giao dịch ACID Transaction) & xem lịch sử mượn của độc giả |
| `/user` | `UserServlet` | [`src/controllers/UserServlet.java`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/UserServlet.java) | Đăng nhập, đăng xuất (`session.invalidate`), đăng ký tài khoản độc giả mới |
| `/admin` | `AdminServlet` | [`src/controllers/AdminServlet.java`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java) | Dashboard thống kê, CRUD sách, duyệt mượn (`approve`), thu sách (`return`) & tính tiền phạt |

---

## 🎯 2. DEMO CHI TIẾT 10 CHỨC NĂNG: FILE NÀO, DÒNG BAO NHIÊU, CODE CHẠY NHƯ THẾ NÀO?

---

### CHỨC NĂNG 1: Trang chủ, Danh mục Sách, Tìm kiếm & Thuật toán Phân trang
* **Giao diện (View):** [`web/index.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/index.jsp)
* **Bộ điều phối (Controller):** [`CatalogServlet.java#L33-L113`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/CatalogServlet.java#L33-L113)
* **Tầng dữ liệu (DAO):** [`BookDB.java#L96-L152`](file:///C:/LapTrinhWeb/Doanweb/src/data/BookDB.java#L96-L152), [`CategoryDB.java`](file:///C:/LapTrinhWeb/Doanweb/src/data/CategoryDB.java)
* **Model đại diện:** [`Sach.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/Sach.java), [`TheLoai.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/TheLoai.java)

#### 🔍 Chi tiết luồng code chạy:
1. **Tiếp nhận Request:** Người dùng vào `http://localhost:8080/Doanweb/catalog?q=Java&cat=1&page=2`.
2. **Controller xử lý (`CatalogServlet.java`):**
   * Dòng 34 - 48: Phương thức `doGet()` nhận request, gọi hàm con `showList(request, response)` (Dòng 65).
   * Dòng 67 - 76: Đọc tham số `q` (từ khóa) và `cat` (mã thể loại).
   * Dòng 78: Gọi `BookDB.search(keyword, categoryId)`.
     * Trong `BookDB.java` (Dòng 138 - 152): Lọc danh sách sách theo điều kiện chứa từ khóa tiêu đề/tác giả và khớp mã thể loại.
   * Dòng 81 - 102: **Thuật toán phân trang ứng dụng:**
     * `pageSize = 12` (quy định 12 cuốn/trang).
     * `totalCount = allBooks.size()` (tổng số sách tìm được).
     * `totalPages = (int) Math.ceil((double) totalCount / pageSize)` (tính tổng số trang cần có).
     * `fromIndex = (currentPage - 1) * pageSize`, `toIndex = Math.min(fromIndex + pageSize, totalCount)`.
     * `pagedBooks = allBooks.subList(fromIndex, toIndex)` (cắt đúng 12 cuốn của trang hiện tại).
   * Dòng 104 - 110: Gắn `books`, `categories`, `currentPage`, `totalPages`, `keyword`, `selectedCat` vào Request Scope.
   * Dòng 112: `request.getRequestDispatcher("/index.jsp").forward(request, response)` chuyển tiếp dữ liệu ra giao diện.
3. **Hiển thị View (`index.jsp`):** Dùng vòng lặp for hiển thị thẻ card sách, hiển thị nhãn "Còn X cuốn" và tạo thanh phân trang Bootstrap.

---

### CHỨC NĂNG 2: Xem Chi tiết Sách & Gửi Đánh giá Sao (Reviews)
* **Giao diện (View):** [`web/book_detail.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/book_detail.jsp)
* **Bộ điều phối (Controller):** [`CatalogServlet.java#L115-L167`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/CatalogServlet.java#L115-L167)
* **Tầng dữ liệu (DAO):** [`BookDB.java#L154-L181`](file:///C:/LapTrinhWeb/Doanweb/src/data/BookDB.java#L154-L181), [`ReviewDB.java`](file:///C:/LapTrinhWeb/Doanweb/src/data/ReviewDB.java)
* **Model đại diện:** [`Sach.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/Sach.java), [`DanhGia.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/DanhGia.java)

#### 🔍 Chi tiết luồng code chạy:
* **Xem chi tiết sách (`showDetail`, Dòng 115 - 142):**
  1. Người dùng click vào sách $\rightarrow$ URL: `GET /catalog?action=detail&id=1`.
  2. `CatalogServlet` đọc `id`, gọi `BookDB.getById(id)` lấy thông tin sách và gọi `ReviewDB.getByBook(id)` lấy danh sách đánh giá của các độc giả trước đó.
  3. Gắn `book` và `reviews` vào request rồi `forward("/book_detail.jsp")`.
* **Gửi đánh giá sao (`addReview`, Dòng 144 - 167):**
  1. Độc giả chọn số sao (1 - 5 ⭐), nhập bình luận rồi bấm nút Gửi $\rightarrow$ `POST /catalog?action=review`.
  2. `CatalogServlet` kiểm tra session người dùng (`currentUser`). Nếu chưa đăng nhập thì redirect về trang login.
  3. Đóng gói thông tin vào Model `DanhGia` và gọi `ReviewDB.add(dg)` để thực thi câu lệnh SQL:
     ```sql
     INSERT INTO danh_gia (ma_sach, ma_nguoi_dung, so_sao, noi_dung, ngay_dang) VALUES (?, ?, ?, ?, NOW());
     ```
  4. Dùng `response.sendRedirect("catalog?action=detail&id=" + bookId + "&reviewed=true")` để tải lại trang chi tiết kèm nhận xét mới.

---

### CHỨC NĂNG 3: Quản lý Giỏ mượn sách (Session Cart Management)
* **Giao diện (View):** [`web/cart.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/cart.jsp)
* **Bộ điều phối (Controller):** [`BorrowCartServlet.java#L26-L144`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/BorrowCartServlet.java#L26-L144)
* **Model đại diện:** [`GioMuon.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/GioMuon.java), [`ChiTietGioMuon.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/ChiTietGioMuon.java)

#### 🔍 Chi tiết luồng code chạy:
1. **Lưu trữ giỏ mượn trong `HttpSession`:**
   * Không lưu giỏ hàng vào database để tránh quá tải I/O. Mỗi phiên làm việc của người dùng giữ một đối tượng `GioMuon` độc lập qua `session.getAttribute("borrowCart")`.
2. **Cơ chế tự động đồng bộ tồn kho `syncCartWithLatestStock` (Dòng 33 - 47):**
   * Khi người dùng mở giỏ hoặc thêm/sửa số lượng, hàm này duyệt qua từng cuốn sách trong giỏ và truy vấn số lượng khả dụng mới nhất trong bảng `sach`.
   * Nếu sách đã bị mượn hết (`soLuongCon <= 0`) $\rightarrow$ tự động xóa khỏi giỏ.
   * Nếu người dùng yêu cầu số lượng lớn hơn số lượng còn lại trong kho $\rightarrow$ tự động điều chỉnh số lượng mượn bằng đúng tồn kho tối đa.
3. **Các hành động POST (Dòng 68 - 144):**
   * `action=add` (Dòng 92 - 107): Thêm sách vào giỏ với số lượng chỉ định.
   * `action=update` (Dòng 108 - 123): Nhận giá trị mới từ nút `+` hoặc `-`. Nếu vượt kho khả dụng, gán cờ `clampedToMax = true`.
   * `action=remove` (Dòng 124 - 128): Gọi `cart.xoaSach(bookId)`.
   * `action=clear` (Dòng 129 - 133): Gọi `cart.xoaHet()`.
4. **Mô hình PRG (Post/Redirect/Get) (Dòng 139 - 143):**
   * Sau khi cập nhật giỏ, dùng `response.sendRedirect("cart")` để chuyển hướng. Tránh lỗi người dùng F5 bị gửi lại lệnh thêm/xóa.

---

### CHỨC NĂNG 4: Giao dịch Xác nhận Mượn sách (ACID Database Transaction)
* **Giao diện (View):** Form mượn sách trong [`web/cart.jsp#L138-L176`](file:///C:/LapTrinhWeb/Doanweb/web/cart.jsp#L138-L176)
* **Bộ điều phối (Controller):** [`BorrowServlet.java#L65-L125`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/BorrowServlet.java#L65-L125)
* **Tầng dữ liệu (DAO Cốt lõi):** [`BorrowDB.java#L124-L230`](file:///C:/LapTrinhWeb/Doanweb/src/data/BorrowDB.java#L124-L230)
* **Bảng CSDL tác động:** `phieu_muon`, `chi_tiet_phieu_muon`, `ban_sao_sach`

#### 🔍 Chi tiết luồng code chạy (Quy trình Transaction nguyên tử):
1. Độc giả bấm nút *"Gửi yêu cầu mượn"* $\rightarrow$ gửi `POST /borrow` kèm các tham số: `notes` (ghi chú), `loanDuration` (thời hạn mượn), `customDue` (mốc ngày giờ tùy chỉnh).
2. **Tại `BorrowServlet.doPost()`:**
   * Dòng 71 - 76: Kiểm tra đăng nhập qua `session.getAttribute("currentUser")`.
   * Dòng 79 - 84: Khóa an toàn đa luồng `synchronized (session.getId().intern())` chống việc độc giả bấm liên tiếp 2 lần sinh ra 2 phiếu mượn trùng lặp.
   * Dòng 95: Gọi phương thức giao dịch `BorrowDB.createBorrow(...)`.
3. **Bên trong `BorrowDB.createBorrow(...)` (Dòng 124 - 230):**
   * **Bước 1 - Tính hạn trả (Dòng 131 - 162):** Tính toán chính xác ngày mượn `ngayMuon` và hạn trả `hanTra` theo tùy chọn (14 ngày mặc định, 1 phút test nhanh, quá hạn ngay hoặc ngày giờ tự chọn).
   * **Bước 2 - Bật Transaction (Dòng 178):** `con.setAutoCommit(false)` $\rightarrow$ Ngắt chế độ tự lưu, bắt đầu giao dịch.
   * **Bước 3 - Tạo Phiếu mượn (Dòng 180 - 190):** Thực thi câu lệnh:
     ```sql
     INSERT INTO phieu_muon (ma_doc_gia, ma_quy_dinh, ngay_muon, han_tra, ghi_chu, trang_thai)
     VALUES (?, 1, ?, ?, ?, 'CHO_DUYET');
     ```
     Sử dụng `Statement.RETURN_GENERATED_KEYS` để lấy chính xác mã phiếu mượn tự tăng (`generatedMaPhieu`).
   * **Bước 4 - Chèn chi tiết mượn bằng Batch Execution (Dòng 193 - 211):**
     Duyệt qua danh sách sách trong giỏ, dùng `psCt.addBatch()` và `psBs.addBatch()`, sau đó gọi `psCt.executeBatch()` để chèn đồng loạt toàn bộ chi tiết sách và đổi trạng thái bản sao sách thành `san_sang = FALSE`.
   * **Bước 5 - Xác nhận thành công (Dòng 213):** `con.commit()` $\rightarrow$ Dữ liệu được lưu vĩnh viễn vào CSDL.
   * **Bước 6 - Xử lý sự cố (Dòng 217 - 221):** Nếu có bất kỳ lỗi ngoại lệ nào $\rightarrow$ `con.rollback()`, toàn bộ các thay đổi bị hủy bỏ, đảm bảo không bao giờ sinh ra dữ liệu rác.
4. **Kết thúc:** `cart.xoaHet()`, sau đó `response.sendRedirect("borrow?action=history&success=true")`.

---

### CHỨC NĂNG 5: Xem Lịch sử Mượn sách của Độc giả
* **Giao diện (View):** [`web/history.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/history.jsp)
* **Bộ điều phối (Controller):** [`BorrowServlet.java#L33-L60`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/BorrowServlet.java#L33-L60)
* **Tầng dữ liệu (DAO):** [`BorrowDB.java#L279-L288`](file:///C:/LapTrinhWeb/Doanweb/src/data/BorrowDB.java#L279-L288)
* **Model đại diện:** [`PhieuMuon.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/PhieuMuon.java), [`ChiTietPhieuMuon.java`](file:///C:/LapTrinhWeb/Doanweb/src/model/ChiTietPhieuMuon.java)

#### 🔍 Chi tiết luồng code chạy:
1. Độc giả bấm vào menu *"Lịch sử mượn"* $\rightarrow$ gửi `GET /borrow?action=history`.
2. `BorrowServlet.doGet()`:
   * Lấy đối tượng độc giả từ session: `NguoiDung user = (NguoiDung) session.getAttribute("currentUser")`.
   * Gọi `BorrowDB.getByReader(user.getMaNguoiDung())` để lấy danh sách các phiếu mượn của riêng độc giả đó.
   * Gắn kết quả vào request scope: `request.setAttribute("borrows", borrows)`.
   * Chuyển tiếp ra giao diện: `request.getRequestDispatcher("/history.jsp").forward(request, response)`.
3. Giao diện `history.jsp` hiển thị từng phiếu mượn kèm trạng thái (*Chờ duyệt*, *Đang mượn*, *Đã trả*), danh sách sách mượn bên trong, ngày mượn, hạn trả và hiển thị thông báo phạt màu đỏ nếu có phát sinh phiếu phạt.

---

### CHỨC NĂNG 6: Đăng nhập, Đăng ký & Phân quyền Người dùng (RBAC)
* **Giao diện (View):** [`web/login.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/login.jsp), [`web/register.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/register.jsp)
* **Bộ điều phối (Controller):** [`UserServlet.java#L23-L110`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/UserServlet.java#L23-L110)
* **Tầng dữ liệu (DAO):** [`UserDB.java#L60-L205`](file:///C:/LapTrinhWeb/Doanweb/src/data/UserDB.java#L60-L205)
* **Tiện ích bảo mật:** [`PasswordUtil.java`](file:///C:/LapTrinhWeb/Doanweb/src/util/PasswordUtil.java)

#### 🔍 Chi tiết luồng code chạy:
* **Quy trình Đăng nhập (`handleLogin`, Dòng 62 - 81):**
  1. Độc giả nhập email/SĐT và mật khẩu $\rightarrow$ `POST /user?action=login`.
  2. Gọi `UserDB.login(email, password)`:
     * Dùng `PreparedStatement` truy vấn kết nối 2 bảng:
       ```sql
       SELECT u.*, t.ma_the, t.khoa_vien, t.lop_hoc, t.ngay_cap, t.ngay_het_han as the_het_han, t.dang_hoat_dong
       FROM nguoi_dung u LEFT JOIN the_thu_vien t ON u.ma_nguoi_dung = t.ma_doc_gia
       WHERE (u.email = ? OR u.so_dien_thoai = ?);
       ```
     * Dùng `PasswordUtil.checkPassword(password, storedPasswordOrHash)` để kiểm tra mật khẩu.
     * **Ứng dụng tính ĐA HÌNH trong OOP:** Nếu cột `vai_tro = 'QUAN_TRI'` thì khởi tạo đối tượng `ThuThu`; nếu là `DOC_GIA` thì khởi tạo đối tượng `DocGia`.
  3. **Phân quyền điều hướng:**
     * Lưu user vào session: `session.setAttribute("currentUser", user)`.
     * Nếu `user.isQuanTri()` $\rightarrow$ điều hướng về trang quản trị `response.sendRedirect("admin")`.
     * Nếu là Độc giả bình thường $\rightarrow$ điều hướng về trang chủ `response.sendRedirect("catalog")`.
* **Quy trình Đăng ký (`handleRegister`, Dòng 83 - 107):**
  1. Nhận thông tin họ tên, email, SĐT, mật khẩu, loại độc giả từ form.
  2. Gọi `UserDB.register(dg)`: Băm mật khẩu bằng `PasswordUtil.hashPassword()`, mở Transaction và chèn đồng thời vào 3 bảng (`nguoi_dung` $\rightarrow$ `doc_gia` $\rightarrow$ `the_thu_vien`), sau đó gọi `commit()`.
* **Đăng xuất (Dòng 35 - 41):** Gọi `session.invalidate()` để hủy phiên và chuyển về `/catalog`.

---

### CHỨC NĂNG 7: Dashboard Quản trị & Thống kê Tổng quan (Admin)
* **Giao diện (View):** [`web/admin/dashboard.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/dashboard.jsp)
* **Bộ điều phối (Controller):** [`AdminServlet.java#L37-L133`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java#L37-L133)
* **Tầng dữ liệu (DAO):** [`BookDB.java`](file:///C:/LapTrinhWeb/Doanweb/src/data/BookDB.java), [`BorrowDB.java`](file:///C:/LapTrinhWeb/Doanweb/src/data/BorrowDB.java)

#### 🔍 Chi tiết luồng code chạy:
1. Thủ thư truy cập `http://localhost:8080/Doanweb/admin`.
2. **Kiểm tra phân quyền (`checkAdmin`, Dòng 37 - 45):**
   * Lấy `currentUser` từ session.
   * Nếu user bằng null hoặc `!user.isQuanTri()` $\rightarrow$ chặn đứng truy cập và chuyển hướng về trang đăng nhập với thông báo lỗi.
3. **Tổng hợp số liệu thống kê (Dòng 124 - 132):**
   * `totalBooks = BookDB.getAll().size()` (Tổng số đầu sách).
   * `totalBorrows = BorrowDB.getAll().size()` (Tổng lượt mượn trong hệ thống).
   * `pendingCount = BorrowDB.countPendingBorrows()` (Số phiếu mượn đang ở trạng thái `CHO_DUYET`).
4. Gắn các biến thống kê vào request scope và `forward("/admin/dashboard.jsp")` để hiển thị các ô thống kê màu sắc và danh sách các phiếu mượn mới nhất.

---

### CHỨC NĂNG 8: Phê duyệt Mượn sách (Approve & Reject)
* **Giao diện (View):** [`web/admin/dashboard.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/dashboard.jsp), [`web/admin/borrows.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/borrows.jsp)
* **Bộ điều phối (Controller):** [`AdminServlet.java#L86-L123`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java#L86-L123)
* **Tầng dữ liệu (DAO):** [`BorrowDB.java#L398-L463`](file:///C:/LapTrinhWeb/Doanweb/src/data/BorrowDB.java#L398-L463)

#### 🔍 Chi tiết luồng code chạy:
* **Khi Thủ thư bấm "Duyệt" (`approve`):**
  1. Gửi `GET /admin?action=approve&id=X`.
  2. `AdminServlet` lấy ID phiếu mượn và thông tin thủ thư đang đăng nhập.
  3. Gọi `BorrowDB.approve(id, thuThu)`:
     ```sql
     UPDATE phieu_muon SET trang_thai = 'DANG_MUON', ma_thu_thu = ? WHERE ma_phieu_muon = ?;
     ```
  4. Trạng thái phiếu chuyển sang `DANG_MUON`, độc giả có thể đến quầy nhận sách vật lý.
* **Khi Thủ thư bấm "Từ chối" (`reject`):**
  1. Gửi `GET /admin?action=reject&id=X&reason=...`.
  2. Gọi `BorrowDB.reject(id, reason)`:
     * Cập nhật `trang_thai = 'TU_CHOI'` và ghi chú lý do từ chối.
     * **Cơ chế hoàn trả kho tự động:** Gọi `BookDB.adjustStock(maSach, +soLuong)` để cộng trả lại số lượng sách vào kho khả dụng cho các độc giả khác.

---

### CHỨC NĂNG 9: Thu hồi Trả sách & Thuật toán Tính phạt Quá hạn tự động
* **Giao diện (View):** Nút "Thu hồi sách" trên [`web/admin/borrows.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/borrows.jsp)
* **Bộ điều phối (Controller):** [`AdminServlet.java#L99-L108`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java#L99-L108)
* **Tầng dữ liệu (DAO Cốt lõi):** [`BorrowDB.java#L484-L603`](file:///C:/LapTrinhWeb/Doanweb/src/data/BorrowDB.java#L484-L603)
* **Bảng CSDL tác động:** `phieu_muon`, `chi_tiet_phieu_muon`, `ban_sao_sach`, `phieu_phat`

#### 🔍 Chi tiết luồng code chạy (Thuật toán tính phạt):
1. Khi độc giả mang sách trả tại quầy, Thủ thư bấm nút *"Thu hồi sách"* $\rightarrow$ gửi `GET /admin?action=return&id=X`.
2. `AdminServlet` gọi `BorrowDB.returnBooks(id)`.
3. **Bên trong `BorrowDB.returnBooks(id)` (Dòng 494 - 603):**
   * **Bước 1:** Ghi nhận mốc thời gian thực tế `now = new Date()`.
   * **Bước 2:** Cập nhật bảng `phieu_muon` chuyển `trang_thai = 'DA_TRA'` và `ngay_tra_thuc_te = now`.
   * **Bước 3 (Hoàn kho khả dụng):** Gọi `BookDB.adjustStock(maSach, +soLuong)` để cộng lại số lượng sách vào kho, đổi trạng thái bản sao sách `ban_sao_sach.san_sang = TRUE`.
   * **Bước 4 (Thuật toán kiểm tra trễ hạn - Dòng 557 - 576):**
     * So sánh: `if (now.after(hanTra))` $\rightarrow$ Tính độ lệch thời gian: `diffMs = now.getTime() - hanTra.getTime()`.
     * **Trường hợp trễ từ 1 ngày trở lên (`lateDays >= 1`):**
       $$\text{Tiền phạt} = \text{lateDays} \times 5.000\text{ VNĐ}$$
     * **Trường hợp trễ dưới 1 ngày (chế độ demo test nhanh theo phút, ví dụ test 1 phút):**
       Áp dụng mức phạt tối thiểu **5.000 VNĐ** kèm lý do *"Quá hạn X phút (Chế độ test nhanh)"*.
   * **Bước 5 (Tự động sinh Phiếu phạt - Dòng 583 - 600):**
     Nếu `fineAmount > 0`, hệ thống tự động thực thi câu lệnh SQL:
     ```sql
     INSERT INTO phieu_phat (ma_phieu_muon, so_tien_phat, ngay_lap, trang_thai, ly_do)
     VALUES (?, ?, now, 'CHUA_NOP_PHAT', ?);
     ```
4. Redirect về trang quản trị. Trên bảng lịch sử của độc giả và dashboard của Admin lập tức xuất hiện thông tin phạt và số tiền nợ chính xác!

---

### CHỨC NĂNG 10: Quản lý Kho sách (CRUD: Thêm, Sửa, Xóa Sách)
* **Giao diện (View):** [`web/admin/books.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/books.jsp), [`web/admin/book_form.jsp`](file:///C:/LapTrinhWeb/Doanweb/web/admin/book_form.jsp)
* **Bộ điều phối (Controller):** [`AdminServlet.java#L59-L79`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java#L59-L79), [`L144-L170`](file:///C:/LapTrinhWeb/Doanweb/src/controllers/AdminServlet.java#L144-L170)
* **Tầng dữ liệu (DAO):** [`BookDB.java#L183-L353`](file:///C:/LapTrinhWeb/Doanweb/src/data/BookDB.java#L183-L353)

#### 🔍 Chi tiết luồng code chạy:
* **Thêm mới hoặc Cập nhật sách (`save_book`, Dòng 144 - 170):**
  1. Thủ thư điền thông tin sách vào form $\rightarrow$ `POST /admin?action=save_book`.
  2. `AdminServlet` trích xuất các trường: tiêu đề, tác giả, thể loại, năm XB, giá bìa, số lượng tổng, vị trí kệ, mô tả, ảnh bìa.
  3. Nếu `maSach > 0` $\rightarrow$ gọi `BookDB.update(s)` thực thi `UPDATE sach SET ... WHERE ma_sach = ?`.
  4. Nếu `maSach == 0` (thêm mới) $\rightarrow$ gọi `BookDB.insert(s)` thực thi `INSERT INTO sach (...)`, đồng thời tự động tạo các bản sao sách với mã vạch `BC-XXXX-XX` trong bảng `ban_sao_sach`.
* **Xóa sách (`delete_book`, Dòng 75 - 79):**
  1. Thủ thư bấm nút Xóa $\rightarrow$ `GET /admin?action=delete_book&id=X`.
  2. `AdminServlet` gọi `BookDB.delete(id)` $\rightarrow$ thực thi `DELETE FROM sach WHERE ma_sach = ?`.
* **Kỹ thuật Phòng thủ Tồn kho (`adjustStock`, Dòng 329 - 353 trong `BookDB.java`):**
  Sử dụng câu lệnh SQL phòng thủ chống race condition:
  ```sql
  UPDATE sach SET so_luong_con = GREATEST(0, LEAST(so_luong_tong, so_luong_con + ?)) WHERE ma_sach = ?;
  ```
  Hàm `LEAST` ngăn không cho tồn kho vượt quá tổng số sách nhập vào; hàm `GREATEST` ngăn không cho tồn kho bị số âm.

---

## 💡 3. BỘ CÂU HỎI THƯỜNG GẶP CỦA HỘI ĐỒNG VÀ KỊCH BẢN TRẢ LỜI MẪU

### Câu hỏi 1: "Mô hình MVC trong đồ án của em hoạt động như thế nào? Chỉ rõ đường đi của một request?"
> **Kịch bản trả lời:**  
> *"Dạ thưa Thầy/Cô, đồ án của em áp dụng chuẩn mô hình **MVC 2**. Khi người dùng gửi request từ trình duyệt (ví dụ bấm mượn sách):  
> 1. Request đi qua web server Tomcat và được định tuyến đến Controller tương ứng (ở đây là `BorrowServlet`).  
> 2. Controller chịu trách nhiệm kiểm tra session đăng nhập và gọi tầng Model/DAO (`BorrowDB`).  
> 3. Tầng DAO mở kết nối JDBC với MySQL, thực thi câu lệnh SQL với `PreparedStatement` và trả về kết quả cho Controller.  
> 4. Cuối cùng, Controller đóng gói dữ liệu vào Request scope và chuyển tiếp (forward/redirect) sang tầng View (các file JSP) để hiển thị giao diện cho người dùng. Toàn bộ logic nghiệp vụ hoàn toàn không viết trên file JSP."*

### Câu hỏi 2: "Tại sao em phải dùng Transaction khi mượn sách? Nó nằm ở file nào, dòng bao nhiêu?"
> **Kịch bản trả lời:**  
> *"Dạ thưa Thầy/Cô, Transaction của em nằm ở file **`BorrowDB.java` từ dòng 178 đến dòng 222**.  
> Lý do bắt buộc phải dùng Transaction vì nghiệp vụ mượn sách bao gồm nhiều thao tác CSDL phụ thuộc lẫn nhau: (1) Tạo phiếu mượn, (2) Tạo từng dòng chi tiết phiếu mượn, và (3) Trừ tồn kho bản sao sách.  
> Em sử dụng `con.setAutoCommit(false)` để gộp các lệnh này thành một giao dịch nguyên tử. Nếu mọi lệnh thành công thì em gọi `con.commit()`. Nhưng nếu giữa chừng xảy ra sự cố (ví dụ mất điện, lỗi mạng, sách bị trùng mã), khối `catch` sẽ lập tức gọi `con.rollback()` để hoàn tác toàn bộ về trạng thái ban đầu, đảm bảo tính toàn vẹn dữ liệu ACID và không sinh ra dữ liệu rác."*

### Câu hỏi 3: "Em làm thế nào để chống tấn công SQL Injection?"
> **Kịch bản trả lời:**  
> *"Dạ thưa Thầy/Cô, trong toàn bộ các lớp DAO của đồ án (`BookDB`, `BorrowDB`, `UserDB`, `CategoryDB`), em tuyệt đối không sử dụng nối chuỗi câu lệnh SQL với dữ liệu người dùng nhập.  
> Thay vào đó, em sử dụng **`PreparedStatement`** với các dấu tham số `?`. Khi thực thi, trình điều khiển JDBC sẽ tiền biên dịch cấu trúc câu lệnh SQL trước rồi mới truyền giá trị tham số vào sau, giúp ngăn chặn triệt để mọi hành vi chèn mã độc SQL Injection."*

### Câu hỏi 4: "Tại sao trong `BorrowServlet` có chỗ dùng `forward` nhưng có chỗ lại dùng `sendRedirect`?"
> **Kịch bản trả lời:**  
> *"Dạ thưa Thầy/Cô:  
> - Em dùng **`forward`** ở hàm `doGet()` (Dòng 55) khi cần chuyển dữ liệu từ Servlet sang trang `history.jsp` vì `forward` là chuyển tiếp nội bộ trên Server, giữ nguyên được các thuộc tính trong Request Scope.  
> - Còn ở hàm `doPost()` sau khi tạo phiếu mượn thành công (Dòng 103), em dùng **`sendRedirect`** để chuyển hướng trình duyệt theo mô hình **PRG (Post/Redirect/Get)**. Việc này giúp trình duyệt đổi sang phương thức GET, tránh trường hợp người dùng vô tình bấm phím F5 (Refresh) làm gửi lại dữ liệu form POST gây mượn sách hai lần trùng lặp."*

### Câu hỏi 5: "Thuật toán tính phạt trễ hạn được cài đặt như thế nào?"
> **Kịch bản trả lời:**  
> *"Dạ thưa Thầy/Cô, thuật toán tính phạt nằm trong hàm `returnBooks()` tại file **`BorrowDB.java` từ dòng 557 đến dòng 600**.  
> Khi thủ thư bấm nhận trả sách, hệ thống so sánh thời gian hiện tại `now` với `hanTra` của phiếu mượn. Nếu `now.after(hanTra)`:  
> - Em tính khoảng cách mili-giây: `diffMs = now.getTime() - hanTra.getTime()`.  
> - Nếu quá hạn từ 1 ngày trở lên, tiền phạt = `lateDays * 5.000 VNĐ` theo quy định thư viện.  
> - Nếu quá hạn dưới 1 ngày (phục vụ chế độ test nhanh theo phút mà em tích hợp), hệ thống phạt mức tượng trưng 5.000 VNĐ.  
> - Sau đó hệ thống tự động sinh một bản ghi vào bảng `phieu_phat` với trạng thái `CHUA_NOP_PHAT` để theo dõi công nợ."*
