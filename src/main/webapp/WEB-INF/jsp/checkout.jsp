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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/account.css">
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


        <h1 class="checkout-title page-title">Checkout</h1>


        <div class="checkout-card page-card">
            <h2 class="checkout-section-title">
                <i class="ti ti-shopping-bag"></i> Riepilogo ordine
            </h2>

            <div class="checkout-items">
                <c:forEach var="item" items="${carrello.prodotti}">
                    <div class="checkout-item">
                        <c:if test="${not empty item.prodotto.imgPath}">
                            <img class="checkout-item-thumb product-thumb"
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


        <div class="checkout-card page-card" id="checkout-addr-section">
            <h2 class="checkout-section-title">
                <i class="ti ti-map-pin"></i> Indirizzo di spedizione
            </h2>

            <c:if test="${not empty erroreDestinatario or not empty erroreVia or not empty erroreCap or not empty erroreCitta or not empty erroreProvincia or not empty errorePaese}">
                <div class="alert alert-error checkout-addr-alert">
                    Seleziona o inserisci un indirizzo di spedizione valido.
                </div>
            </c:if>

            <c:if test="${not empty indirizzi}">
                <div class="profilo-addr-list">
                    <c:forEach var="ind" items="${indirizzi}">
                        <div class="profilo-addr-card addr-card-selectable"
                             data-id="${ind.id}"
                             data-destinatario="${fn:escapeXml(ind.destinatario)}"
                             data-via="${fn:escapeXml(ind.via)}"
                             data-cap="${fn:escapeXml(ind.cap)}"
                             data-citta="${fn:escapeXml(ind.citta)}"
                             data-provincia="${fn:escapeXml(ind.provincia)}"
                             data-paese="${fn:escapeXml(ind.paese)}"
                             onclick="selezionaIndirizzoCheckout(this)">
                            <input class="addr-radio" type="radio" name="addr-sel" tabindex="-1">
                            <div class="profilo-addr-info">
                                <span class="addr-destinatario"><c:out value="${ind.destinatario}"/></span>
                                <span class="addr-detail"><c:out value="${ind.via}"/></span>
                                <span class="addr-detail">
                                    <c:out value="${ind.cap}"/> <c:out value="${ind.citta}"/> (<c:out
                                        value="${ind.provincia}"/>), <c:out value="${ind.paese}"/>
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <button type="button" class="btn-aggiungi-indirizzo" onclick="apriNuovoIndirizzo()">
                Aggiungi un nuovo indirizzo
            </button>

            <div class="profilo-edit-section${not empty apriFormIndirizzo ? ' open' : ''}" id="indirizzoFormWrap"
                 data-action-nuovo="${pageContext.request.contextPath}/aggiungi-indirizzo"
                 data-action-modifica="${pageContext.request.contextPath}/myAccount/indirizzo/modifica">
                <h3 class="edit-section-title" id="indirizzoFormTitolo">Nuovo indirizzo</h3>
                <form id="formIndirizzo" class="account-form" method="post"
                      action="${pageContext.request.contextPath}/aggiungi-indirizzo" novalidate>
                    <input type="hidden" name="idIndirizzo" id="indirizzoId">
                    <input type="hidden" name="from" id="indirizzoFrom" value="">

                    <div class="form-group full">
                        <label for="destinatario">Destinatario</label>
                        <input type="text" id="destinatario" name="destinatario"
                               placeholder="Nome e cognome del destinatario" required>
                    </div>
                    <div class="form-group full">
                        <label for="via">Via / Indirizzo</label>
                        <input type="text" id="via" name="via"
                               placeholder="Es. Via Roma 12" required>
                    </div>
                    <div class="form-group">
                        <label for="cap">CAP</label>
                        <input type="text" id="cap" name="cap"
                               placeholder="Es. 20100" maxlength="10" required>
                    </div>
                    <div class="form-group">
                        <label for="citta">Citt&agrave;</label>
                        <input type="text" id="citta" name="citta"
                               placeholder="Es. Milano" required>
                    </div>
                    <div class="form-group">
                        <label for="provincia">Provincia</label>
                        <input type="text" id="provincia" name="provincia"
                               placeholder="Es. MI" maxlength="5" list="list-province" required>
                        <datalist id="list-province"></datalist>
                    </div>
                    <div class="form-group">
                        <label for="paese">Paese</label>
                        <input type="text" id="paese" name="paese"
                               placeholder="Es. Italia" list="list-nazioni" required>
                        <datalist id="list-nazioni"></datalist>
                    </div>
                    <div class="account-actions">
                        <button type="submit" class="btn-primary" id="btnSalvaIndirizzo">Salva indirizzo</button>
                        <button type="button" class="btn-secondary" id="btnAnnullaEdit"
                                onclick="chiudiEditIndirizzo()" hidden>Annulla
                        </button>
                    </div>
                </form>
            </div>
        </div>


        <form class="checkout-form" method="post" action="${pageContext.request.contextPath}/checkout">

            <input type="hidden" name="destinatario" id="ck-destinatario"
                   value="${fn:escapeXml(indirizzoPrecompilato.destinatario)}">
            <input type="hidden" name="via" id="ck-via"
                   value="${fn:escapeXml(indirizzoPrecompilato.via)}">
            <input type="hidden" name="cap" id="ck-cap"
                   value="${fn:escapeXml(indirizzoPrecompilato.cap)}">
            <input type="hidden" name="citta" id="ck-citta"
                   value="${fn:escapeXml(indirizzoPrecompilato.citta)}">
            <input type="hidden" name="provincia" id="ck-provincia"
                   value="${fn:escapeXml(indirizzoPrecompilato.provincia)}">
            <input type="hidden" name="paese" id="ck-paese"
                   value="${fn:escapeXml(indirizzoPrecompilato.paese)}">


            <div class="checkout-card page-card">
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
<script src="${pageContext.request.contextPath}/js/autocomplete.js"></script>
<script src="${pageContext.request.contextPath}/js/checkout.js"></script>

</body>
</html>
