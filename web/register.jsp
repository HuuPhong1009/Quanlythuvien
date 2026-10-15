<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<jsp:include page="includes/header.jsp"/>
<jsp:include page="includes/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm rounded-3">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <div class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3" style="width: 60px; height: 60px;">
                            <i class="bi bi-card-heading fs-2"></i>
                        </div>
                        <h4 class="fw-bold">Đăng Ký Thẻ Độc Giả</h4>
                        <p class="text-muted small">Tạo thẻ thư viện trực tuyến để mượn sách online</p>
                    </div>

                    <% if (errorMessage != null) { %>
                    <div class="alert alert-danger py-2 small mb-3">
                        <i class="bi bi-exclamation-circle me-1"></i><%= errorMessage %>
                    </div>
                    <% } %>

                    <form action="user" method="post">
                        <input type="hidden" name="action" value="register">

                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Họ và tên độc giả:</label>
                            <input type="text" name="fullName" class="form-control" placeholder="Nguyễn Văn A" required>
                        </div>

                        <div class="row g-2 mb-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Email:</label>
                                <input type="email" name="email" class="form-control" placeholder="name@email.com" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Số điện thoại:</label>
                                <input type="tel" name="phone" class="form-control" placeholder="0901234567" required>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label small fw-semibold">Mật khẩu khởi tạo:</label>
                            <input type="password" name="password" class="form-control" placeholder="Tối thiểu 6 ký tự" required>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm">
                            Đăng Ký Thẻ
                        </button>
                    </form>

                    <div class="mt-4 pt-3 border-top text-center">
                        <p class="small text-muted mb-0">Đã có thẻ thư viện? <a href="user?action=login" class="fw-semibold text-primary">Đăng nhập</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
