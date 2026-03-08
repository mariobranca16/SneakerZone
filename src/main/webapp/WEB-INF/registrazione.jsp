<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Registrazione - SneakerZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/registrazione.css">
</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp" />

<main>
    <div class="register-page">
        <div class="register-card">
            <h1 class="register-title">Crea il tuo account</h1>
            <p class="register-subtitle">Inserisci i tuoi dati per completare la registrazione</p>

            <c:if test="${not empty errore}">
                <div class="alert alert-error">${errore}</div>
                <a class="register-link" href="${pageContext.request.contextPath}/home">Torna alla home</a>
            </c:if>

            <form id="formRegistrazione" class="register-form" method="post" action="${pageContext.request.contextPath}/registrazione">
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" required>
                </div>

                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" required>
                </div>

                <div class="form-group full">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div class="form-group full">
                    <label for="password">Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="password" name="password" required>
                        <button type="button" class="toggle-password" aria-label="Mostra password" onclick="togglePassword('password', this)">
                            <i class="fa-regular fa-eye" aria-hidden="true"></i>
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono" required>
                </div>

                <div class="form-group">
                    <label for="dataNascita">Data di nascita</label>
                    <input type="date" id="dataNascita" name="dataNascita" required>
                </div>

                <div class="register-actions">
                    <button class="btn-primary" type="submit">Registrati</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
<%-- togglePassword è definito in common.js (caricato dal footer) --%>

</body>
</html>
