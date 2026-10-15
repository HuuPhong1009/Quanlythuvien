<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page import="model.GioMuon" %>
<%@ page import="model.ChiTietGioMuon" %>
<%@ page import="model.NguoiDung" %>
<%
    GioMuon cart = (GioMuon) session.getAttribute("borrowCart");
    if (cart == null) {
        cart = new GioMuon();
    }
    NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
    String maxReached = request.getParameter("maxReached");
    int tongDangCo = cart.getTongSoSachDangCo();
    int tongMuon = cart.getTongSoLuong();
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h4 class="fw-bold mb-0">Giỏ mượn sách (<%= tongMuon %> cuốn)</h4>
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-outline-primary btn-sm rounded-pill px-3">
            + Chọn thêm sách khác
        </a>
    </div>

    <% if ("true".equals(maxReached)) { %>
    <div class="alert alert-warning py-2 px-3 mb-3 small">
        <i class="bi bi-exclamation-circle me-1"></i>Số lượng mượn đã được tự động giới hạn bằng đúng tổng số sách đang có trong kho.
    </div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card border shadow-sm rounded-3 bg-white overflow-hidden">
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-3">Sách</th>
                                <th class="text-center" style="width: 140px;">Sách đang có</th>
                                <th class="text-center" style="width: 190px;">Số lượng mượn</th>
                                <th class="text-end pe-3" style="width: 70px;">Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (cart.getDanhSachSach().isEmpty()) { %>
                            <tr>
                                <td colspan="4" class="text-center py-4 text-muted">
                                    Giỏ mượn trống. <a href="${pageContext.request.contextPath}/catalog">Chọn sách ngay</a>
                                </td>
                            </tr>
                            <% } else { %>
                                <% for (ChiTietGioMuon item : cart.getDanhSachSach()) {
                                    int maxStock = item.getSach().getSoLuongCon();
                                    int curQty = item.getSoLuong();
                                    String disMinus = (curQty <= 1) ? "disabled" : "";
                                    String disPlus = (curQty >= maxStock) ? "disabled" : "";
                                %>
                                <tr>
                                    <td class="ps-3">
                                        <div class="d-flex align-items-center gap-3">
                                            <img src="${pageContext.request.contextPath}/<%= item.getSach().getHinhAnh() %>?v=real2026"
                                                 class="book-cover-mini"
                                                 onerror="this.src='${pageContext.request.contextPath}/images/books/real_cover_1.jpg'">
                                            <div>
                                                <div class="fw-semibold"><%= item.getSach().getTieuDe() %></div>
                                                <small class="text-muted"><%= item.getSach().getTacGia() %></small>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-center">
                                        <span class="stock-badge-in">Còn <%= maxStock %> cuốn</span>
                                    </td>
                                    <td class="text-center">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline-flex align-items-center justify-content-center gap-1">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="bookId" value="<%= item.getSach().getMaSach() %>">

                                            <button type="button" class="btn btn-sm btn-outline-secondary px-2"
                                                    <%= disMinus %>
                                                    onclick="var inp=this.parentNode.querySelector('input[name=quantity]'); inp.value=Math.max(1, parseInt(inp.value)-1); this.form.submit();">
                                                &minus;
                                            </button>

                                            <input type="number" name="quantity"
                                                   value="<%= curQty %>"
                                                   min="1"
                                                   max="<%= maxStock %>"
                                                   onchange="var v = parseInt(this.value) || 1;
                                                   var max = parseInt(this.max) || 1;
                                                   if (v > max) v = max;
                                                   if (v < 1) v = 1;
                                                   this.value = v;
                                                   this.form.submit();"
                                                   class="form-control form-control-sm text-center fw-bold"
                                                   style="width: 62px;">

                                            <button type="button" class="btn btn-sm btn-outline-secondary px-2"
                                                    <%= disPlus %>
                                                    onclick="var inp=this.parentNode.querySelector('input[name=quantity]'); inp.value=Math.min(<%= maxStock %>, parseInt(inp.value)+1); this.form.submit();">
                                                +
                                            </button>
                                        </form>
                                        <div class="text-muted" style="font-size: 0.75rem; margin-top: 2px;">
                                            Tối đa: <strong><%= maxStock %></strong> cuốn
                                        </div>
                                    </td>
                                    <td class="text-end pe-3">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="bookId" value="<%= item.getSach().getMaSach() %>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                                <% } %>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card border shadow-sm rounded-3 p-3 bg-white">
                <h6 class="fw-bold border-bottom pb-2 mb-3">Đăng ký mượn</h6>

                <div class="d-flex justify-content-between align-items-center small mb-2">
                    <span class="text-muted">Tổng số sách còn (trong kho):</span>
                    <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 fw-bold">
                        <%= tongDangCo %> cuốn
                    </span>
                </div>

                <div class="d-flex justify-content-between align-items-center small mb-2">
                    <span class="text-muted">Số lượng đăng ký mượn:</span>
                    <strong class="text-primary fs-6"><%= tongMuon %> / <%= tongDangCo %> cuốn</strong>
                </div>

                <% if (currentUser != null) { 
                    String disSubmit = cart.getDanhSachSach().isEmpty() ? "disabled" : "";
                %>
                <form action="${pageContext.request.contextPath}/borrow" method="post">
                    <!-- THỜI GIAN THỰC TẾ -->
                    <div class="p-2 mb-3 rounded-2 bg-light border small">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <span class="text-muted"><i class="bi bi-clock-history me-1 text-primary"></i>Thời gian mượn:</span>
                            <span id="liveCurrentTime" class="fw-bold text-dark font-monospace"></span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center pt-1 border-top">
                            <span class="text-muted"><i class="bi bi-calendar-event me-1 text-warning"></i>Hạn trả dự kiến:</span>
                            <span id="previewDueTime" class="fw-bold text-primary font-monospace"></span>
                        </div>
                        <div id="previewDueDesc" class="text-muted small mt-1 text-end" style="font-size: 0.75rem;"></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-muted mb-1">Thời hạn mượn / Thử nghiệm quá hạn:</label>
                        <select name="loanDuration" id="loanDurationSelect" class="form-select form-select-sm" onchange="updateDuePreview()">
                            <option value="14_DAYS" selected>Mặc định: 14 ngày (Tiêu chuẩn)</option>
                            <option value="1_MINUTE" class="text-danger fw-bold">&#9889; [TEST NHANH] 1 phút (Hết hạn sau 60 giây)</option>
                            <option value="2_MINUTES">&#9889; [TEST NHANH] 2 phút</option>
                            <option value="5_MINUTES">&#9889; [TEST NHANH] 5 phút</option>
                            <option value="OVERDUE_NOW">&#9889; [TEST TỨC THÌ] Quá hạn ngay (-1 phút)</option>
                            <option value="1_DAY">1 ngày</option>
                            <option value="7_DAYS">7 ngày</option>
                            <option value="CUSTOM">&#128197; Tùy chỉnh ngày &amp; giờ cụ thể...</option>
                        </select>
                    </div>

                    <div id="customDurationBox" class="mb-3 d-none">
                        <label class="form-label small fw-semibold text-muted mb-1">Chọn mốc thời gian hết hạn:</label>
                        <input type="datetime-local" name="customDue" id="customDueInput" class="form-control form-control-sm" onchange="updateDuePreview()">
                        <div class="form-text small text-muted">Hệ thống sẽ tính phạt nếu trả sách sau mốc này.</div>
                    </div>

                    <input type="text" name="notes" class="form-control form-control-sm mb-3" placeholder="Ghi chú mượn sách (tùy chọn)...">
                    <button type="submit" class="btn btn-primary w-100 fw-semibold py-2" <%= disSubmit %>>
                        Gửi yêu cầu mượn (<%= tongMuon %> cuốn)
                    </button>
                </form>
                <% } else { %>
                <a href="${pageContext.request.contextPath}/user?action=login" class="btn btn-primary w-100">
                    Đăng nhập để mượn
                </a>
                <% } %>
            </div>
        </div>
    </div>
</div>

<script>
/**
 * ============================================================================
 * JAVASCRIPT REAL-TIME: Đồng hồ thời gian thực & Xem trước hạn trả mượn
 * ----------------------------------------------------------------------------
 * 1. formatVNTime(): Định dạng đối tượng Date thành chuỗi chuẩn: HH:mm:ss dd/MM/yyyy.
 * 2. updateDuePreview(): Tự động tính toán hạn trả tương ứng với tùy chọn mà người dùng click
 *    (ví dụ: +14 ngày, +1 phút test nhanh, hoặc ngày giờ tự chọn).
 * 3. setInterval(): Cập nhật từng giây (1000ms) để đồng hồ trên giao diện chạy liên tục như đồng hồ thật.
 * ============================================================================
 */
function formatVNTime(date) {
    var d = ("0" + date.getDate()).slice(-2);
    var m = ("0" + (date.getMonth() + 1)).slice(-2);
    var y = date.getFullYear();
    var h = ("0" + date.getHours()).slice(-2);
    var min = ("0" + date.getMinutes()).slice(-2);
    var s = ("0" + date.getSeconds()).slice(-2);
    return h + ":" + min + ":" + s + " " + d + "/" + m + "/" + y;
}

function updateDuePreview() {
    var now = new Date();
    var curEl = document.getElementById('liveCurrentTime');
    if (curEl) curEl.innerText = formatVNTime(now);

    var sel = document.getElementById('loanDurationSelect');
    var box = document.getElementById('customDurationBox');
    var previewEl = document.getElementById('previewDueTime');
    var descEl = document.getElementById('previewDueDesc');
    if (!sel || !previewEl) return;

    var dueDate = new Date(now.getTime());
    var desc = "";

    if (sel.value === '1_MINUTE') {
        dueDate.setMinutes(dueDate.getMinutes() + 1);
        desc = "Hết hạn sau 60 giây để test phiếu phạt tự động.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === '2_MINUTES') {
        dueDate.setMinutes(dueDate.getMinutes() + 2);
        desc = "Hết hạn sau 2 phút.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === '5_MINUTES') {
        dueDate.setMinutes(dueDate.getMinutes() + 5);
        desc = "Hết hạn sau 5 phút.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === 'OVERDUE_NOW') {
        dueDate.setMinutes(dueDate.getMinutes() - 1);
        desc = "Quá hạn ngay tức thì khi vừa mượn.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === '1_DAY') {
        dueDate.setDate(dueDate.getDate() + 1);
        desc = "Thời hạn mượn 24 giờ.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === '7_DAYS') {
        dueDate.setDate(dueDate.getDate() + 7);
        desc = "Thời hạn mượn 1 tuần.";
        if (box) box.classList.add('d-none');
    } else if (sel.value === 'CUSTOM') {
        if (box) {
            box.classList.remove('d-none');
            var inp = document.getElementById('customDueInput');
            if (inp && !inp.value) {
                var temp = new Date(now.getTime() + 60000);
                var offset = temp.getTimezoneOffset() * 60000;
                inp.value = (new Date(temp.getTime() - offset)).toISOString().slice(0, 16);
            }
            if (inp && inp.value) {
                dueDate = new Date(inp.value);
            }
        }
        desc = "Theo mốc thời gian bạn tự chọn.";
    } else {
        dueDate.setDate(dueDate.getDate() + 14);
        desc = "Quy định tiêu chuẩn thư viện: 14 ngày.";
        if (box) box.classList.add('d-none');
    }

    previewEl.innerText = formatVNTime(dueDate);
    if (descEl) descEl.innerText = desc;
}

if (document.getElementById('loanDurationSelect')) {
    updateDuePreview();
    setInterval(function() {
        var now = new Date();
        var curEl = document.getElementById('liveCurrentTime');
        if (curEl) curEl.innerText = formatVNTime(now);
    }, 1000);
}
</script>

<jsp:include page="includes/footer.jsp"/>
