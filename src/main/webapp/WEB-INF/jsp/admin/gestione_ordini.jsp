<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- Lista ordini admin. ${emailUtenti} è una mappa idUtente → email;
     ${stati} contiene tutti i valori dell'enum StatoOrdine per il select inline. --%>
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

<!-- flash messages da sessione (pattern PRG: redirect dopo POST) -->
<c:if test="${not empty sessionScope.flashSuccesso}">
    <div class="alert alert-success"><c:out value="${sessionScope.flashSuccesso}"/></div>
    <c:remove var="flashSuccesso" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashErrore}">
    <div class="alert alert-error"><c:out value="${sessionScope.flashErrore}"/></div>
    <c:remove var="flashErrore" scope="session"/>
</c:if>

<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Gestione ordini</h1>
        <!-- conteggio ordini -->
        <p class="admin-page-subtitle">
            <c:choose>
                <c:when test="${empty ordini}">Nessun ordine presente</c:when>
                <c:otherwise>${fn:length(ordini)} ordini totali</c:otherwise>
            </c:choose>
        </p>
    </div>
</div>

<div class="admin-card admin-card--flush">
    <c:choose>
        <c:when test="${empty ordini}">
            <div class="prod-empty">
                <i class="ti ti-truck"></i>
                <p>Nessun ordine presente.</p>
            </div>
        </c:when>
        <c:otherwise>
            <!-- tabella ordini: ogni riga ha un form inline per aggiornare lo stato -->
            <div class="admin-table-wrap ordini-table-wrap">
                <table class="admin-table ordini-table">
                    <thead>
                    <tr>
                        <th class="col-id col-center">ID</th>
                        <th class="col-utente">Utente</th>
                        <th class="col-data col-hide-sm">Data</th>
                        <th class="col-totale col-center col-hide-xs">Totale</th>
                        <th class="col-aggiorna col-center">Aggiorna stato</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- ogni riga ha un form inline per aggiornare lo stato senza aprire il dettaglio -->
                    <c:forEach var="o" items="${ordini}">
                        <tr>
                            <!-- link al dettaglio del singolo ordine -->
                            <td class="col-id col-center">
                                <a href="${pageContext.request.contextPath}/admin/ordine?id=${o.id}">#${o.id}</a>
                            </td>
                            <!-- email dell'utente dalla mappa emailUtenti -->
                            <td class="col-utente">${emailUtenti[o.idUtente]}</td>
                            <td class="col-data col-hide-sm">${o.dataOrdineFormattata}</td>
                            <td class="col-totale col-center col-hide-xs">
                                <span class="ordine-totale-val">
                                    <fmt:formatNumber value="${o.totaleOrdine}" type="number"
                                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                </span>
                            </td>
                            <!-- form cambio stato inline: ${stati} contiene tutti i valori di StatoOrdine -->
                            <td class="col-aggiorna col-center">
                                <div class="admin-table-actions">
                                    <form class="inline-form" method="post"
                                          action="${pageContext.request.contextPath}/admin/ordini">
                                        <input type="hidden" name="id" value="${o.id}">
                                        <select name="stato" class="select-inline">
                                            <c:forEach var="s" items="${stati}">
                                                <option value="${s}"
                                                        <c:if test="${o.stato == s}">selected</c:if>>${s.label}</option>
                                            </c:forEach>
                                        </select>
                                        <button class="btn btn--small btn--primary" type="submit">Aggiorna</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</div>
</main>
<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>
</body>
</html>
