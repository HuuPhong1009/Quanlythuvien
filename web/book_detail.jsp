<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.Sach" %>
<%@ page import="model.DanhGia" %>
<%@ page import="model.NguoiDung" %>
<%
    Sach book = (Sach) request.getAttribute("book");
    List<DanhGia> reviews = (List<DanhGia>) request.getAttribute("reviews");
    NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
    String reviewed = request.getParameter("reviewed");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    if (book == null) {
        response.sendRedirect("catalog");
        return;
    }
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-4">
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/catalog" class="text-decoration-none small text-secondary">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
        </a>
    </div>

    <div class="card border shadow-sm rounded-3 p-4 bg-white mb-4">
        <div class="row g-4 align-items-start">
            <div class="col-md-4 text-center">
                <img src="<%= book.getHinhAnh().startsWith("http") ? book.getHinhAnh() : (request.getContextPath() + "/" + book.getHinhAnh() + "?v=real2026") %>"
                     alt="<%= book.getTieuDe() %>"
                     class="book-cover-detail mb-3"
                     onerror="this.src='${pageContext.request.contextPath}/images/books/real_cover_1.jpg'">
            </div>

            <div class="col-md-8">
                <span class="badge bg-primary-subtle text-primary mb-2">
                    <%= (book.getTenTheLoai() != null) ? book.getTenTheLoai() : "Sách" %>
                </span>
                <h3 class="fw-bold mb-1"><%= book.getTieuDe() %></h3>
                <p class="text-muted mb-3">Tác giả: <strong><%= book.getTacGia() %></strong></p>

                <div class="row g-2 mb-3 small bg-light p-3 rounded-3">
                    <div class="col-6 col-sm-3">
                        <span class="text-muted d-block">Nhà xuất bản</span>
                        <strong><%= (book.getTenNxb() != null) ? book.getTenNxb() : "-" %></strong>
                    </div>
                    <div class="col-6 col-sm-3">
                        <span class="text-muted d-block">Năm XB</span>
                        <strong><%= book.getNamXuatBan() %></strong>
                    </div>
                    <div class="col-6 col-sm-3">
                        <span class="text-muted d-block">Vị trí kệ</span>
                        <strong><%= book.getViTriKe() %></strong>
                    </div>
                    <div class="col-6 col-sm-3">
                        <span class="text-muted d-block">Sách đang có</span>
                        <% if (book.isConSach()) { %>
                        <strong class="text-success">Còn <%= book.getSoLuongCon() %> / <%= book.getSoLuongTong() %> cuốn</strong>
                        <% } else { %>
                        <strong class="text-danger">Hết sách</strong>
                        <% } %>
                    </div>
                </div>

                <p class="text-secondary mb-4" style="line-height: 1.6;">
                    <%= (book.getMoTa() != null) ? book.getMoTa() : "" %>
                </p>

                <% if (book.isConSach()) { %>
                <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center gap-3 flex-wrap">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="bookId" value="<%= book.getMaSach() %>">
                    <div class="d-flex align-items-center gap-2">
                        <label class="small fw-semibold text-secondary">Số lượng mượn (Tối đa <%= book.getSoLuongCon() %>):</label>
                        <input type="number" name="quantity" value="1" min="1" max="<%= book.getSoLuongCon() %>"
                               class="form-control form-control-sm text-center fw-bold" style="width: 70px;">
                    </div>
                    <button type="submit" class="btn btn-primary px-4 py-2 rounded-pill fw-semibold">
                        <i class="bi bi-bag-plus me-1"></i>Thêm vào giỏ mượn
                    </button>
                </form>
                <% } else { %>
                <button class="btn btn-secondary px-4 py-2 rounded-pill" disabled>Hết sách</button>
                <% } %>
            </div>
        </div>
    </div>

    <!-- REVIEWS SECTION -->
    <div class="card border shadow-sm rounded-3 p-4 bg-white">
        <h5 class="fw-bold mb-3">Đánh giá &amp; Nhận xét (<%= (reviews != null) ? reviews.size() : 0 %>)</h5>

        <% if ("true".equals(reviewed)) { %>
        <div class="alert alert-success py-2 px-3 mb-3 small">
            <i class="bi bi-check-circle-fill me-1"></i>Đã gửi đánh giá của bạn thành công!
        </div>
        <% } %>

        <% if (currentUser != null) { %>
        <form action="${pageContext.request.contextPath}/catalog" method="post" class="mb-4 p-3 bg-light rounded-3 border">
            <input type="hidden" name="action" value="review">
            <input type="hidden" name="bookId" value="<%= book.getMaSach() %>">
            <div class="row g-2 align-items-center">
                <div class="col-sm-3 col-md-2">
                    <select name="stars" class="form-select form-select-sm fw-semibold">
                        <option value="5">5 Sao (Rất hay)</option>
                        <option value="4">4 Sao (Tốt)</option>
                        <option value="3">3 Sao (Khá)</option>
                        <option value="2">2 Sao (Trung bình)</option>
                        <option value="1">1 Sao (Kém)</option>
                    </select>
                </div>
                <div class="col-sm-7 col-md-8">
                    <input type="text" name="comment" class="form-control form-control-sm" placeholder="Nhập nhận xét của bạn về cuốn sách này..." required>
                </div>
                <div class="col-sm-2 col-md-2">
                    <button type="submit" class="btn btn-primary btn-sm w-100 fw-semibold">
                        <i class="bi bi-send me-1"></i>Gửi đánh giá
                    </button>
                </div>
            </div>
        </form>
        <% } else { %>
        <div class="alert alert-light border py-2 px-3 mb-3 small">
            Vui lòng <a href="${pageContext.request.contextPath}/user?action=login" class="fw-bold">Đăng nhập</a> để gửi đánh giá về cuốn sách này.
        </div>
        <% } %>

        <% if (reviews != null && !reviews.isEmpty()) { %>
            <div class="d-flex flex-column gap-2">
            <% for (DanhGia r : reviews) { %>
                <div class="p-3 border rounded-3 bg-white">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <div>
                            <strong class="text-dark"><i class="bi bi-person-circle text-primary me-1"></i><%= r.getTenNguoiDung() %></strong>
                            <span class="text-warning ms-2">
                                <% for (int i = 0; i < r.getSoSao(); i++) { %>&#9733;<% } %>
                            </span>
                        </div>
                        <% if (r.getNgayDang() != null) { %>
                        <small class="text-muted"><%= sdf.format(r.getNgayDang()) %></small>
                        <% } %>
                    </div>
                    <div class="text-secondary small mt-1"><%= r.getNoiDung() %></div>
                </div>
            <% } %>
            </div>
        <% } else { %>
            <div class="text-muted small py-2">Chưa có nhận xét nào cho cuốn sách này.</div>
        <% } %>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
