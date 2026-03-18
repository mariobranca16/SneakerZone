<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="ctx" content="${pageContext.request.contextPath}">
    <title>Nuovo indirizzo – SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/aggiungi_indirizzo.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="addr-page">
        <div class="addr-card">

            <c:choose>
                <c:when test="${from == 'profile'}">
                    <a class="addr-back" href="${pageContext.request.contextPath}/myAccount">
                        <i class="ti ti-arrow-left"></i> Torna al profilo
                    </a>
                </c:when>
                <c:otherwise>
                    <a class="addr-back" href="${pageContext.request.contextPath}/checkout">
                        <i class="ti ti-arrow-left"></i> Torna al checkout
                    </a>
                </c:otherwise>
            </c:choose>

            <div class="addr-header">
                <div class="addr-icon" aria-hidden="true">
                    <i class="ti ti-map-pin"></i>
                </div>
                <h1 class="addr-title">Nuovo indirizzo di spedizione</h1>
            </div>

            <form id="formIndirizzo" class="addr-form" method="post"
                  action="${pageContext.request.contextPath}/aggiungi-indirizzo" novalidate>
                <input type="hidden" name="from" value="${fn:escapeXml(from)}">

                <div class="form-group">
                    <label class="form-label" for="destinatario">Destinatario</label>
                    <input class="form-input" type="text" id="destinatario" name="destinatario"
                           placeholder="Nome e cognome del destinatario"
                           value="${fn:escapeXml(not empty formDestinatario ? formDestinatario : '')}" required>
                    <c:if test="${not empty erroreDestinatario}">
                        <span class="field-error"><c:out value="${erroreDestinatario}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label class="form-label" for="via">Via / Indirizzo</label>
                    <input class="form-input" type="text" id="via" name="via"
                           placeholder="Es. Via Roma 12"
                           value="${fn:escapeXml(not empty formVia ? formVia : '')}" required>
                    <c:if test="${not empty erroreVia}">
                        <span class="field-error"><c:out value="${erroreVia}"/></span>
                    </c:if>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="cap">CAP</label>
                        <input class="form-input" type="text" id="cap" name="cap"
                               placeholder="Es. 20100" maxlength="10"
                               value="${fn:escapeXml(not empty formCap ? formCap : '')}" required>
                        <c:if test="${not empty erroreCap}">
                            <span class="field-error"><c:out value="${erroreCap}"/></span>
                        </c:if>
                    </div>
                    <div class="form-group form-group-grow">
                        <label class="form-label" for="citta">Citt&agrave;</label>
                        <input class="form-input" type="text" id="citta" name="citta"
                               placeholder="Es. Milano"
                               value="${fn:escapeXml(not empty formCitta ? formCitta : '')}" required>
                        <c:if test="${not empty erroreCitta}">
                            <span class="field-error"><c:out value="${erroreCitta}"/></span>
                        </c:if>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label" for="provincia">Provincia</label>
                        <input class="form-input" type="text" id="provincia" name="provincia"
                               placeholder="Es. MI" maxlength="5" list="list-province"
                               value="${fn:escapeXml(not empty formProvincia ? formProvincia : '')}" required>
                        <datalist id="list-province"></datalist>
                        <c:if test="${not empty erroreProvincia}">
                            <span class="field-error"><c:out value="${erroreProvincia}"/></span>
                        </c:if>
                    </div>
                    <div class="form-group form-group-grow">
                        <label class="form-label" for="paese">Paese</label>
                        <input class="form-input" type="text" id="paese" name="paese"
                               placeholder="Es. Italia" list="list-nazioni"
                               value="${fn:escapeXml(not empty formPaese ? formPaese : '')}" required>
                        <datalist id="list-nazioni"></datalist>
                        <c:if test="${not empty errorePaese}">
                            <span class="field-error"><c:out value="${errorePaese}"/></span>
                        </c:if>
                    </div>
                </div>

                <button class="addr-submit" type="submit">
                    Salva indirizzo
                </button>

            </form>

        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
<script src="${pageContext.request.contextPath}/js/autocomplete.js"></script>
</body>
</html>
