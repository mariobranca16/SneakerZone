<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/registrazione.css">
</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="register-page">
        <div class="register-card page-card">
            <h1 class="register-title page-title">Crea il tuo account</h1>
            <p class="register-subtitle page-subtitle">Inserisci i tuoi dati per completare la registrazione</p>

            <form id="formRegistrazione" class="register-form" method="post"
                  action="${pageContext.request.contextPath}/registrazione" novalidate>
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome"
                           value="${fn:escapeXml(not empty formNome ? formNome : '')}" required>
                    <c:if test="${not empty erroreNome}">
                        <span class="field-error"><c:out value="${erroreNome}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome"
                           value="${fn:escapeXml(not empty formCognome ? formCognome : '')}" required>
                    <c:if test="${not empty erroreCognome}">
                        <span class="field-error"><c:out value="${erroreCognome}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email"
                           value="${fn:escapeXml(not empty formEmail ? formEmail : '')}" required>
                    <c:if test="${not empty erroreEmail}">
                        <span class="field-error"><c:out value="${erroreEmail}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="password">Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="password" name="password" required minlength="8" maxlength="64"
                               autocomplete="new-password">
                        <button type="button" class="toggle-password" aria-label="Mostra password"
                                onclick="togglePassword('password', this)">
                            <i class="ti ti-eye" aria-hidden="true"></i>
                        </button>
                    </div>
                    <span class="field-hint">8-64 caratteri, con maiuscola, minuscola, numero e simbolo, senza spazi.</span>
                    <c:if test="${not empty errorePassword}">
                        <span class="field-error"><c:out value="${errorePassword}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono"
                           value="${fn:escapeXml(not empty formTelefono ? formTelefono : '')}" required>
                    <c:if test="${not empty erroreTelefono}">
                        <span class="field-error"><c:out value="${erroreTelefono}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="dataNascita">Data di nascita</label>
                    <input type="date" id="dataNascita" name="dataNascita"
                           value="${fn:escapeXml(not empty formDataNascita ? formDataNascita : '')}" required>
                    <c:if test="${not empty erroreDataNascita}">
                        <span class="field-error"><c:out value="${erroreDataNascita}"/></span>
                    </c:if>
                </div>

                <div class="register-actions">
                    <button class="btn-primary" type="submit">Registrati</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>

</body>
</html>
