<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Dashboard admin: tre tile di navigazione verso Utenti, Prodotti e Ordini. --%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-dashboard.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>
<div class="admin-welcome">
    <h2 class="admin-welcome-title">Pannello amministratore</h2>
    <p class="admin-welcome-subtitle">Seleziona un'area da gestire</p>
</div>
<div class="admin-grid-3">
    <a class="admin-tile" href="${pageContext.request.contextPath}/admin/utenti">
        <div class="admin-tile-icon"><i class="ti ti-users"></i></div>
        <div class="admin-tile-title">Utenti</div>
        <div class="admin-tile-desc">Gestisci utenti registrati, ruoli e stato account.</div>
    </a>
    <a class="admin-tile" href="${pageContext.request.contextPath}/admin/prodotti">
        <div class="admin-tile-icon"><i class="ti ti-package"></i></div>
        <div class="admin-tile-title">Prodotti</div>
        <div class="admin-tile-desc">Gestisci catalogo, disponibilita e visibilita.</div>
    </a>
    <a class="admin-tile" href="${pageContext.request.contextPath}/admin/ordini">
        <div class="admin-tile-icon"><i class="ti ti-truck"></i></div>
        <div class="admin-tile-title">Ordini</div>
        <div class="admin-tile-desc">Consulta ordini e aggiorna lo stato.</div>
    </a>
</div>
</div>
</main>
<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>
</body>
</html>
