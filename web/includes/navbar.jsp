<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="model.NguoiDung" %>
<%@ page import="model.GioMuon" %>
<%@ page import="data.BorrowDB" %>
<%
    NguoiDung navUser = (NguoiDung) session.getAttribute("currentUser");
    GioMuon navCart = (GioMuon) session.getAttribute("borrowCart");
    int cartCount = (navCart != null) ? navCart.getTongSoLuong() : 0;
    int pendingAdminCount = (navUser != null && navUser.isQuanTri()) ? BorrowDB.countPendingBorrows() : 0;
%>
<nav class="navbar navbar-expand-lg navbar-basic sticky-top">
    <div class="container">
        <a class="navbar-brand-basic" href="${pageContext.request.contextPath}/catalog">
            <i class="bi bi-book-half text-primary fs-4"></i>
            <span>Thư Viện Sách</span>
        </a>

        <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3 gap-1">
                <li class="nav-item">
                    <a class="nav-link nav-link-basic" href="${pageContext.request.contextPath}/catalog">
                        <i class="bi bi-grid me-1"></i>Tất cả sách
                    </a>
                </li>
                <% if (navUser != null) { %>
                <li class="nav-item">
                    <a class="nav-link nav-link-basic" href="${pageContext.request.contextPath}/borrow?action=history">
                        <i class="bi bi-clock-history me-1"></i>Lịch sử mượn
                    </a>
                </li>
                <% } %>
                <% if (navUser != null && navUser.isQuanTri()) { %>
                <li class="nav-item">
                    <a class="nav-link nav-link-basic fw-bold text-primary" href="${pageContext.request.contextPath}/admin?action=borrows">
                        <i class="bi bi-shield-check me-1"></i>Quản lý Mượn/Trả
                        <% if (pendingAdminCount > 0) { %>
                        <span class="badge rounded-pill bg-danger ms-1">
                            <i class="bi bi-bell-fill"></i> <%= pendingAdminCount %>
                        </span>
                        <% } %>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link nav-link-basic" href="${pageContext.request.contextPath}/admin?action=books">
                        <i class="bi bi-journal-plus me-1"></i>Kho sách
                    </a>
                </li>
                <% } %>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <a class="btn btn-outline-primary btn-sm px-3 rounded-pill" href="${pageContext.request.contextPath}/cart">
                    <i class="bi bi-bag me-1"></i>Giỏ mượn
                    <% if (cartCount > 0) { %>
                    <span class="badge bg-primary rounded-pill ms-1"><%= cartCount %></span>
                    <% } %>
                </a>

                <% if (navUser != null) { %>
                <span class="small fw-semibold text-dark px-2 border-start">
                    <i class="bi bi-person-circle text-primary me-1"></i><%= navUser.getHoTen() %>
                </span>
                <a class="btn btn-outline-danger btn-sm rounded-pill px-3" href="${pageContext.request.contextPath}/user?action=logout">
                    Đăng xuất
                </a>
                <% } else { %>
                <a class="btn btn-primary btn-sm px-3 rounded-pill" href="${pageContext.request.contextPath}/user?action=login">
                    Đăng nhập
                </a>
                <a class="btn btn-outline-secondary btn-sm px-3 rounded-pill" href="${pageContext.request.contextPath}/user?action=register">
                    Đăng ký
                </a>
                <% } %>
            </div>
        </div>
    </div>
</nav>
