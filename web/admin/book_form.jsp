<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Sach" %>
<%@ page import="model.TheLoai" %>
<%
    Sach book = (Sach) request.getAttribute("book");
    List<TheLoai> categories = (List<TheLoai>) request.getAttribute("categories");
    boolean isEdit = (book != null);
%>
<jsp:include page="../includes/header.jsp"/>
<jsp:include page="../includes/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-md-10">
            <div class="card border-0 shadow-sm rounded-4 bg-white">
                <div class="card-header bg-white py-3 px-4 border-bottom d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0 text-dark">
                        <i class="bi <%= isEdit ? "bi-pencil-square text-primary" : "bi-plus-circle text-success" %> me-2"></i>
                        <%= isEdit ? "Chỉnh Sửa Thông Tin Sách" : "Thêm Đầu Sách Mới Vào Thư Viện" %>
                    </h5>
                    <a href="${pageContext.request.contextPath}/admin?action=books" class="btn btn-outline-secondary btn-sm rounded-pill px-3"><i class="bi bi-arrow-left me-1"></i>Quay lại</a>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/admin" method="post">
                        <input type="hidden" name="action" value="save_book">
                        <input type="hidden" name="maSach" value="<%= isEdit ? book.getMaSach() : 0 %>">
                        <input type="hidden" name="soLuongCon" value="<%= isEdit ? book.getSoLuongCon() : 0 %>">

                        <div class="mb-3">
                            <label class="form-label small fw-bold">Tiêu đề sách (*):</label>
                            <input type="text" name="tieuDe" class="form-control" placeholder="Nhập tên đầy đủ của tác phẩm" value="<%= isEdit ? book.getTieuDe() : "" %>" required>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Tác giả (*):</label>
                                <input type="text" name="tacGia" class="form-control" placeholder="Họ và tên tác giả / dịch giả" value="<%= isEdit ? book.getTacGia() : "" %>" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Thể loại chuyên ngành:</label>
                                <select name="maTheLoai" class="form-select">
                                    <% if (categories != null) {
                                        for (TheLoai c : categories) {
                                            String sel = (isEdit && book.getMaTheLoai() == c.getMaTheLoai()) ? "selected" : "";
                                    %>
                                    <option value="<%= c.getMaTheLoai() %>" <%= sel %>><%= c.getTenTheLoai() %></option>
                                    <%  }
                                    } %>
                                </select>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-4">
                                <label class="form-label small fw-bold">Năm xuất bản:</label>
                                <input type="number" name="namXuatBan" class="form-control" value="<%= isEdit ? book.getNamXuatBan() : 2024 %>">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label small fw-bold">Giá bìa tham chiếu (VNĐ):</label>
                                <input type="number" name="giaBia" class="form-control" step="1000" value="<%= isEdit ? (long)book.getGiaBia() : 100000 %>">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label small fw-bold">Số lượng trong kho (*):</label>
                                <input type="number" name="soLuong" class="form-control" min="1" value="<%= isEdit ? book.getSoLuongTong() : 5 %>" required>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Vị trí kệ sách:</label>
                                <input type="text" name="viTriKe" class="form-control" placeholder="Ví dụ: Kệ A1-01" value="<%= isEdit ? book.getViTriKe() : "Khu A" %>">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Tình trạng sách:</label>
                                <select name="tinhTrang" class="form-select">
                                    <option value="MOI" <%= (isEdit && "MOI".equals(book.getTinhTrang())) ? "selected" : "" %>>Mới 100%</option>
                                    <option value="TOT" <%= (!isEdit || "TOT".equals(book.getTinhTrang())) ? "selected" : "" %>>Tốt</option>
                                    <option value="CU_XUOC" <%= (isEdit && "CU_XUOC".equals(book.getTinhTrang())) ? "selected" : "" %>>Cũ / Có xước nhẹ</option>
                                    <option value="HONG" <%= (isEdit && "HONG".equals(book.getTinhTrang())) ? "selected" : "" %>>Cần bảo trì / Hỏng</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label small fw-bold">Đường dẫn ảnh bìa sách (Link ảnh mạng https://... hoặc đường dẫn tĩnh):</label>
                            <input type="text" name="hinhAnh" class="form-control" placeholder="images/books/real_cover_1.jpg hoặc https://..." value="<%= isEdit ? book.getHinhAnh() : "images/books/real_cover_1.jpg" %>">
                            <div class="form-text">Có thể chọn ảnh có sẵn (`images/books/real_cover_1.jpg` đến `real_cover_27.jpg`) hoặc dán link ảnh trực tiếp từ mạng (`https://...`).</div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label small fw-bold">Tóm tắt mô tả nội dung:</label>
                            <textarea name="moTa" class="form-control" rows="3" placeholder="Tóm tắt ngắn gọn nội dung cuốn sách..."><%= isEdit ? book.getMoTa() : "" %></textarea>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/admin?action=books" class="btn btn-light px-4 rounded-pill">Hủy Bỏ</a>
                            <button type="submit" class="btn btn-primary px-4 fw-bold rounded-pill">
                                <i class="bi bi-save me-1"></i> Lưu Thông Tin Sách
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
