package controllers;

import data.BorrowDB;
import model.GioMuon;
import model.NguoiDung;
import model.PhieuMuon;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * CONTROLLER: BorrowServlet (Servlet điều phối nghiệp vụ Mượn sách & Lịch sử mượn)
 * ----------------------------------------------------------------------------
 * VAI TRÒ TRONG KIẾN TRÚC MVC:
 * - Đóng vai trò là TẦNG CONTROLLER.
 * - Nhận HTTP Request từ Client (trình duyệt), giải mã dữ liệu form/URL.
 * - Kiểm tra phiên đăng nhập (Authentication) thông qua HttpSession.
 * - Gọi TẦNG MODEL/DAO (BorrowDB) để thực thi các nghiệp vụ thêm/truy vấn CSDL.
 * - Điều hướng kết quả tới TẦNG VIEW (history.jsp hoặc cart.jsp) qua forward hoặc redirect.
 *
 * CÁC ĐƯỜNG DẪN ÁNH XẠ (URL MAPPING):
 * - GET  /borrow?action=history : Xem danh sách phiếu mượn của độc giả đang đăng nhập.
 * - POST /borrow                : Xử lý xác nhận mượn toàn bộ sách có trong giỏ hàng.
 * ============================================================================
 */
public class BorrowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * PHƯƠNG THỨC doGet(): Xử lý các yêu cầu lấy dữ liệu hiển thị (HTTP GET)
     * - Mục đích: Hiển thị lịch sử mượn sách của độc giả hiện tại (/history.jsp).
     * - Kỹ thuật áp dụng:
     *   1. request.setCharacterEncoding("UTF-8"): Đảm bảo tiếng Việt không bị lỗi font.
     *   2. session.getAttribute("currentUser"): Kiểm tra quyền truy cập (Authentication).
     *   3. request.setAttribute("borrows", list): Gắn dữ liệu vào request scope để JSP đọc được.
     *   4. request.getRequestDispatcher(...).forward(...): Chuyển tiếp nội bộ trên Server.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập bảng mã UTF-8 cho cả request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // BƯỚC 1: KIỂM TRA ĐĂNG NHẬP QUA SESSION
        // Lấy session hiện tại. Nếu chưa đăng nhập (user == null), chuyển hướng bắt đăng nhập.
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        if (user == null) {
            // Dùng sendRedirect vì chuyển hướng sang URL khác hoàn toàn (Trang đăng nhập)
            response.sendRedirect("user?action=login");
            return;
        }

        // BƯỚC 2: PHÂN TÍCH THAM SỐ ACTION
        String action = request.getParameter("action");
        if (action == null) {
            action = "history";
        }

        // BƯỚC 3: XỬ LÝ LẤY DỮ LIỆU LỊCH SỬ MƯỢN SÁCH
        if ("history".equals(action)) {
            // Gọi tầng DAO (BorrowDB) để truy vấn danh sách phiếu mượn theo mã độc giả (ma_doc_gia)
            List<PhieuMuon> borrows = BorrowDB.getByReader(user.getMaNguoiDung());

            // Đặt danh sách phiếu mượn vào Request Scope với tên "borrows"
            request.setAttribute("borrows", borrows);

            // Forward sang trang history.jsp để hiển thị (View chỉ lo việc render giao diện)
            request.getRequestDispatcher("/history.jsp").forward(request, response);
        } else {
            response.sendRedirect("catalog");
        }
    }

    /**
     * PHƯƠNG THỨC doPost(): Xử lý các yêu cầu gửi dữ liệu thay đổi trạng thái (HTTP POST)
     * - Mục đích: Xác nhận tạo phiếu mượn sách từ giỏ hàng (/cart.jsp).
     * - Kỹ thuật áp dụng:
     *   1. Thread-Safe Session Locking: Dùng synchronized trên session id để chống race-condition
     *      (ngăn người dùng vô tình bấm đúp 2 lần liên tiếp tạo ra 2 phiếu trùng lặp).
     *   2. Database Transaction: Gọi BorrowDB.createBorrow để thực thi thêm phiếu mượn + trừ kho.
     *   3. Post/Redirect/Get (PRG) Pattern: Sau khi POST thành công, dùng sendRedirect thay vì forward
     *      để ngăn người dùng bấm F5 bị submit lại đơn mượn.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập bảng mã UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // BƯỚC 1: KIỂM TRA ĐĂNG NHẬP
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("user?action=login&message=Vui+long+dang+nhap+de+muon+sach");
            return;
        }

        // BƯỚC 2: TRUY XUẤT GIỎ HÀNG AN TOÀN ĐA LUỒNG (THREAD-SAFE)
        final Object lock = session.getId().intern();
        GioMuon cart;
        synchronized (lock) {
            cart = (GioMuon) session.getAttribute("borrowCart");
        }

        // BƯỚC 3: KIỂM TRA ĐIỀU KIỆN GIỎ MƯỢN
        // Nếu giỏ hàng rỗng thì không cho tạo phiếu, điều hướng về lại giỏ sách
        if (cart == null || cart.getDanhSachSach().isEmpty()) {
            response.sendRedirect("cart");
            return;
        }

        // BƯỚC 4: THU THẬP CÁC THAM SỐ TỪ FORM ĐĂNG KÝ MƯỢN
        String ghiChu = request.getParameter("notes");              // Ghi chú mượn sách
        String loanDuration = request.getParameter("loanDuration");  // Thời hạn: 14_DAYS, 1_MINUTE, CUSTOM...
        String customDue = request.getParameter("customDue");        // Ngày giờ tùy chỉnh (nếu chọn CUSTOM)

        // BƯỚC 5: GỌI TẦNG DAO ĐỂ THỰC THI GIAO DỊCH TẠO PHIẾU (TRANSACTION)
        // Hàm này sẽ tự động:
        // - INSERT bảng phieu_muon
        // - INSERT các dòng vào chi_tiet_phieu_muon
        // - UPDATE giảm số lượng sách khả dụng trong kho (ban_sao_sach / so_luong_con)
        boolean success = BorrowDB.createBorrow(
                user.getMaNguoiDung(),
                user.getHoTen(),
                cart,
                ghiChu,
                loanDuration,
                customDue
        );

        // BƯỚC 6: XỬ LÝ KẾT QUẢ VÀ ĐIỀU HƯỚNG THEO MÔ HÌNH PRG (POST/REDIRECT/GET)
        if (success) {
            // Mượn thành công -> Làm sạch giỏ hàng trong session
            synchronized (lock) {
                cart.xoaHet();
                session.setAttribute("borrowCart", cart);
            }
            // Chuyển hướng sang trang lịch sử với thông báo thành công
            response.sendRedirect("borrow?action=history&success=true");
        } else {
            // Mượn thất bại (ví dụ sách đã hết tồn kho) -> Trở về giỏ và báo lỗi
            response.sendRedirect("cart?error=true");
        }
    }
}
