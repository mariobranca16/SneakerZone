<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout – SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="checkout-page">

        <c:if test="${not empty erroreStock}">
            <div class="alert alert-error alert-wide">
                <i class="ti ti-alert-circle"></i>&nbsp;<c:out value="${erroreStock}"/>
            </div>
        </c:if>


        <h1 class="checkout-title">Checkout</h1>


        <div class="checkout-card">
            <h2 class="checkout-section-title">
                <i class="ti ti-shopping-bag"></i> Riepilogo ordine
            </h2>

            <div class="checkout-items">
                <c:forEach var="item" items="${carrello.prodotti}">
                    <div class="checkout-item">
                        <c:if test="${not empty item.prodotto.imgPath}">
                            <img class="checkout-item-thumb"
                                 src="${pageContext.request.contextPath}${item.prodotto.imgPath}"
                                 alt="${item.prodotto.nome}">
                        </c:if>
                        <div class="checkout-item-info">
                            <span class="checkout-item-name">${item.prodotto.nome}</span>
                            <span class="checkout-item-meta">
                                Taglia&nbsp;${item.taglia}&nbsp;&middot;&nbsp;Qt&agrave;&nbsp;${item.quantita}
                            </span>
                        </div>
                        <span class="checkout-item-price">
                            <fmt:formatNumber value="${item.subtotale}"
                                              type="number" minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                        </span>
                    </div>
                </c:forEach>
            </div>

            <div class="checkout-total-row">
                <span class="checkout-total-label">Totale</span>
                <span class="checkout-total-value">
                    <fmt:formatNumber value="${carrello.totale}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                </span>
            </div>
        </div>


        <form class="checkout-form" method="post" action="${pageContext.request.contextPath}/checkout">


            <div class="checkout-card">
                <h2 class="checkout-section-title">
                    <i class="ti ti-map-pin"></i> Indirizzo di spedizione
                </h2>

                <div class="pay-grid">
                    <div class="pay-group pay-full">
                        <label class="pay-label" for="destinatario">Destinatario</label>
                        <input class="pay-input ${not empty erroreDestinatario ? 'pay-input-error' : ''}"
                               type="text" id="destinatario" name="destinatario"
                               placeholder="Nome e cognome del destinatario"
                               value="${fn:escapeXml(param.destinatario)}" required>
                        <c:if test="${not empty erroreDestinatario}">
                            <span class="pay-error">${erroreDestinatario}</span>
                        </c:if>
                    </div>
                    <div class="pay-group pay-full">
                        <label class="pay-label" for="via">Via / Indirizzo</label>
                        <input class="pay-input ${not empty erroreVia ? 'pay-input-error' : ''}"
                               type="text" id="via" name="via"
                               placeholder="Es. Via Roma 12"
                               value="${fn:escapeXml(param.via)}" required>
                        <c:if test="${not empty erroreVia}">
                            <span class="pay-error">${erroreVia}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="cap">CAP</label>
                        <input class="pay-input ${not empty erroreCap ? 'pay-input-error' : ''}"
                               type="text" id="cap" name="cap"
                               placeholder="Es. 20100" maxlength="10"
                               value="${fn:escapeXml(param.cap)}" required>
                        <c:if test="${not empty erroreCap}">
                            <span class="pay-error">${erroreCap}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="citta">Citt&agrave;</label>
                        <input class="pay-input ${not empty erroreCitta ? 'pay-input-error' : ''}"
                               type="text" id="citta" name="citta"
                               placeholder="Es. Milano"
                               value="${fn:escapeXml(param.citta)}" required>
                        <c:if test="${not empty erroreCitta}">
                            <span class="pay-error">${erroreCitta}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="provincia">Provincia</label>
                        <input class="pay-input ${not empty erroreProvincia ? 'pay-input-error' : ''}"
                               type="text" id="provincia" name="provincia"
                               placeholder="Es. MI" maxlength="5"
                               value="${fn:escapeXml(param.provincia)}" required>
                        <c:if test="${not empty erroreProvincia}">
                            <span class="pay-error">${erroreProvincia}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="paese">Paese</label>
                        <input class="pay-input ${not empty errorePaese ? 'pay-input-error' : ''}"
                               type="text" id="paese" name="paese"
                               placeholder="Es. Italia"
                               value="${fn:escapeXml(param.paese)}" required>
                        <c:if test="${not empty errorePaese}">
                            <span class="pay-error">${errorePaese}</span>
                        </c:if>
                    </div>
                </div>
            </div>


            <div class="checkout-card">
                <h2 class="checkout-section-title">
                    <i class="ti ti-credit-card"></i> Dati di pagamento
                </h2>

                <div class="pay-grid">
                    <div class="pay-group pay-full">
                        <label class="pay-label" for="nomeCarta">Nome sulla carta</label>
                        <input class="pay-input ${not empty erroreNomeCarta ? 'pay-input-error' : ''}"
                               type="text" id="nomeCarta" name="nomeCarta"
                               placeholder="Es. Mario Rossi"
                               value="${fn:escapeXml(param.nomeCarta)}"
                               autocomplete="cc-name" required>
                        <c:if test="${not empty erroreNomeCarta}">
                            <span class="pay-error">${erroreNomeCarta}</span>
                        </c:if>
                    </div>
                    <div class="pay-group pay-full">
                        <label class="pay-label" for="numeroCarta">Numero carta</label>
                        <input class="pay-input js-card-number ${not empty erroreNumeroCarta ? 'pay-input-error' : ''}"
                               type="text" id="numeroCarta" name="numeroCarta"
                               placeholder="1234 5678 9012 3456" maxlength="19"
                               value="${fn:escapeXml(param.numeroCarta)}"
                               autocomplete="cc-number" required>
                        <c:if test="${not empty erroreNumeroCarta}">
                            <span class="pay-error">${erroreNumeroCarta}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="scadenza">Scadenza</label>
                        <input class="pay-input js-card-expiry ${not empty erroreScadenza ? 'pay-input-error' : ''}"
                               type="text" id="scadenza" name="scadenza"
                               placeholder="MM/AA" maxlength="5"
                               value="${fn:escapeXml(param.scadenza)}"
                               autocomplete="cc-exp" required>
                        <c:if test="${not empty erroreScadenza}">
                            <span class="pay-error">${erroreScadenza}</span>
                        </c:if>
                    </div>
                    <div class="pay-group">
                        <label class="pay-label" for="cvv">CVV</label>
                        <input class="pay-input ${not empty erroreCvv ? 'pay-input-error' : ''}"
                               type="password" id="cvv" name="cvv"
                               placeholder="&bull;&bull;&bull;" maxlength="4"
                               autocomplete="cc-csc" required>
                        <c:if test="${not empty erroreCvv}">
                            <span class="pay-error">${erroreCvv}</span>
                        </c:if>
                    </div>
                </div>

            </div>


            <button class="btn-confirm" type="submit">
                <i class="ti ti-lock"></i> Conferma ordine
            </button>

        </form>

        <a class="checkout-back" href="${pageContext.request.contextPath}/carrello">
            <i class="ti ti-arrow-left"></i> Torna al carrello
        </a>

    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
<script src="${pageContext.request.contextPath}/js/checkout.js"></script>

</body>
</html>
