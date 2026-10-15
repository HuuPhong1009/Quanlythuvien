package controllers;

import data.UserDB;
import model.DocGia;
import model.NguoiDung;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ============================================================================
 * CONTROLLER: UserServlet (Servlet Quản lý Người dùng & Xác thực)
 * ----------------------------------------------------------------------------
 * VAI TRÒ TRONG KIẾN TRÚC MVC:
 * - Đóng vai trò là TẦNG CONTROLLER chuyên trách xác thực (Authentication).
 * - Quản lý việc Đăng nhập, Đăng xuất (Logout), Đăng ký tài khoản độc giả mới.
 * - Thiết lập phiên làm việc (HttpSession) và phân quyền:
 *   + Nếu là Quản trị viên (Admin) -> Chuyển hướng tới trang /admin.
 *   + Nếu là Độc giả (Reader) -> Chuyển hướng tới danh mục sách /catalog.
 *
 * CÁC HÀNH ĐỘNG HỖ TRỢ (ACTION PARAMETERS):
 * - GET  /user?action=login    : Hiển thị form đăng nhập (/login.jsp).
 * - GET  /user?action=register : Hiển thị form đăng ký độc giả (/register.jsp).
 * - GET  /user?action=logout   : Hủy session, đăng xuất an toàn.
 * - POST /user?action=login    : Xử lý kiểm tra email & mật khẩu từ form đăng nhập.
 * - POST /user?action=register : Tiếp nhận dữ liệu form và tạo tài khoản mới.
 * ============================================================================
 */
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * PHƯƠNG THỨC doGet(): Điều hướng hiển thị giao diện theo yêu cầu (HTTP GET)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "login";

        // TRƯỜNG HỢP 1: ĐĂNG XUẤT (LOGOUT)
        if ("logout".equals(action)) {
            // Lấy session hiện tại (tham số false: nếu không có session thì không tạo mới)
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Hủy bỏ toàn bộ session: Xóa thông tin đăng nhập và giỏ mượn sách
                session.invalidate();
            }
            // Điều hướng người dùng về trang danh mục sách công khai
            response.sendRedirect("catalog");
        } 
        // TRƯỜNG HỢP 2: HIỂN THỊ TRANG ĐĂNG KÝ
        else if ("register".equals(action)) {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } 
        // TRƯỜNG HỢP 3: HIỂN THỊ TRANG ĐĂNG NHẬP MẶC ĐỊNH
        else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * PHƯƠNG THỨC doPost(): Tiếp nhận và xử lý dữ liệu Form (HTTP POST)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("register".equals(action)) {
            handleRegister(request, response);
        } else {
            handleLogin(request, response);
        }
    }

    /**
     * XỬ LÝ ĐĂNG NHẬP: handleLogin()
     * - Nhận email và password từ form gửi lên.
     * - Gọi UserDB.login(email, password) để kiểm tra trong CSDL (có mã hóa mật khẩu).
     * - Nếu đúng: Lưu đối tượng NguoiDung vào session với key "currentUser" và phân quyền điều hướng.
     * - Nếu sai: Đính kèm thông báo lỗi và forward về lại /login.jsp.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Gọi tầng DAO để xác thực
        NguoiDung user = UserDB.login(email, password);

        if (user != null) {
            // Đăng nhập thành công -> Lưu vào HttpSession
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);

            // Phân quyền điều hướng dựa vào Role
            if (user.isQuanTri()) {
                // Là Admin/Thủ thư -> Chuyển về Dashboard quản trị
                response.sendRedirect("admin");
            } else {
                // Là Độc giả bình thường -> Chuyển về trang danh mục sách
                response.sendRedirect("catalog");
            }
        } else {
            // Đăng nhập thất bại -> Gắn thông báo lỗi vào Request Scope
            request.setAttribute("errorMessage", "Email hoặc mật khẩu không chính xác!");
            request.setAttribute("email", email); // Giữ lại email người dùng vừa nhập để không phải gõ lại
            // Dùng forward để giữ lại thông báo lỗi và giá trị input
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * XỬ LÝ ĐĂNG KÝ TÀI KHOẢN MỚI: handleRegister()
     * - Đọc các trường: Họ tên, Email, Số điện thoại, Mật khẩu, Loại độc giả.
     * - Đóng gói vào đối tượng DocGia (Model).
     * - Gọi UserDB.register(dg) để lưu vào CSDL MySQL.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String readerType = request.getParameter("readerType");

        // Đóng gói dữ liệu vào Model DocGia
        DocGia dg = new DocGia();
        dg.setHoTen(fullName);
        dg.setEmail(email);
        dg.setSoDienThoai(phone);
        dg.setMatKhau(password);
        dg.setLoaiDocGia(readerType);

        // Gọi DAO ghi vào bảng nguoi_dung và doc_gia
        boolean ok = UserDB.register(dg);
        if (ok) {
            // Đăng ký thành công -> Chuyển về trang đăng nhập kèm cờ registered=true để hiện câu chúc mừng
            response.sendRedirect("user?action=login&registered=true");
        } else {
            // Đăng ký thất bại (ví dụ email đã tồn tại) -> Giữ lại form và báo lỗi
            request.setAttribute("errorMessage", "Đăng ký không thành công. Email có thể đã tồn tại trong hệ thống!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
