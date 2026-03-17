<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore - SneakerZone</title>
</head>
<body>
<h2>Si è verificato un errore!</h2>

<p>Ci scusiamo per l'inconveniente. Qualcosa è andato storto durante l'elaborazione della richiesta.</p>

<c:if test="${not empty messaggioErrore}">
    <p style="color:red;">Dettagli: ${messaggioErrore}</p>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/home">Torna alla home</a>
</p>
</body>
</html>
