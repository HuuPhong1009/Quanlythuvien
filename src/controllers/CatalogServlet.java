package controllers;

import data.BookDB;
import data.CategoryDB;
import data.ReviewDB;
import model.DanhGia;
import model.NguoiDung;
import model.Sach;
import model.TheLoai;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * CONTROLLER: CatalogServlet (Servlet Quản lý Danh mục Sách & Tìm kiếm & Đánh giá)
 * ----------------------------------------------------------------------------
 * VAI TRÒ TRONG KIẾN TRÚC MVC:
 * - Đóng vai trò là TẦNG CONTROLLER phục vụ trang chủ và tra cứu sách.
 * - Nhận các tham số tìm kiếm từ khóa (?q=...), lọc theo thể loại (?cat=...),
 *   phân trang (?page=...).
 * - Thực hiện thuật toán PHÂN TRANG (Pagination) ở tầng ứng dụng hoặc CSDL.
 * - Hiển thị chi tiết sách (/book_detail.jsp) và tiếp nhận bình luận/đánh giá sao.
 *
 * CÁC ĐƯỜNG DẪN ÁNH XẠ (URL MAPPING):
 * - GET  /catalog             : Trang chủ / Danh mục sách có phân trang & tìm kiếm.
 * - GET  /catalog?action=detail&id=X : Xem chi tiết 1 cuốn sách cụ thể.
 * - POST /catalog?action=review      : Gửi đánh giá sao và nhận xét cho cuốn sách.
 * ============================================================================
 */
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * PHƯƠNG THỨC doGet(): Xử lý xem danh sách hoặc xem chi tiết sách
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        // Tách nhánh xử lý: Xem chi tiết hay Xem danh mục
        if ("detail".equals(action)) {
            showDetail(request, response);
        } else {
            showList(request, response);
        }
    }

    /**
     * PHƯƠNG THỨC doPost(): Tiếp nhận đánh giá sách từ độc giả
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("review".equals(action)) {
            addReview(request, response);
        } else {
            doGet(request, response);
        }
    }

    /**
     * XỬ LÝ HIỂN THỊ DANH MỤC SÁCH + TÌM KIẾM + PHÂN TRANG: showList()
     * - Kỹ thuật thuật toán phân trang:
     *   1. pageSize: Số sách hiển thị trên 1 trang (ở đây đặt là 12 cuốn).
     *   2. totalPages = ceil(totalCount / pageSize): Tổng số trang.
     *   3. fromIndex = (currentPage - 1) * pageSize: Vị trí bắt đầu cắt mảng.
     *   4. toIndex = min(fromIndex + pageSize, totalCount): Vị trí kết thúc.
     */
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đọc từ khóa tìm kiếm (q) và mã thể loại (cat)
        String keyword = request.getParameter("q");
        String catStr = request.getParameter("cat");
        int categoryId = 0;
        try {
            if (catStr != null && !catStr.isEmpty()) {
                categoryId = Integer.parseInt(catStr);
            }
        } catch (NumberFormatException e) {
            categoryId = 0;
        }

        // Gọi BookDB.search() để lấy danh sách thỏa mãn điều kiện lọc
        List<Sach> allBooks = BookDB.search(keyword, categoryId);
        // Lấy tất cả thể loại sách để hiển thị menu dropdown lọc
        List<TheLoai> categories = CategoryDB.getAll();

        // --- THUẬT TOÁN PHÂN TRANG (PAGINATION) ---
        int pageSize = 12; // 12 cuốn / trang
        int totalCount = allBooks.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages < 1) totalPages = 1;

        // Đọc số trang hiện tại từ request (?page=...)
        int currentPage = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                currentPage = Integer.parseInt(pageStr);
            }
        } catch (NumberFormatException ignored) {
            currentPage = 1;
        }
        // Giới hạn biên an toàn: không cho page < 1 hoặc vượt quá totalPages
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        // Cắt danh sách theo trang hiện tại (subList)
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalCount);
        List<Sach> pagedBooks = (fromIndex < totalCount)
                ? allBooks.subList(fromIndex, toIndex)
                : new java.util.ArrayList<Sach>();

        // Đóng gói các biến vào Request Scope để chuyển sang JSP
        request.setAttribute("books", pagedBooks);
        request.setAttribute("categories", categories);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("selectedCat", categoryId);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        // Chuyển tiếp sang index.jsp để render giao diện
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * XỬ LÝ XEM CHI TIẾT SÁCH: showDetail()
     * - Lấy thông tin cuốn sách theo ID.
     * - Lấy danh sách các đánh giá / bình luận của độc giả về cuốn sách này.
     * - Forward sang /book_detail.jsp.
     */
    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("catalog");
            return;
        }

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("catalog");
            return;
        }

        // Lấy sách theo ID
        Sach book = BookDB.getById(id);
        if (book == null) {
            response.sendRedirect("catalog");
            return;
        }

        // Lấy danh sách đánh giá sao từ ReviewDB
        List<DanhGia> reviews = ReviewDB.getByBook(id);
        request.setAttribute("book", book);
        request.setAttribute("reviews", reviews);

        request.getRequestDispatcher("/book_detail.jsp").forward(request, response);
    }

    /**
     * TIẾP NHẬN ĐÁNH GIÁ SÁCH: addReview()
     * - Kiểm tra người dùng đã đăng nhập chưa.
     * - Nhận số sao (1-5 sao) và nội dung nhận xét.
     * - Lưu vào bảng danh_gia trong CSDL.
     */
    private void addReview(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("user?action=login");
            return;
        }

        int bookId = Integer.parseInt(request.getParameter("bookId"));
        int stars = Integer.parseInt(request.getParameter("stars"));
        String comment = request.getParameter("comment");

        // Đóng gói Model DanhGia
        DanhGia dg = new DanhGia();
        dg.setMaSach(bookId);
        dg.setMaNguoiDung(user.getMaNguoiDung());
        dg.setTenNguoiDung(user.getHoTen());
        dg.setSoSao(stars);
        dg.setNoiDung(comment != null ? comment.trim() : "");
        dg.setNgayDang(new java.util.Date());

        // Ghi vào CSDL
        ReviewDB.add(dg);

        // Chuyển hướng quay lại trang chi tiết kèm cờ reviewed=true
        response.sendRedirect("catalog?action=detail&id=" + bookId + "&reviewed=true");
    }
}
