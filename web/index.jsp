<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="model.Sach" %>
<%@ page import="model.TheLoai" %>
<%
    List<Sach> books = (List<Sach>) request.getAttribute("books");
    List<TheLoai> categories = (List<TheLoai>) request.getAttribute("categories");
    String keyword = (String) request.getAttribute("keyword");
    Integer selectedCat = (Integer) request.getAttribute("selectedCat");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");

    if (books == null) {
        response.sendRedirect("catalog");
        return;
    }
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;

    String queryParam = "";
    if (keyword != null && !keyword.trim().isEmpty()) {
        queryParam += "&q=" + URLEncoder.encode(keyword.trim(), "UTF-8");
    }
    if (selectedCat != null && selectedCat > 0) {
        queryParam += "&cat=" + selectedCat;
    }
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-4">
    <!-- SEARCH & FILTER BAR -->
    <div class="toolbar-box shadow-sm">
        <form action="${pageContext.request.contextPath}/catalog" method="get">
            <div class="row g-2 align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 text-muted">
                            <i class="bi bi-search"></i>
                        </span>
                        <input type="text" name="q" class="form-control border-start-0"
                               placeholder="Tìm tên sách hoặc tác giả..."
                               value="<%= (keyword != null) ? keyword : "" %>">
                    </div>
                </div>
                <div class="col-md-4">
                    <select name="cat" class="form-select">
                        <option value="0">Tất cả thể loại</option>
                        <% if (categories != null) {
                            for (TheLoai c : categories) {
                                String sel = (selectedCat != null && selectedCat == c.getMaTheLoai()) ? "selected" : "";
                        %>
                        <option value="<%= c.getMaTheLoai() %>" <%= sel %>><%= c.getTenTheLoai() %></option>
                        <%  }
                        } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100 fw-semibold">
                        Tìm kiếm
                    </button>
                </div>
            </div>
        </form>
    </div>

    <!-- CATEGORY QUICK PILLS -->
    <div class="cat-pills">
        <a href="${pageContext.request.contextPath}/catalog"
           class="cat-pill <%= (selectedCat == null || selectedCat <= 0) ? "active" : "" %>">
            Tất cả
        </a>
        <% if (categories != null) {
            for (TheLoai c : categories) {
                boolean isActive = (selectedCat != null && selectedCat == c.getMaTheLoai());
        %>
        <a href="${pageContext.request.contextPath}/catalog?cat=<%= c.getMaTheLoai() %>"
           class="cat-pill <%= isActive ? "active" : "" %>">
            <%= c.getTenTheLoai() %>
        </a>
        <%  }
        } %>
    </div>

    <!-- BOOK GRID -->
    <% if (books.isEmpty()) { %>
    <div class="text-center py-5 bg-white rounded-3 border">
        <p class="text-muted mb-3">Không tìm thấy cuốn sách nào phù hợp.</p>
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-outline-primary btn-sm rounded-pill px-3">
            Xem tất cả sách
        </a>
    </div>
    <% } else { %>
    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
        <% for (Sach s : books) { %>
        <div class="col">
            <div class="book-card">
                <a href="${pageContext.request.contextPath}/catalog?action=detail&id=<%= s.getMaSach() %>" class="book-cover-box" title="<%= s.getTieuDe() %>">
                    <img src="<%= s.getHinhAnh().startsWith("http") ? s.getHinhAnh() : (request.getContextPath() + "/" + s.getHinhAnh() + "?v=real2026") %>"
                         alt="<%= s.getTieuDe() %>"
                         loading="lazy"
                         onerror="this.src='${pageContext.request.contextPath}/images/books/real_cover_1.jpg'">
                </a>

                <div class="book-title" title="<%= s.getTieuDe() %>">
                    <a href="${pageContext.request.contextPath}/catalog?action=detail&id=<%= s.getMaSach() %>">
                        <%= s.getTieuDe() %>
                    </a>
                </div>
                <div class="book-author"><%= s.getTacGia() %></div>

                <div class="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
                    <% if (s.isConSach()) { %>
                    <span class="stock-badge-in">Còn <%= s.getSoLuongCon() %></span>
                    <form action="${pageContext.request.contextPath}/cart" method="post" class="m-0">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="bookId" value="<%= s.getMaSach() %>">
                        <button type="submit" class="btn btn-primary btn-sm rounded-pill px-3">
                            + Mượn
                        </button>
                    </form>
                    <% } else { %>
                    <span class="stock-badge-out">Hết sách</span>
                    <button class="btn btn-light btn-sm rounded-pill px-3 text-muted" disabled>Hết</button>
                    <% } %>
                </div>
            </div>
        </div>
        <% } %>
    </div>

    <!-- PAGINATION BAR (1, 2, 3, 4...) -->
    <% if (totalPages > 1) { %>
    <div class="d-flex flex-column align-items-center mt-5 mb-3">
        <nav aria-label="Phân trang danh sách sách">
            <ul class="pagination shadow-sm mb-2">
                <li class="page-item <%= (currentPage <= 1) ? "disabled" : "" %>">
                    <a class="page-link px-3 fw-semibold" href="${pageContext.request.contextPath}/catalog?page=<%= currentPage - 1 %><%= queryParam %>">
                        &laquo; Trước
                    </a>
                </li>

                <% for (int p = 1; p <= totalPages; p++) { %>
                <li class="page-item <%= (p == currentPage) ? "active" : "" %>">
                    <a class="page-link px-3 fw-bold" href="${pageContext.request.contextPath}/catalog?page=<%= p %><%= queryParam %>">
                        <%= p %>
                    </a>
                </li>
                <% } %>

                <li class="page-item <%= (currentPage >= totalPages) ? "disabled" : "" %>">
                    <a class="page-link px-3 fw-semibold" href="${pageContext.request.contextPath}/catalog?page=<%= currentPage + 1 %><%= queryParam %>">
                        Sau &raquo;
                    </a>
                </li>
            </ul>
        </nav>
    </div>
    <% } %>
    <% } %>
</div>

<jsp:include page="includes/footer.jsp"/>
