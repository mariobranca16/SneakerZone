<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- Pagina di login. In caso di credenziali errate, la servlet rimanda qui
     con ${emailInserita} e ${errore}. Dopo il login reindirizza a redirectDopoLogin. --%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SneakerZone - Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main>
    <div class="login-page">
        <div class="login-card">
            <h1 class="login-title">Accedi al tuo account</h1>
            <p class="login-subtitle">Inserisci le credenziali per continuare</p>
            <!-- form login: l'errore è mostrato sotto la password, non sotto l'email,
                 per non rivelare se è sbagliata l'email o la password -->
            <form id="formLogin" method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input id="email" name="email" type="email" value="${fn:escapeXml(emailInserita)}" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="password-wrapper">
                        <input id="password" name="password" type="password" required>
                        <button type="button" class="toggle-password" aria-label="Mostra password"
                                onclick="togglePassword('password', this)">
                            <i class="ti ti-eye" aria-hidden="true"></i>
                        </button>
                    </div>
                    <c:if test="${not empty errore}">
                        <span class="field-error"><c:out value="${errore}"/></span>
                    </c:if>
                </div>
                <div class="login-actions">
                    <button class="btn-primary" type="submit">Accedi</button>
                </div>
            </form>
            <div class="login-links">
                Non hai un account?
                <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
            </div>
        </div>
    </div>
</main>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
</body>
</html>
