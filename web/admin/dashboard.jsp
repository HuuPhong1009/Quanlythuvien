<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.PhieuMuon" %>
<%@ page import="model.ChiTietPhieuMuon" %>
<%
    Integer totalBooks = (Integer) request.getAttribute("totalBooks");
    Integer totalBorrows = (Integer) request.getAttribute("totalBorrows");
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    if (pendingCount == null) pendingCount = 0;
    List<PhieuMuon> borrows = (List<PhieuMuon>) request.getAttribute("borrows");
    String msg = request.getParameter("msg");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
%>
<jsp:include page="../includes/header.jsp"/>
<jsp:include page="../includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h4 class="fw-bold mb-0">Quản trị Thư viện</h4>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin?action=books" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                Kho sách (<%= (totalBooks != null) ? totalBooks : 0 %>)
            </a>
            <a href="${pageContext.request.contextPath}/admin?action=borrows" class="btn btn-primary btn-sm rounded-pill px-3">
                Duyệt &amp; Thu sách (<%= pendingCount %> chờ duyệt)
            </a>
        </div>
    </div>

    <% if ("approved".equals(msg)) { %>
    <div class="alert alert-success py-2 px-3 mb-3">Đã duyệt cho mượn sách thành công!</div>
    <% } else if ("returned".equals(msg)) { %>
    <div class="alert alert-info py-2 px-3 mb-3">Đã nhận trả sách và cộng lại kho thành công!</div>
    <% } else if ("rejected".equals(msg)) { %>
    <div class="alert alert-secondary py-2 px-3 mb-3">Đã từ chối phiếu mượn!</div>
    <% } %>

    <% if (pendingCount > 0) { %>
    <div class="alert alert-warning py-2 px-3 mb-3 d-flex justify-content-between align-items-center">
        <span><i class="bi bi-bell-fill me-2"></i>Có <strong><%= pendingCount %> phiếu mượn mới</strong> đang chờ duyệt.</span>
        <a href="${pageContext.request.contextPath}/admin?action=borrows" class="btn btn-warning btn-sm fw-semibold">Xem ngay</a>
    </div>
    <% } %>

    <!-- 3 SIMPLE STAT CARDS -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card border shadow-sm p-3 bg-white">
                <span class="text-muted small">Tổng đầu sách</span>
                <h3 class="fw-bold mb-0 text-primary"><%= (totalBooks != null) ? totalBooks : 0 %></h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border shadow-sm p-3 bg-white">
                <span class="text-muted small">Tổng phiếu mượn</span>
                <h3 class="fw-bold mb-0 text-dark"><%= (totalBorrows != null) ? totalBorrows : 0 %></h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border shadow-sm p-3 bg-white">
                <span class="text-muted small">Phiếu chờ duyệt</span>
                <h3 class="fw-bold mb-0 <%= pendingCount > 0 ? "text-danger" : "text-success" %>"><%= pendingCount %></h3>
            </div>
        </div>
    </div>

    <!-- BORROW TABLE -->
    <div class="card border shadow-sm rounded-3 bg-white overflow-hidden">
        <div class="card-header bg-white py-2 px-3 border-bottom fw-bold">
            Danh sách phiếu mượn &amp; trả sách
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">Mã phiếu</th>
                        <th>Độc giả</th>
                        <th>Sách mượn</th>
                        <th>Ngày mượn / Hạn trả</th>
                        <th>Trạng thái</th>
                        <th class="text-end pe-3">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (borrows != null && !borrows.isEmpty()) { %>
                        <% for (PhieuMuon p : borrows) { %>
                        <tr class="<%= "CHO_DUYET".equals(p.getTrangThai()) ? "table-warning" : (p.isQuaHan() && "DANG_MUON".equals(p.getTrangThai()) ? "table-danger" : "") %>">
                            <td class="ps-3"><strong>#PM-<%= p.getMaPhieu() %></strong></td>
                            <td><%= p.getTenDocGia() %></td>
                            <td>
                                <% for (ChiTietPhieuMuon ct : p.getChiTietList()) { %>
                                <div><%= ct.getTieuDeSach() %> <span class="text-muted small">(x<%= ct.getSoLuong() %>)</span></div>
                                <% } %>
                            </td>
                            <td>
                                <div class="d-flex flex-column gap-1 small" style="min-width: 215px;">
                                    <div class="d-flex align-items-center text-muted">
                                        <span style="width: 65px;"><i class="bi bi-box-arrow-right text-primary me-1"></i>Mượn:</span>
                                        <span class="fw-semibold text-body"><%= p.getNgayMuon() != null ? sdf.format(p.getNgayMuon()) : "---" %></span>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <span class="text-muted" style="width: 65px;"><i class="bi bi-alarm text-warning me-1"></i>Hạn trả:</span>
                                        <span class="fw-semibold text-danger"><%= p.getHanTra() != null ? sdf.format(p.getHanTra()) : "---" %></span>
                                    </div>
                                    <% if ("DA_TRA".equals(p.getTrangThai()) && p.getNgayTraThucTe() != null) { %>
                                    <div class="d-flex align-items-center">
                                        <span class="text-muted" style="width: 65px;"><i class="bi bi-check-circle-fill text-success me-1"></i>Đã trả:</span>
                                        <span class="fw-semibold text-success"><%= sdf.format(p.getNgayTraThucTe()) %></span>
                                    </div>
                                    <% } %>
                                    <% if (p.isQuaHan() && "DANG_MUON".equals(p.getTrangThai())) { %>
                                    <div>
                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle py-1 px-2 rounded-2">
                                            <i class="bi bi-exclamation-triangle-fill me-1"></i>Quá hạn (<%= p.tinhSoPhutQuaHan() < 60 ? p.tinhSoPhutQuaHan() + " phút" : p.tinhSoNgayQuaHan() + " ngày" %>)
                                        </span>
                                    </div>
                                    <% } %>
                                    <% if (p.getPhieuPhat() != null && p.getPhieuPhat().getSoTienPhat() > 0) { %>
                                    <div class="p-1 px-2 rounded-2 mt-1" style="background: rgba(220, 53, 69, 0.12); border-left: 3px solid #dc3545;">
                                        <div class="fw-bold text-danger">
                                            <i class="bi bi-exclamation-octagon-fill me-1"></i>Phạt: <%= String.format("%,.0f", p.getPhieuPhat().getSoTienPhat()) %>đ
                                        </div>
                                        <div class="text-muted small fst-italic" style="font-size: 0.78rem;">
                                            <%= p.getPhieuPhat().getLyDo() %>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                            </td>
                            <td>
                                <% if ("CHO_DUYET".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                <% } else if ("DANG_MUON".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-primary">Đang mượn</span>
                                <% } else if ("DA_TRA".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-success">Đã trả</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">Đã hủy</span>
                                <% } %>
                            </td>
                            <td class="text-end pe-3">
                                <% if ("CHO_DUYET".equals(p.getTrangThai())) { %>
                                    <a href="${pageContext.request.contextPath}/admin?action=approve&id=<%= p.getMaPhieu() %>&from=dashboard" class="btn btn-sm btn-success">Duyệt</a>
                                    <a href="${pageContext.request.contextPath}/admin?action=reject&id=<%= p.getMaPhieu() %>&from=dashboard" class="btn btn-sm btn-outline-danger">Hủy</a>
                                <% } else if ("DANG_MUON".equals(p.getTrangThai())) { %>
                                    <a href="${pageContext.request.contextPath}/admin?action=return&id=<%= p.getMaPhieu() %>&from=dashboard" class="btn btn-sm btn-primary">Thu sách</a>
                                <% } else { %>
                                    <span class="text-success small"><i class="bi bi-check2"></i> Xong</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    <% } else { %>
                        <tr><td colspan="6" class="text-center py-4 text-muted">Chưa có phiếu mượn nào.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
