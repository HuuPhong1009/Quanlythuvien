package controllers;

import data.BookDB;
import model.ChiTietGioMuon;
import model.GioMuon;
import model.Sach;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ============================================================================
 * CONTROLLER: BorrowCartServlet (Servlet quản lý Giỏ mượn sách)
 * ----------------------------------------------------------------------------
 * VAI TRÒ TRONG KIẾN TRÚC MVC:
 * - Đóng vai trò là TẦNG CONTROLLER quản lý trạng thái giỏ mượn.
 * - Giỏ mượn được lưu trong HttpSession (Session Scope), giúp mỗi người dùng
 *   sở hữu một giỏ mượn độc lập mà không cần phải ghi dữ liệu liên tục vào CSDL.
 * - Tự động đối chiếu số lượng mượn với số lượng tồn kho thực tế trong MySQL
 *   (phương thức syncCartWithLatestStock) để chống việc mượn sách đã hết.
 *
 * CÁC HÀNH ĐỘNG HỖ TRỢ (ACTION PARAMETERS):
 * - GET  /cart                : Xem chi tiết giỏ mượn hiện tại (/cart.jsp).
 * - POST /cart?action=add     : Thêm sách vào giỏ (từ trang danh mục hoặc chi tiết sách).
 * - POST /cart?action=update  : Tăng/giảm số lượng của một cuốn sách trong giỏ.
 * - POST /cart?action=remove  : Xóa 1 cuốn sách cụ thể ra khỏi giỏ.
 * - POST /cart?action=clear   : Xóa toàn bộ giỏ mượn.
 * ============================================================================
 */
public class BorrowCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * HÀM TIỆN ÍCH: syncCartWithLatestStock()
     * - Mục đích: Đồng bộ giỏ mượn với số lượng tồn kho khả dụng mới nhất từ CSDL.
     */
    private void syncCartWithLatestStock(GioMuon cart) {
        if (cart == null) return;
        // Duyệt ngược từ cuối danh sách về đầu để xóa phần tử an toàn (không bị IndexOutOfBoundsException)
        for (int i = cart.getDanhSachSach().size() - 1; i >= 0; i--) {
            ChiTietGioMuon item = cart.getDanhSachSach().get(i);
            // Lấy thông tin sách tươi mới nhất từ CSDL
            Sach fresh = BookDB.getById(item.getSach().getMaSach());
            if (fresh != null) {
                item.setSach(fresh);
                // Trường hợp 1: Sách trong kho đã hết sạch (soLuongCon <= 0) -> Xóa khỏi giỏ
                if (fresh.getSoLuongCon() <= 0) {
                    cart.getDanhSachSach().remove(i);
                } 
                // Trường hợp 2: Số lượng mượn trong giỏ vượt quá số lượng còn lại trong kho -> Ép về mức tối đa
                else if (item.getSoLuong() > fresh.getSoLuongCon()) {
                    item.setSoLuong(fresh.getSoLuongCon());
                }
            }
        }
    }

    /**
     * PHƯƠNG THỨC doGet(): Xử lý xem giỏ mượn (HTTP GET)
     * - Lấy đối tượng GioMuon từ HttpSession.
     * - Đồng bộ lại số lượng tồn kho mới nhất.
     * - Chuyển tiếp (forward) sang trang /cart.jsp để hiển thị bảng danh sách sách.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        final Object lock = session.getId().intern();
        synchronized (lock) {
            GioMuon cart = (GioMuon) session.getAttribute("borrowCart");
            if (cart != null) {
                syncCartWithLatestStock(cart);
                session.setAttribute("borrowCart", cart);
            }
        }

        // Chuyển tiếp sang giao diện giỏ mượn
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    /**
     * PHƯƠNG THỨC doPost(): Xử lý các thao tác thêm, cập nhật, xóa sách trong giỏ (HTTP POST)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        // BƯỚC 1: LẤY HOẶC KHỞI TẠO GIỎ MƯỢN TRONG SESSION
        HttpSession session = request.getSession();
        final Object lock = session.getId().intern();
        GioMuon cart;
        synchronized (lock) {
            cart = (GioMuon) session.getAttribute("borrowCart");
            if (cart == null) {
                cart = new GioMuon();
            }
            // Luôn đồng bộ dữ liệu tồn kho trước khi thực hiện thao tác
            syncCartWithLatestStock(cart);
        }

        boolean clampedToMax = false;

        // BƯỚC 2: PHÂN LOẠI VÀ XỬ LÝ THEO THAM SỐ ACTION
        if ("add".equals(action)) {
            // Thao tác: Thêm sách vào giỏ
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            int addQty = 1;
            try {
                if (request.getParameter("quantity") != null) {
                    addQty = Integer.parseInt(request.getParameter("quantity"));
                }
            } catch (NumberFormatException ignored) {
                addQty = 1;
            }
            Sach book = BookDB.getById(bookId);
            // Chỉ cho phép thêm nếu sách tồn tại và còn số lượng trong kho
            if (book != null && book.isConSach()) {
                synchronized (lock) {
                    cart.themSach(book, addQty);
                }
            }
        } else if ("update".equals(action)) {
            // Thao tác: Cập nhật lại số lượng sách (ví dụ người dùng bấm nút + hoặc -)
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            int quantity = 1;
            try {
                quantity = Integer.parseInt(request.getParameter("quantity"));
            } catch (NumberFormatException e) {
                quantity = 1;
            }
            // Kiểm tra ràng buộc: Không cho mượn vượt quá số lượng đang có trong kho
            Sach fresh = BookDB.getById(bookId);
            if (fresh != null && quantity > fresh.getSoLuongCon()) {
                quantity = fresh.getSoLuongCon();
                clampedToMax = true;
            }
            synchronized (lock) {
                cart.capNhatSoLuong(bookId, quantity);
            }
        } else if ("remove".equals(action)) {
            // Thao tác: Xóa 1 cuốn sách ra khỏi giỏ
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            synchronized (lock) {
                cart.xoaSach(bookId);
            }
        } else if ("clear".equals(action)) {
            // Thao tác: Xóa trắng toàn bộ giỏ mượn
            synchronized (lock) {
                cart.xoaHet();
            }
        }

        // BƯỚC 3: CẬP NHẬT LẠI GIỎ VÀO SESSION
        synchronized (lock) {
            session.setAttribute("borrowCart", cart);
        }

        // BƯỚC 4: ĐIỀU HƯỚNG QUAY LẠI TRANG GIỎ (Áp dụng PRG Pattern)
        if (clampedToMax) {
            response.sendRedirect("cart?maxReached=true");
        } else {
            response.sendRedirect("cart");
        }
    }
}
