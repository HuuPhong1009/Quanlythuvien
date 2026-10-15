<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="model.PhieuMuon" %>
<%@ page import="model.ChiTietPhieuMuon" %>
<%
    List<PhieuMuon> borrows = (List<PhieuMuon>) request.getAttribute("borrows");
    String success = request.getParameter("success");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0">Lịch sử mượn sách</h4>
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-outline-primary btn-sm rounded-pill px-3">
            + Mượn thêm sách
        </a>
    </div>

    <% if ("true".equals(success)) { %>
    <div class="alert alert-success py-2 px-3 mb-3 d-flex align-items-center justify-content-between">
        <span><i class="bi bi-check-circle-fill me-2"></i><strong>Đã gửi yêu cầu mượn sách!</strong> Phiếu mượn đã được chuyển tới Admin chờ duyệt.</span>
        <button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="card border shadow-sm rounded-3 bg-white overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">Mã phiếu</th>
                        <th>Sách mượn</th>
                        <th>Ngày giờ mượn</th>
                        <th>Hạn trả / Ngày trả</th>
                        <th class="text-end pe-3">Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (borrows == null || borrows.isEmpty()) { %>
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">Bạn chưa có phiếu mượn nào.</td>
                    </tr>
                    <% } else { %>
                        <% for (PhieuMuon pm : borrows) { %>
                        <tr>
                            <td class="ps-3"><strong>#PM-<%= pm.getMaPhieu() %></strong></td>
                            <td>
                                <% for (ChiTietPhieuMuon ct : pm.getChiTietList()) { %>
                                <div><%= ct.getTieuDeSach() %> <span class="text-muted small">(x<%= ct.getSoLuong() %>)</span></div>
                                <% } %>
                            </td>
                            <td><small class="fw-semibold text-body"><%= pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : "---" %></small></td>
                            <td>
                                <div><small class="text-danger fw-semibold"><%= pm.getHanTra() != null ? sdf.format(pm.getHanTra()) : "---" %></small></div>
                                <% if ("DA_TRA".equals(pm.getTrangThai()) && pm.getNgayTraThucTe() != null) { %>
                                <small class="text-success d-block mt-1"><strong><i class="bi bi-check-circle-fill me-1"></i>Đã trả:</strong> <%= sdf.format(pm.getNgayTraThucTe()) %></small>
                                <% } %>
                                <% if (pm.isQuaHan() && "DANG_MUON".equals(pm.getTrangThai())) { %>
                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle mt-1 py-1 px-2 rounded-2"><i class="bi bi-clock-history me-1"></i>Đã quá hạn (<%= pm.tinhSoPhutQuaHan() < 60 ? pm.tinhSoPhutQuaHan() + " phút" : pm.tinhSoNgayQuaHan() + " ngày" %>)</span>
                                <% } %>
                                <% if (pm.getPhieuPhat() != null && pm.getPhieuPhat().getSoTienPhat() > 0) { %>
                                <div class="p-1 px-2 rounded-2 mt-1" style="background: rgba(220, 53, 69, 0.12); border-left: 3px solid #dc3545;">
                                    <div class="fw-bold text-danger small">
                                        <i class="bi bi-exclamation-triangle-fill me-1"></i>Phiếu phạt: <%= String.format("%,.0f", pm.getPhieuPhat().getSoTienPhat()) %>đ
                                    </div>
                                    <div class="text-muted small fst-italic" style="font-size: 0.78rem;">
                                        <%= pm.getPhieuPhat().getLyDo() %>
                                    </div>
                                </div>
                                <% } %>
                            </td>
                            <td class="text-end pe-3">
                                <% if ("CHO_DUYET".equals(pm.getTrangThai())) { %>
                                    <span class="badge bg-warning text-dark">Chờ Admin duyệt</span>
                                <% } else if ("DANG_MUON".equals(pm.getTrangThai())) { %>
                                    <span class="badge bg-primary">Đang mượn</span>
                                <% } else if ("DA_TRA".equals(pm.getTrangThai())) { %>
                                    <span class="badge bg-success">Đã trả</span>
                                <% } else if ("TU_CHOI".equals(pm.getTrangThai())) { %>
                                    <span class="badge bg-danger">Từ chối</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">Đã hủy</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
