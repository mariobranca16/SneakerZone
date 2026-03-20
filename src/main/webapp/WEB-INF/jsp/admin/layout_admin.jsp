<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-base.css">
<script src="${pageContext.request.contextPath}/js/common.js" defer></script>
<header class="admin-header">
    <div class="admin-header-row">
        <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="ti ti-shield-check"></i>
            Dashboard
            <span class="admin-logo-badge">Admin</span>
        </a>
        <nav class="admin-nav">
            <a class="admin-nav-link" href="${pageContext.request.contextPath}/admin/prodotti">
                <i class="ti ti-package"></i> Prodotti
            </a>
            <a class="admin-nav-link" href="${pageContext.request.contextPath}/admin/utenti">
                <i class="ti ti-users"></i> Utenti
            </a>
            <a class="admin-nav-link" href="${pageContext.request.contextPath}/admin/ordini">
                <i class="ti ti-truck"></i> Ordini
            </a>
        </nav>
        <a class="admin-back-link" href="${pageContext.request.contextPath}/home">
            <i class="ti ti-arrow-left"></i> Torna al sito
        </a>
    </div>
</header>
<main>
    <div class="admin-container">
