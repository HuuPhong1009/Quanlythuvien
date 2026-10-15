<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Sach" %>
<%
    List<Sach> books = (List<Sach>) request.getAttribute("books");
%>
<jsp:include page="../includes/header.jsp"/>
<jsp:include page="../includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0">Kho sách (<%= (books != null) ? books.size() : 0 %> cuốn)</h4>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin?action=book_form" class="btn btn-primary btn-sm rounded-pill px-3">
                + Thêm sách mới
            </a>
        </div>
    </div>

    <div class="card border shadow-sm rounded-3 bg-white overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">Mã</th>
                        <th>Sách</th>
                        <th>Tác giả</th>
                        <th>Thể loại</th>
                        <th class="text-center">Kệ</th>
                        <th class="text-center">Còn / Tổng</th>
                        <th class="text-end pe-3">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (books != null && !books.isEmpty()) { %>
                        <% for (Sach s : books) { %>
                        <tr>
                            <td class="ps-3">#<%= s.getMaSach() %></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="<%= s.getHinhAnh().startsWith("http") ? s.getHinhAnh() : (request.getContextPath() + "/" + s.getHinhAnh()) %>"
                                         class="book-cover-mini"
                                         onerror="this.src='${pageContext.request.contextPath}/images/books/real_cover_1.jpg'">
                                    <span class="fw-semibold"><%= s.getTieuDe() %></span>
                                </div>
                            </td>
                            <td><%= s.getTacGia() %></td>
                            <td>
                                <span class="badge rounded-pill" style="background-color: #222222; color: #f5f5f5; border: 1px solid #3d3d3d; font-size: 0.82rem; font-weight: 500; padding: 0.35rem 0.65rem;">
                                    <i class="bi bi-tag-fill me-1" style="color: #a3a3a3;"></i><%= (s.getTenTheLoai() != null && !s.getTenTheLoai().trim().isEmpty()) ? s.getTenTheLoai() : "Chưa phân loại" %>
                                </span>
                            </td>
                            <td class="text-center"><%= s.getViTriKe() %></td>
                            <td class="text-center">
                                <strong><%= s.getSoLuongCon() %></strong> / <%= s.getSoLuongTong() %>
                            </td>
                            <td class="text-end pe-3">
                                <a href="${pageContext.request.contextPath}/admin?action=book_form&id=<%= s.getMaSach() %>" class="btn btn-sm btn-outline-primary">Sửa</a>
                                <a href="${pageContext.request.contextPath}/admin?action=delete_book&id=<%= s.getMaSach() %>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Xóa sách này?');">Xóa</a>
                            </td>
                        </tr>
                        <% } %>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
