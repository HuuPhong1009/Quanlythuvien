<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String message = request.getParameter("message");
    String registered = request.getParameter("registered");
    String email = (String) request.getAttribute("email");
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card border-0 shadow-sm rounded-3">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <div class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3" style="width: 60px; height: 60px;">
                            <i class="bi bi-person-lock fs-2"></i>
                        </div>
                        <h4 class="fw-bold">Đăng Nhập Thư Viện</h4>
                        <p class="text-muted small">Nhập thông tin tài khoản độc giả hoặc thủ thư</p>
                    </div>

                    <% if (errorMessage != null) { %>
                    <div class="alert alert-danger py-2 small mb-3">
                        <i class="bi bi-exclamation-circle me-1"></i><%= errorMessage %>
                    </div>
                    <% } %>

                    <% if (message != null) { %>
                    <div class="alert alert-warning py-2 small mb-3">
                        <i class="bi bi-info-circle me-1"></i><%= message %>
                    </div>
                    <% } %>

                    <% if ("true".equals(registered)) { %>
                    <div class="alert alert-success py-2 small mb-3">
                        <i class="bi bi-check-circle me-1"></i>Đăng ký thẻ độc giả thành công! Vui lòng đăng nhập.
                    </div>
                    <% } %>

                    <form action="user" method="post">
                        <input type="hidden" name="action" value="login">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Email hoặc Số điện thoại:</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                                <input type="text" name="email" class="form-control" placeholder="name@domain.com"
                                       value="<%= (email != null) ? email : "" %>" required>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label small fw-semibold">Mật khẩu:</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-key"></i></span>
                                <input type="password" name="password" class="form-control" placeholder="••••••••" required>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm">
                            Đăng Nhập
                        </button>
                    </form>

                    <div class="mt-4 pt-3 border-top text-center">
                        <p class="small text-muted mb-2">Chưa có thẻ thư viện? <a href="user?action=register" class="fw-semibold text-primary">Đăng ký ngay</a></p>
                    </div>

                    <!-- DEMO ACCOUNTS HELPER -->
                    <div class="mt-3 p-2 bg-light rounded small text-muted">
                        <strong class="text-dark d-block mb-1"><i class="bi bi-info-circle me-1"></i>Tài khoản mẫu thử nghiệm:</strong>
                        <div>&bull; <strong>Thủ thư / Admin:</strong> <code>admin@thuvien.edu.vn</code> | MK: <code>admin123</code></div>
                        <div>&bull; <strong>Độc giả:</strong> <code>phong@student.edu.vn</code> | MK: <code>123456</code></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
