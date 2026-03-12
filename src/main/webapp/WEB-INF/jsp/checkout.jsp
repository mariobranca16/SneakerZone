<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
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
            <div class="alert alert-error" style="margin:1rem auto;max-width:900px;">
                <i class="ti ti-alert-circle"></i>&nbsp;<c:out value="${erroreStock}"/>
            </div>
        </c:if>


        <div class="checkout-steps">
            <div class="step step-done">
                <span class="step-num"><i class="ti ti-check"></i></span>
                <span class="step-label">Carrello</span>
            </div>
            <div class="step-line step-line-done"></div>
            <div class="step step-active">
                <span class="step-num">2</span>
                <span class="step-label">Checkout</span>
            </div>
            <div class="step-line"></div>
            <div class="step">
                <span class="step-num">3</span>
                <span class="step-label">Conferma</span>
            </div>
        </div>

        <h1 class="checkout-title">Checkout</h1>


        <div class="checkout-card">
            <h2 class="checkout-section-title">
                <i class="ti ti-shopping-bag"></i> Riepilogo ordine
            </h2>

            <div class="checkout-items">
                <c:set var="totale" value="0"/>
                <c:forEach var="item" items="${carrello.prodotti}">
                    <c:set var="totale" value="${totale + (item.prodotto.costo * item.quantita)}"/>
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
                            <fmt:formatNumber value="${item.prodotto.costo * item.quantita}"
                                              type="number" minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                        </span>
                    </div>
                </c:forEach>
            </div>

            <div class="checkout-total-row">
                <span class="checkout-total-label">Totale</span>
                <span class="checkout-total-value">
                    <fmt:formatNumber value="${totale}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                </span>
            </div>
        </div>


        <form class="checkout-form" method="post" action="${pageContext.request.contextPath}/checkout">


            <div class="checkout-card">
                <h2 class="checkout-section-title">
                    <i class="ti ti-map-pin"></i> Indirizzo di spedizione
                </h2>

                <c:choose>
                    <c:when test="${fn:length(indirizzi) == 0}">
                        <div class="checkout-no-address">
                            <i class="ti ti-alert-triangle"></i>
                            Nessun indirizzo salvato. Aggiungine uno prima di confermare l&rsquo;ordine.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="checkout-address-list">
                            <c:forEach var="indirizzo" items="${indirizzi}">
                                <label class="address-card">
                                    <input class="address-radio" type="radio"
                                           name="idIndirizzo" value="${indirizzo.id}"
                                        ${param.idIndirizzo == indirizzo.id ? 'checked' : ''} required/>
                                    <div class="address-card-body">
                                        <span class="address-destinatario">${indirizzo.destinatario}</span>
                                        <span class="address-detail">${indirizzo.via}</span>
                                        <span class="address-detail">
                                            ${indirizzo.cap} ${indirizzo.citta}
                                            (${indirizzo.provincia}), ${indirizzo.paese}
                                        </span>
                                    </div>
                                    <span class="address-check-icon">
                                        <i class="ti ti-circle-check"></i>
                                    </span>
                                </label>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <a class="checkout-add-address" href="${pageContext.request.contextPath}/aggiungi-indirizzo">
                    <i class="ti ti-plus"></i> Aggiungi un nuovo indirizzo
                </a>
            </div>


            <div class="checkout-card">
                <h2 class="checkout-section-title">
                    <i class="ti ti-credit-card"></i> Dati di pagamento
                </h2>

                <c:choose>

                    <c:when test="${not empty metodoPagamento}">

                        <div class="pay-choice-list">

                            <label class="pay-choice-card">
                                <input class="pay-choice-radio" type="radio"
                                       name="usaCarta" value="salvata" checked>
                                <i class="ti ti-credit-card pay-choice-icon"></i>
                                <div class="pay-choice-info">
                                    <span class="pay-choice-number">${metodoPagamento.numeroMascherato}</span>
                                    <span class="pay-choice-meta">
                                        ${metodoPagamento.nomeCarta}&nbsp;&middot;&nbsp;Scad.&nbsp;${metodoPagamento.scadenza}
                                    </span>
                                </div>
                                <span class="pay-choice-check">
                                    <i class="ti ti-circle-check"></i>
                                </span>
                            </label>


                            <label class="pay-choice-card">
                                <input class="pay-choice-radio" type="radio"
                                       name="usaCarta" value="nuova">
                                <i class="ti ti-plus pay-choice-icon"></i>
                                <div class="pay-choice-info">
                                    <span class="pay-choice-number">Inserisci una carta diversa</span>
                                </div>
                                <span class="pay-choice-check">
                                    <i class="ti ti-circle-check"></i>
                                </span>
                            </label>
                        </div>


                        <div id="nuovaCartaForm" class="pay-grid pay-grid-hidden" data-card-error="${(not empty erroreNomeCarta) || (not empty erroreNumeroCarta) || (not empty erroreScadenza)}">
                            <div class="pay-group pay-full">
                                <label class="pay-label" for="nomeCarta">Nome sulla carta</label>
                                <input class="pay-input ${not empty erroreNomeCarta ? 'pay-input-error' : ''}"
                                       type="text" id="nomeCarta" name="nomeCarta"
                                       placeholder="Es. Mario Rossi"
                                       value="${fn:escapeXml(param.nomeCarta)}"
                                       autocomplete="cc-name">
                                <c:if test="${not empty erroreNomeCarta}">
                                    <span class="pay-error">${erroreNomeCarta}</span>
                                </c:if>
                            </div>
                            <div class="pay-group pay-full">
                                <label class="pay-label" for="numeroCarta">Numero carta</label>
                                <input class="pay-input ${not empty erroreNumeroCarta ? 'pay-input-error' : ''}"
                                       type="text" id="numeroCarta" name="numeroCarta" class="js-card-number"
                                       placeholder="1234 5678 9012 3456" maxlength="19"
                                       value="${fn:escapeXml(param.numeroCarta)}"
                                       autocomplete="cc-number">
                                <c:if test="${not empty erroreNumeroCarta}">
                                    <span class="pay-error">${erroreNumeroCarta}</span>
                                </c:if>
                            </div>
                            <div class="pay-group pay-full">
                                <label class="pay-label" for="scadenza">Scadenza</label>
                                <input class="pay-input ${not empty erroreScadenza ? 'pay-input-error' : ''}"
                                       type="text" id="scadenza" name="scadenza" class="js-card-expiry"
                                       placeholder="MM/AA" maxlength="5"
                                       value="${fn:escapeXml(param.scadenza)}"
                                       autocomplete="cc-exp">
                                <c:if test="${not empty erroreScadenza}">
                                    <span class="pay-error">${erroreScadenza}</span>
                                </c:if>
                            </div>
                        </div>


                        <div class="pay-group pay-full pay-cvv-row">
                            <label class="pay-label" for="cvv">CVV</label>
                            <input class="pay-input ${not empty erroreCvv ? 'pay-input-error' : ''}"
                                   type="password" id="cvv" name="cvv"
                                   placeholder="&bull;&bull;&bull;" maxlength="4"
                                   autocomplete="cc-csc" required>
                            <c:if test="${not empty erroreCvv}">
                                <span class="pay-error">${erroreCvv}</span>
                            </c:if>
                        </div>

                    </c:when>


                    <c:otherwise>
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
                                <input class="pay-input ${not empty erroreNumeroCarta ? 'pay-input-error' : ''}"
                                       type="text" id="numeroCarta" name="numeroCarta" class="js-card-number"
                                       placeholder="1234 5678 9012 3456" maxlength="19"
                                       value="${fn:escapeXml(param.numeroCarta)}"
                                       autocomplete="cc-number" required>
                                <c:if test="${not empty erroreNumeroCarta}">
                                    <span class="pay-error">${erroreNumeroCarta}</span>
                                </c:if>
                            </div>
                            <div class="pay-group">
                                <label class="pay-label" for="scadenza">Scadenza</label>
                                <input class="pay-input ${not empty erroreScadenza ? 'pay-input-error' : ''}"
                                       type="text" id="scadenza" name="scadenza" class="js-card-expiry"
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
                    </c:otherwise>
                </c:choose>

            </div>


            <c:if test="${fn:length(indirizzi) > 0}">
                <button class="btn-confirm" type="submit">
                    <i class="ti ti-lock"></i> Conferma ordine
                </button>
            </c:if>

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
