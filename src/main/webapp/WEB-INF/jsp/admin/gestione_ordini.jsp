<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-ordini.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>

<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Gestione ordini</h1>
        <p class="admin-page-subtitle">Consulta e aggiorna lo stato degli ordini</p>
    </div>
</div>

<div class="admin-table-wrap">
<table class="admin-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Utente</th>
        <th>Data</th>
        <th>Totale</th>
        <th>Stato</th>
        <th>Aggiorna stato</th>
    </tr>
    </thead>
    <tbody>

    <c:choose>
        <c:when test="${empty ordini}">
            <tr>
                <td colspan="6" style="color:#6B7280; font-style:italic;">Nessun ordine presente.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="o" items="${ordini}">
                <tr>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/ordine?id=${o.id}">#${o.id}</a>
                    </td>
                    <td>${emailUtenti[o.idUtente]}</td>
                    <td>${o.dataOrdineFormattata}</td>
                    <td style="font-weight:800;"><fmt:formatNumber value="${o.calcolaTotaleOrdine()}" type="number" minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;</td>
                    <td>
                        <c:choose>
                            <c:when test="${o.stato.name() == 'IN_ELABORAZIONE'}">
                                <span class="badge badge--elaborazione"><i class="fas fa-clock"></i> ${o.stato.label}</span>
                            </c:when>
                            <c:when test="${o.stato.name() == 'SPEDITO'}">
                                <span class="badge badge--spedito"><i class="fas fa-truck"></i> ${o.stato.label}</span>
                            </c:when>
                            <c:when test="${o.stato.name() == 'CONSEGNATO'}">
                                <span class="badge badge--consegnato"><i class="fas fa-circle-check"></i> ${o.stato.label}</span>
                            </c:when>
                            <c:when test="${o.stato.name() == 'ANNULLATO'}">
                                <span class="badge badge--annullato"><i class="fas fa-ban"></i> ${o.stato.label}</span>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td class="admin-table-actions">
                        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/ordini">
                            <input type="hidden" name="id" value="${o.id}">
                            <select name="stato" class="select-inline">
                                <c:forEach var="s" items="${stati}">
                                    <option value="${s}" <c:if test="${o.stato == s}">selected</c:if>>${s.label}</option>
                                </c:forEach>
                            </select>
                            <button class="btn btn--small btn--primary" type="submit">Aggiorna</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    </tbody>
</table>
</div>

</div>
</main>

<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>

</body>
</html>
