<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Pagina di errore generica (404, 500, 403). Il codice HTTP viene letto da requestScope
     e usato per mostrare il messaggio corretto. ${messaggioErrore} è impostato dalle servlet. --%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore - SneakerZone</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/errore.css">
</head>
<body>
<c:set var="statusCode" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<div class="error-card">
    <div class="error-icon">
        <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 9v4m0 4h.01M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
        </svg>
    </div>
    <c:choose>
        <c:when test="${statusCode == 404}">
            <div class="error-code">Errore <span>${statusCode}</span></div>
            <h1>Pagina non trovata.</h1>
            <p>La pagina che stai cercando non esiste o è stata spostata.</p>
        </c:when>
        <c:when test="${statusCode == 500}">
            <div class="error-code">Errore <span>${statusCode}</span></div>
            <h1>Errore del server.</h1>
            <p>Qualcosa è andato storto sul nostro server. Riprova tra qualche minuto.</p>
        </c:when>
        <c:when test="${statusCode == 403}">
            <div class="error-code">Errore <span>${statusCode}</span></div>
            <h1>Accesso negato.</h1>
            <p>Non hai i permessi per accedere a questa risorsa.</p>
        </c:when>
        <c:otherwise>
            <div class="error-code">
                Errore<c:if test="${not empty statusCode}"> <span>${statusCode}</span></c:if>
            </div>
            <h1>Qualcosa è andato storto.</h1>
            <p>Si è verificato un errore durante l'elaborazione della richiesta.</p>
        </c:otherwise>
    </c:choose>
    <div class="error-divider"></div>
    <c:if test="${not empty messaggioErrore}">
        <p class="error-detail">${messaggioErrore}</p>
    </c:if>
    <a href="${pageContext.request.contextPath}/home">Torna alla home</a>
</div>
</body>
</html>
