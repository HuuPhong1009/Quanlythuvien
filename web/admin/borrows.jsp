<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.PhieuMuon" %>
<%@ page import="model.ChiTietPhieuMuon" %>
<%
    List<PhieuMuon> borrows = (List<PhieuMuon>) request.getAttribute("borrows");
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    if (pendingCount == null) pendingCount = 0;
    String msg = request.getParameter("msg");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
%>
<jsp:include page="../includes/header.jsp"/>
<jsp:include page="../includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0">Quản lý Duyệt Mượn &amp; Thu Sách</h4>
        <a href="${pageContext.request.contextPath}/admin?action=dashboard" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
            Về tổng quan
        </a>
    </div>

    <% if ("approved".equals(msg)) { %>
    <div class="alert alert-success py-2 px-3 mb-3">Đã duyệt cho mượn sách thành công!</div>
    <% } else if ("returned".equals(msg)) { %>
    <div class="alert alert-info py-2 px-3 mb-3">Đã thu sách / nhận trả sách thành công! Phiếu phạt đã được ghi nhận nếu quá hạn.</div>
    <% } else if ("rejected".equals(msg)) { %>
    <div class="alert alert-secondary py-2 px-3 mb-3">Đã từ chối phiếu mượn!</div>
    <% } %>

    <% if (pendingCount > 0) { %>
    <div class="alert alert-warning py-2 px-3 mb-3">
        <i class="bi bi-bell-fill me-2"></i>Có <strong><%= pendingCount %> phiếu mượn mới</strong> đang chờ duyệt (tô vàng bên dưới).
    </div>
    <% } %>

    <div class="card border shadow-sm rounded-3 bg-white overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">Mã phiếu</th>
                        <th>Độc giả</th>
                        <th>Sách mượn</th>
                        <th>Ngày giờ mượn</th>
                        <th>Hạn trả / Ngày trả</th>
                        <th>Trạng thái</th>
                        <th class="text-end pe-3">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (borrows != null && !borrows.isEmpty()) { %>
                        <% for (PhieuMuon p : borrows) { %>
                        <tr class="<%= "CHO_DUYET".equals(p.getTrangThai()) ? "table-warning" : (p.isQuaHan() && "DANG_MUON".equals(p.getTrangThai()) ? "table-danger" : "") %>">
                            <td class="ps-3"><strong>#PM-<%= p.getMaPhieu() %></strong></td>
                            <td>
                                <div class="fw-semibold"><%= p.getTenDocGia() %></div>
                                <% if (p.getGhiChu() != null && !p.getGhiChu().isEmpty()) { %>
                                <small class="text-muted"><%= p.getGhiChu() %></small>
                                <% } %>
                            </td>
                            <td>
                                <% for (ChiTietPhieuMuon ct : p.getChiTietList()) { %>
                                <div><%= ct.getTieuDeSach() %> <span class="text-muted small">(x<%= ct.getSoLuong() %>)</span></div>
                                <% } %>
                            </td>
                            <td>
                                <div><small class="fw-semibold text-body"><%= p.getNgayMuon() != null ? sdf.format(p.getNgayMuon()) : "---" %></small></div>
                            </td>
                            <td>
                                <div><small class="text-danger fw-semibold"><%= p.getHanTra() != null ? sdf.format(p.getHanTra()) : "---" %></small></div>
                                <% if ("DA_TRA".equals(p.getTrangThai()) && p.getNgayTraThucTe() != null) { %>
                                <small class="text-success d-block mt-1"><strong><i class="bi bi-check-circle-fill me-1"></i>Đã trả:</strong> <%= sdf.format(p.getNgayTraThucTe()) %></small>
                                <% } %>
                                <% if (p.isQuaHan() && "DANG_MUON".equals(p.getTrangThai())) { %>
                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle mt-1 py-1 px-2 rounded-2"><i class="bi bi-clock-history me-1"></i>Quá hạn (<%= p.tinhSoPhutQuaHan() < 60 ? p.tinhSoPhutQuaHan() + " phút" : p.tinhSoNgayQuaHan() + " ngày" %>)</span>
                                <% } %>
                                <% if (p.getPhieuPhat() != null && p.getPhieuPhat().getSoTienPhat() > 0) { %>
                                <div class="p-1 px-2 rounded-2 mt-1" style="background: rgba(220, 53, 69, 0.12); border-left: 3px solid #dc3545;">
                                    <div class="fw-bold text-danger small">
                                        <i class="bi bi-exclamation-octagon-fill me-1"></i>Phạt: <%= String.format("%,.0f", p.getPhieuPhat().getSoTienPhat()) %>đ
                                    </div>
                                    <div class="text-muted small fst-italic" style="font-size: 0.78rem;">
                                        <%= p.getPhieuPhat().getLyDo() %>
                                    </div>
                                </div>
                                <% } %>
                            </td>
                            <td>
                                <% if ("CHO_DUYET".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                <% } else if ("DANG_MUON".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-primary">Đang mượn</span>
                                <% } else if ("DA_TRA".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-success">Đã trả</span>
                                <% } else if ("TU_CHOI".equals(p.getTrangThai())) { %>
                                    <span class="badge bg-danger">Từ chối</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">Đã hủy</span>
                                <% } %>
                            </td>
                            <td class="text-end pe-3">
                                <% if ("CHO_DUYET".equals(p.getTrangThai())) { %>
                                    <a href="${pageContext.request.contextPath}/admin?action=approve&id=<%= p.getMaPhieu() %>" class="btn btn-sm btn-success">Duyệt</a>
                                    <a href="${pageContext.request.contextPath}/admin?action=reject&id=<%= p.getMaPhieu() %>" class="btn btn-sm btn-outline-danger">Hủy</a>
                                <% } else if ("DANG_MUON".equals(p.getTrangThai())) { %>
                                    <a href="${pageContext.request.contextPath}/admin?action=return&id=<%= p.getMaPhieu() %>" class="btn btn-sm btn-primary">Thu sách</a>
                                <% } else { %>
                                    <span class="text-success small"><i class="bi bi-check2"></i> Xong</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    <% } else { %>
                        <tr><td colspan="7" class="text-center py-4 text-muted">Chưa có phiếu mượn nào.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
