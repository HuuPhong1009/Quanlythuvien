<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="vi" data-bs-theme="dark" style="color-scheme: dark !important;">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="color-scheme" content="dark">
    <title>Thư Viện Sách - Tra Cứu &amp; Mượn Sách</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Cache-busted Main CSS -->
    <link href="${pageContext.request.contextPath}/styles/main.css?v=20261014_trueblack" rel="stylesheet">
    <style>
        /* ====================================================
           LUXURY TRUE BLACK & MONOCHROME THEME
           Thuần màu đen, không ám xanh, dịu mắt và cực kỳ sang trọng
           ==================================================== */
        :root, html, body {
            color-scheme: dark !important;
            forced-color-adjust: none !important;
            background-color: #0a0a0a !important;
            background-image: none !important;
            color: #f5f5f5 !important;
            font-family: 'Inter', system-ui, -apple-system, sans-serif !important;
        }

        /* Pure Dark Navbar */
        .navbar-basic {
            background-color: #121212 !important;
            background-image: none !important;
            border-bottom: 1px solid #222222 !important;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.6) !important;
            padding: 0.85rem 0 !important;
        }

        .navbar-brand-basic {
            font-weight: 700 !important;
            font-size: 1.3rem !important;
            color: #ffffff !important;
            text-decoration: none !important;
            display: inline-flex !important;
            align-items: center !important;
            gap: 0.5rem !important;
        }

        .navbar-brand-basic i, .navbar-brand-basic .text-primary {
            color: #ffffff !important;
        }

        .nav-link-basic {
            color: #a3a3a3 !important;
            font-weight: 500 !important;
            font-size: 0.95rem !important;
            padding: 0.45rem 0.9rem !important;
            border-radius: 8px !important;
            text-decoration: none !important;
            transition: all 0.15s ease !important;
        }

        .nav-link-basic:hover, .nav-link-basic.active {
            color: #ffffff !important;
            background-color: #222222 !important;
        }

        /* Pure Dark Search Toolbar */
        .toolbar-box {
            background-color: #141414 !important;
            background-image: none !important;
            border: 1px solid #262626 !important;
            border-radius: 16px !important;
            padding: 1.25rem !important;
            margin-bottom: 1.25rem !important;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4) !important;
        }

        .form-control, .form-select, .input-group-text {
            background-color: #0a0a0a !important;
            background-image: none !important;
            color: #ffffff !important;
            border-color: #2d2d2d !important;
        }

        .form-control:focus, .form-select:focus {
            background-color: #121212 !important;
            color: #ffffff !important;
            border-color: #666666 !important;
            box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.15) !important;
        }

        .form-control::placeholder {
            color: #555555 !important;
        }

        /* Pure Dark Category Pills */
        .cat-pills {
            display: flex !important;
            flex-wrap: wrap !important;
            gap: 0.6rem !important;
            margin-bottom: 1.75rem !important;
        }

        .cat-pill {
            display: inline-block !important;
            padding: 0.45rem 1rem !important;
            font-size: 0.86rem !important;
            font-weight: 500 !important;
            color: #a3a3a3 !important;
            background-color: #141414 !important;
            background-image: none !important;
            border: 1px solid #2a2a2a !important;
            border-radius: 50px !important;
            text-decoration: none !important;
            transition: all 0.2s ease !important;
        }

        .cat-pill:hover {
            border-color: #555555 !important;
            color: #ffffff !important;
            background-color: #222222 !important;
            transform: translateY(-1px) !important;
        }

        .cat-pill.active {
            background-color: #ffffff !important;
            background-image: none !important;
            color: #0a0a0a !important;
            border-color: #ffffff !important;
            box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2) !important;
            font-weight: 700 !important;
        }

        /* Pure Dark Book Card */
        .book-card {
            background-color: #141414 !important;
            background-image: none !important;
            border: 1px solid #242424 !important;
            border-radius: 16px !important;
            padding: 1.15rem !important;
            height: 100% !important;
            display: flex !important;
            flex-direction: column !important;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
            transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, background-color 0.2s ease !important;
        }

        .book-card:hover {
            transform: translateY(-4px) !important;
            box-shadow: 0 14px 30px rgba(0, 0, 0, 0.7) !important;
            border-color: #4a4a4a !important;
            background-color: #1c1c1c !important;
        }

        /* Pure Black Book Cover Stage */
        .book-cover-box {
            width: 100% !important;
            height: 235px !important;
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            background-color: #0a0a0a !important;
            background-image: none !important;
            border: 1px solid #202020 !important;
            border-radius: 12px !important;
            padding: 14px !important;
            margin-bottom: 1rem !important;
            overflow: hidden !important;
        }

        .book-cover-box img {
            width: 145px !important;
            height: 205px !important;
            object-fit: cover !important;
            border-radius: 6px !important;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.8) !important;
            background-color: #171717 !important;
        }

        .book-title {
            font-weight: 700 !important;
            font-size: 1rem !important;
            color: #ffffff !important;
            margin-bottom: 0.25rem !important;
            display: -webkit-box !important;
            -webkit-line-clamp: 2 !important;
            line-clamp: 2 !important;
            -webkit-box-orient: vertical !important;
            overflow: hidden !important;
            line-height: 1.4 !important;
            min-height: 2.8rem !important;
        }

        .book-title a {
            color: #ffffff !important;
            text-decoration: none !important;
            transition: color 0.15s ease !important;
        }

        .book-title a:hover {
            color: #d4d4d4 !important;
        }

        .book-author {
            font-size: 0.86rem !important;
            color: #8c8c8c !important;
            margin-bottom: 0.85rem !important;
            white-space: nowrap !important;
            overflow: hidden !important;
            text-overflow: ellipsis !important;
        }

        /* Status badges */
        .stock-badge-in {
            font-size: 0.78rem !important;
            font-weight: 600 !important;
            color: #4ade80 !important;
            background-color: rgba(74, 222, 128, 0.12) !important;
            background-image: none !important;
            border: 1px solid rgba(74, 222, 128, 0.25) !important;
            padding: 0.28rem 0.65rem !important;
            border-radius: 50px !important;
        }

        .stock-badge-out {
            font-size: 0.78rem !important;
            font-weight: 600 !important;
            color: #f87171 !important;
            background-color: rgba(248, 113, 113, 0.12) !important;
            background-image: none !important;
            border: 1px solid rgba(248, 113, 113, 0.25) !important;
            padding: 0.28rem 0.65rem !important;
            border-radius: 50px !important;
        }

        /* Generic cards, tables, containers */
        .card, .bg-white, .table, .footer-basic {
            background-color: #141414 !important;
            background-image: none !important;
            color: #f5f5f5 !important;
            border-color: #262626 !important;
        }

        .card-header {
            background-color: #1a1a1a !important;
            border-bottom: 1px solid #262626 !important;
            color: #ffffff !important;
        }

        .card-footer {
            background-color: #0f0f0f !important;
            border-top: 1px solid #262626 !important;
        }

        .table {
            color: #f5f5f5 !important;
            --bs-table-bg: transparent !important;
            --bs-table-striped-bg: #1a1a1a !important;
            --bs-table-hover-bg: #222222 !important;
            --bs-table-border-color: #282828 !important;
        }

        .table th {
            color: #a3a3a3 !important;
            background-color: #101010 !important;
            border-bottom: 2px solid #2e2e2e !important;
        }

        .table td {
            border-bottom: 1px solid #222222 !important;
        }

        /* Pure Dark Pagination */
        .pagination .page-link {
            background-color: #141414 !important;
            background-image: none !important;
            color: #a3a3a3 !important;
            border: 1px solid #2a2a2a !important;
            margin: 0 3px !important;
            border-radius: 8px !important;
        }

        .pagination .page-link:hover {
            background-color: #222222 !important;
            color: #ffffff !important;
            border-color: #555555 !important;
        }

        .pagination .page-item.active .page-link {
            background-color: #ffffff !important;
            background-image: none !important;
            color: #0a0a0a !important;
            border-color: #ffffff !important;
            box-shadow: 0 4px 12px rgba(255, 255, 255, 0.25) !important;
            font-weight: 700 !important;
        }

        .pagination .page-item.disabled .page-link {
            background-color: #0a0a0a !important;
            background-image: none !important;
            color: #404040 !important;
            border-color: #1c1c1c !important;
        }

        /* High-contrast Monochrome Buttons (Không còn màu xanh) */
        .btn-primary, .btn-primary:active, .btn-primary:focus {
            background-color: #ffffff !important;
            background-image: none !important;
            border-color: #ffffff !important;
            color: #0a0a0a !important;
            font-weight: 600 !important;
            box-shadow: 0 2px 8px rgba(255, 255, 255, 0.15) !important;
        }

        .btn-primary:hover {
            background-color: #e5e5e5 !important;
            border-color: #e5e5e5 !important;
            color: #000000 !important;
        }

        .btn-outline-primary {
            background-color: transparent !important;
            color: #ffffff !important;
            border-color: #383838 !important;
            font-weight: 500 !important;
        }

        .btn-outline-primary:hover {
            background-color: #ffffff !important;
            color: #0a0a0a !important;
            border-color: #ffffff !important;
        }

        .btn-light {
            background-color: #222222 !important;
            color: #f5f5f5 !important;
            border-color: #333333 !important;
        }

        .btn-light:hover {
            background-color: #2c2c2c !important;
            color: #ffffff !important;
        }

        .btn-outline-secondary {
            background-color: transparent !important;
            color: #a3a3a3 !important;
            border-color: #383838 !important;
        }

        .btn-outline-secondary:hover {
            background-color: #262626 !important;
            color: #ffffff !important;
            border-color: #555555 !important;
        }

        .text-primary {
            color: #ffffff !important;
        }

        .bg-primary {
            background-color: #ffffff !important;
            color: #0a0a0a !important;
        }

        /* Detail cover glow */
        .book-cover-detail {
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.8), 0 0 15px rgba(255, 255, 255, 0.08) !important;
        }

        /* Modals & Dropdowns */
        .modal-content, .dropdown-menu {
            background-color: #141414 !important;
            border: 1px solid #2a2a2a !important;
            color: #f5f5f5 !important;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.8) !important;
        }

        .dropdown-item {
            color: #d4d4d4 !important;
        }

        .dropdown-item:hover {
            background-color: #222222 !important;
            color: #ffffff !important;
        }

        .dropdown-divider {
            border-top: 1px solid #262626 !important;
        }

        /* Lists & Border Utilities */
        .list-group-item {
            background-color: #141414 !important;
            border-color: #262626 !important;
            color: #f5f5f5 !important;
        }

        hr, .border, .border-top, .border-bottom, .border-start, .border-end {
            border-color: #262626 !important;
        }

        .text-dark {
            color: #ffffff !important;
        }

        .bg-light {
            background-color: #1a1a1a !important;
            color: #f5f5f5 !important;
            border-color: #2e2e2e !important;
        }

        .badge.bg-light {
            background-color: #222222 !important;
            color: #f5f5f5 !important;
            border: 1px solid #383838 !important;
        }

        .text-muted {
            color: #8c8c8c !important;
        }

        /* Refined Neutral Alerts */
        .alert-info {
            background-color: rgba(255, 255, 255, 0.08) !important;
            color: #ffffff !important;
            border-color: #383838 !important;
        }

        .alert-success {
            background-color: rgba(74, 222, 128, 0.1) !important;
            color: #4ade80 !important;
            border-color: rgba(74, 222, 128, 0.25) !important;
        }

        .alert-warning {
            background-color: rgba(251, 191, 36, 0.1) !important;
            color: #fbbf24 !important;
            border-color: rgba(251, 191, 36, 0.25) !important;
        }

        .alert-danger {
            background-color: rgba(248, 113, 113, 0.1) !important;
            color: #f87171 !important;
            border-color: rgba(248, 113, 113, 0.25) !important;
        }

        /* Footer */
        .footer-basic {
            background-color: #0a0a0a !important;
            border-top: 1px solid #1f1f1f !important;
            color: #737373 !important;
        }
    </style>
</head>
<body class="d-flex flex-column min-vh-100">
