package controllers;

import data.BookDB;
import data.BorrowDB;
import data.CategoryDB;
import model.NguoiDung;
import model.PhieuMuon;
import model.Sach;
import model.TheLoai;
import model.ThuThu;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * CONTROLLER: AdminServlet (Servlet Quản trị Hệ thống Thư viện)
 * ----------------------------------------------------------------------------
 * VAI TRÒ TRONG KIẾN TRÚC MVC:
 * - Đóng vai trò là TẦNG CONTROLLER dành riêng cho Quản trị viên (Admin / Thủ thư).
 * - Thực hiện kiểm soát truy cập (Authorization) bằng hàm checkAdmin(): Ngăn chặn
 *   độc giả hoặc người dùng vãng lai xâm nhập trái phép vào khu vực quản trị.
 * - Điều phối 4 nhóm chức năng quản trị cốt lõi:
 *   1. Dashboard: Thống kê tổng số sách, số lượt mượn, số phiếu cần duyệt.
 *   2. Quản lý sách (CRUD): Xem danh sách, thêm sách mới, cập nhật, xóa sách.
 *   3. Phê duyệt mượn sách: Duyệt đơn (approve) hoặc Từ chối đơn (reject).
 *   4. Thu hồi sách (return): Xác nhận độc giả trả sách, tự động tính tiền phạt trễ hạn.
 *
 * CÁC HÀNH ĐỘNG HỖ TRỢ (ACTION PARAMETERS):
 * - GET  /admin?action=dashboard   : Bảng điều khiển tổng quan (/admin/dashboard.jsp).
 * - GET  /admin?action=books       : Danh sách quản lý toàn bộ sách (/admin/books.jsp).
 * - GET  /admin?action=book_form   : Mở form thêm mới hoặc sửa sách (/admin/book_form.jsp).
 * - GET  /admin?action=delete_book : Xóa sách theo mã sách (?id=X).
 * - GET  /admin?action=borrows     : Danh sách quản lý mượn trả (/admin/borrows.jsp).
 * - GET  /admin?action=approve     : Thủ thư duyệt phiếu mượn (?id=X).
 * - GET  /admin?action=return      : Thủ thư thu hồi sách đã mượn (?id=X).
 * - GET  /admin?action=reject      : Thủ thư từ chối yêu cầu mượn (?id=X&reason=...).
 * - POST /admin?action=save_book   : Tiếp nhận form lưu sách (thêm mới hoặc cập nhật).
 * ============================================================================
 */
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * HÀM BẢO MẬT PHÂN QUYỀN: checkAdmin()
     * - Mục đích: Xác minh người dùng đang gửi request có quyền Admin hay không.
     * - Logic: Lấy currentUser từ session -> nếu null hoặc không phải Quản trị
     *   thì lập tức điều hướng về trang đăng nhập và trả về false để dừng xử lý.
     */
    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        if (user == null || !user.isQuanTri()) {
            response.sendRedirect("user?action=login&message=Vui+long+dang+nhap+tai+khoan+Quan+tri");
            return false;
        }
        return true;
    }

    /**
     * PHƯƠNG THỨC doGet(): Xử lý các yêu cầu lấy thông tin hoặc thao tác qua liên kết GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // BƯỚC 1: KIỂM TRA PHÂN QUYỀN
        if (!checkAdmin(request, response)) return;

        String action = request.getParameter("action");
        if (action == null) action = "dashboard";

        // BƯỚC 2: PHÂN NHÁNH XỬ LÝ THEO ACTION
        if ("books".equals(action)) {
            // Xem danh sách toàn bộ sách trong kho
            List<Sach> books = BookDB.getAll();
            request.setAttribute("books", books);
            request.getRequestDispatcher("/admin/books.jsp").forward(request, response);
        } else if ("book_form".equals(action)) {
            // Mở form thêm mới hoặc chỉnh sửa thông tin sách
            String idStr = request.getParameter("id");
            Sach book = null;
            if (idStr != null && !idStr.isEmpty()) {
                book = BookDB.getById(Integer.parseInt(idStr));
            }
            List<TheLoai> categories = CategoryDB.getAll();
            request.setAttribute("book", book);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/admin/book_form.jsp").forward(request, response);
        } else if ("delete_book".equals(action)) {
            // Xóa một cuốn sách khỏi hệ thống theo mã sách (ID)
            int id = Integer.parseInt(request.getParameter("id"));
            BookDB.delete(id);
            response.sendRedirect("admin?action=books");
        } else if ("borrows".equals(action)) {
            // Trang danh sách phiếu mượn trả
            List<PhieuMuon> borrows = BorrowDB.getAll();
            request.setAttribute("borrows", borrows);
            request.setAttribute("pendingCount", BorrowDB.countPendingBorrows());
            request.getRequestDispatcher("/admin/borrows.jsp").forward(request, response);
        } else if ("approve".equals(action)) {
            // Thủ thư duyệt yêu cầu mượn: Chuyển trạng thái từ CHO_DUYET -> DANG_MUON
            int id = Integer.parseInt(request.getParameter("id"));
            HttpSession session = request.getSession();
            NguoiDung u = (NguoiDung) session.getAttribute("currentUser");
            ThuThu tt = (u instanceof ThuThu) ? (ThuThu) u : new ThuThu(u != null ? u.getMaNguoiDung() : 1, u != null ? u.getHoTen() : "Thủ Thư Quản Trị", "admin@library.edu.vn", "0901234567", "123456", new java.util.Date());
            BorrowDB.approve(id, tt);

            String from = request.getParameter("from");
            if ("dashboard".equals(from)) {
                response.sendRedirect("admin?action=dashboard&msg=approved&id=" + id);
            } else {
                response.sendRedirect("admin?action=borrows&msg=approved&id=" + id);
            }
        } else if ("return".equals(action)) {
            // Thủ thư xác nhận độc giả trả sách:
            // 1. Chuyển trạng thái phiếu sang DA_TRA.
            // 2. Tự động kiểm tra nếu trả trễ hạn thì sinh Phiếu phạt (PhieuPhat).
            // 3. Tự động cộng lại số lượng sách vào kho khả dụng.
            int id = Integer.parseInt(request.getParameter("id"));
            BorrowDB.returnBooks(id);

            String from = request.getParameter("from");
            if ("dashboard".equals(from)) {
                response.sendRedirect("admin?action=dashboard&msg=returned&id=" + id);
            } else {
                response.sendRedirect("admin?action=borrows&msg=returned&id=" + id);
            }
        } else if ("reject".equals(action)) {
            // Thủ thư từ chối yêu cầu mượn (ví dụ độc giả đang bị nợ phạt hoặc sách hỏng)
            int id = Integer.parseInt(request.getParameter("id"));
            String reason = request.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Thủ thư không phê duyệt yêu cầu mượn này";
            }
            BorrowDB.reject(id, reason);

            String from = request.getParameter("from");
            if ("dashboard".equals(from)) {
                response.sendRedirect("admin?action=dashboard&msg=rejected&id=" + id);
            } else {
                response.sendRedirect("admin?action=borrows&msg=rejected&id=" + id);
            }
        } else {
            // Trang tổng quan Dashboard: Thống kê số liệu trực quan
            List<Sach> books = BookDB.getAll();
            List<PhieuMuon> borrows = BorrowDB.getAll();
            request.setAttribute("totalBooks", books.size());
            request.setAttribute("totalBorrows", borrows.size());
            request.setAttribute("pendingCount", BorrowDB.countPendingBorrows());
            request.setAttribute("borrows", borrows);
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }

    /**
     * PHƯƠNG THỨC doPost(): Tiếp nhận form lưu sách mới hoặc cập nhật sách cũ
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkAdmin(request, response)) return;

        String action = request.getParameter("action");
        if ("save_book".equals(action)) {
            String idStr = request.getParameter("maSach");
            Sach s = new Sach();
            s.setTieuDe(request.getParameter("tieuDe"));
            s.setTacGia(request.getParameter("tacGia"));
            s.setMaTheLoai(Integer.parseInt(request.getParameter("maTheLoai")));
            s.setNamXuatBan(Integer.parseInt(request.getParameter("namXuatBan")));
            s.setGiaBia(Double.parseDouble(request.getParameter("giaBia")));
            int soLuong = Integer.parseInt(request.getParameter("soLuong"));
            s.setSoLuongTong(soLuong);
            s.setSoLuongCon(soLuong);
            s.setViTriKe(request.getParameter("viTriKe"));
            s.setTinhTrang(request.getParameter("tinhTrang"));
            s.setMoTa(request.getParameter("moTa"));
            String hinhAnh = request.getParameter("hinhAnh");
            if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
                s.setHinhAnh(hinhAnh.trim());
            }

            // Nếu maSach > 0 -> Cập nhật sách đang có; nếu 0 hoặc rỗng -> Thêm sách mới
            if (idStr != null && !idStr.trim().isEmpty() && !"0".equals(idStr)) {
                s.setMaSach(Integer.parseInt(idStr));
                s.setSoLuongCon(Integer.parseInt(request.getParameter("soLuongCon")));
                BookDB.update(s);
            } else {
                BookDB.insert(s);
            }
            response.sendRedirect("admin?action=books");
        } else {
            doGet(request, response);
        }
    }
}
