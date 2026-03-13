<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>

</head>
<body>

<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>

<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Gestione utenti</h1>
        <p class="admin-page-subtitle">Gestisci ruoli e account degli utenti registrati</p>
    </div>
</div>

<div class="admin-table-wrap">
<table class="admin-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Email</th>
        <th>Nome</th>
        <th>Cognome</th>
        <th>Admin</th>
        <th>Azioni</th>
    </tr>
    </thead>
    <tbody>

    <c:set var="me" value="${sessionScope.utenteConnesso}" />

    <c:forEach var="u" items="${utenti}">
        <c:set var="isMe" value="${me != null && me.id == u.id}" />
        <tr>
            <td>
                    ${u.id}
                <c:if test="${isMe}"> (Tu)</c:if>
            </td>
            <td>${u.email}</td>
            <td>${u.nome}</td>
            <td>${u.cognome}</td>
            <td>
                <c:choose>
                    <c:when test="${u.admin}">Si</c:when>
                    <c:otherwise>No</c:otherwise>
                </c:choose>
            </td>
            <td class="admin-table-actions">
                <c:choose>
                    <c:when test="${u.admin}">
                        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/utenti">
                            <input type="hidden" name="id" value="${u.id}">
                            <input type="hidden" name="azione" value="retrocedi">
                            <button class="btn btn--small" type="submit" <c:if test="${isMe}">disabled</c:if>>Rimuovi admin</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/utenti">
                            <input type="hidden" name="id" value="${u.id}">
                            <input type="hidden" name="azione" value="promuovi">
                            <button class="btn btn--small btn--primary" type="submit">Rendi admin</button>
                        </form>
                    </c:otherwise>
                </c:choose>

                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/utenti">
                    <input type="hidden" name="id" value="${u.id}">
                    <input type="hidden" name="azione" value="elimina">
                    <button class="btn btn--small btn--danger" type="submit"
                            data-confirm="Sei sicuro di voler eliminare questo utente?"
                            <c:if test="${isMe}">disabled</c:if>>Elimina</button>
                </form>

            </td>
        </tr>
    </c:forEach>

    </tbody>
</table>
</div>

</div>
</main>

<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>

</body>
</html>
