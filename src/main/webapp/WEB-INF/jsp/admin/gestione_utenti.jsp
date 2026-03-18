<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-utenti.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>

<c:if test="${not empty sessionScope.flashErrore}">
    <div class="alert alert-error">
        <c:out value="${sessionScope.flashErrore}"/>
    </div>
    <c:remove var="flashErrore" scope="session"/>
</c:if>


<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Gestione utenti</h1>
        <p class="admin-page-subtitle">
            <c:choose>
                <c:when test="${empty utenti}">Nessun utente registrato</c:when>
                <c:otherwise>${fn:length(utenti)} utenti registrati</c:otherwise>
            </c:choose>
        </p>
    </div>
</div>


<div class="admin-card admin-card--flush">

    <c:choose>
        <c:when test="${empty utenti}">
            <div class="prod-empty">
                <i class="ti ti-users"></i>
                <p>Nessun utente registrato.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="admin-table-wrap utenti-table-wrap">
                <table class="admin-table utenti-table">
                    <thead>
                    <tr>
                        <th class="col-id col-center">ID</th>
                        <th class="col-email">Email</th>
                        <th class="col-hide-sm">Nome</th>
                        <th class="col-hide-sm">Cognome</th>
                        <th class="col-actions col-center">Azioni</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:set var="me" value="${sessionScope.utenteConnesso}"/>

                    <c:forEach var="u" items="${utenti}">
                        <c:set var="isMe" value="${me != null && me.id == u.id}"/>
                        <tr>
                            <td class="col-id col-center">${u.id}</td>
                            <td class="col-email td-email">${u.email}</td>
                            <td class="col-hide-sm">${u.nome}</td>
                            <td class="col-hide-sm">${u.cognome}</td>
                            <td class="col-actions col-center">
                                <div class="admin-table-actions">
                                    <form class="inline-form" method="post"
                                          action="${pageContext.request.contextPath}/admin/utenti">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <input type="hidden" name="azione" value="elimina">
                                        <button class="btn btn--small btn--danger" type="submit"
                                                data-confirm="Sei sicuro di voler eliminare questo utente?"
                                                <c:if test="${isMe}">disabled</c:if>>Elimina
                                        </button>
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
