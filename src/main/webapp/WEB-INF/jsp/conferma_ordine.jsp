<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ordine Confermato – SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/conferma_ordine.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="confirm-page">

        <div class="confirm-banner">
            <i class="ti ti-circle-check confirm-banner-icon" aria-hidden="true"></i>
            <div>
                <h1 class="confirm-title">Ordine confermato!</h1>
                <p class="confirm-subtitle">
                    Grazie per il tuo acquisto. L&rsquo;ordine <strong>#${ordine.id}</strong>
                    &egrave; stato ricevuto e verr&agrave; elaborato a breve.
                </p>
            </div>
        </div>

        <div class="confirm-card">

            <div class="confirm-info">
                <div class="confirm-info-row">
                    <span class="confirm-info-label">Numero ordine</span>
                    <span class="confirm-info-value">#${ordine.id}</span>
                </div>
                <div class="confirm-info-row">
                    <span class="confirm-info-label">Data</span>
                    <span class="confirm-info-value">${dataOrdine}</span>
                </div>
                <div class="confirm-info-row">
                    <span class="confirm-info-label">Stato</span>
                    <span class="confirm-status-badge">${ordine.stato.label}</span>
                </div>
                <div class="confirm-info-row">
                    <span class="confirm-info-label">Articoli</span>
                    <span class="confirm-info-value">${ordine.numeroArticoli}</span>
                </div>
                <c:if test="${not empty ordine.indirizzo}">
                    <div class="confirm-info-row">
                        <span class="confirm-info-label">Destinatario</span>
                        <span class="confirm-info-value">${ordine.indirizzo.destinatario}</span>
                    </div>
                    <div class="confirm-info-row">
                        <span class="confirm-info-label">Indirizzo di spedizione</span>
                        <span class="confirm-info-value">
                            ${ordine.indirizzo.via}, ${ordine.indirizzo.cap} ${ordine.indirizzo.citta}
                            (${ordine.indirizzo.provincia})
                            <c:if test="${not empty ordine.indirizzo.paese}">&nbsp;&ndash;&nbsp;${ordine.indirizzo.paese}</c:if>
                        </span>
                    </div>
                </c:if>
            </div>

            <div class="confirm-section-title">Prodotti ordinati</div>

            <div class="confirm-items">
                <c:forEach var="d" items="${ordine.dettagliOrdine}">
                    <div class="confirm-item">
                        <c:if test="${not empty d.prodotto and not empty d.prodotto.imgPath}">
                            <img class="confirm-item-thumb"
                                 src="${pageContext.request.contextPath}${d.prodotto.imgPath}"
                                 alt="${d.prodotto.nome}">
                        </c:if>
                        <div class="confirm-item-info">
                            <span class="confirm-item-nome"><c:out value="${d.prodotto.nome}"/></span>
                            <span class="confirm-item-meta">
                                Taglia&nbsp;${d.taglia}&nbsp;&middot;&nbsp;Qt&agrave;&nbsp;${d.quantita}
                                <c:if test="${not empty d.prodotto.brand}">&nbsp;&middot;&nbsp;<c:out
                                    value="${d.prodotto.brand}"/></c:if>
                            </span>
                        </div>
                        <span class="confirm-item-prezzo">
                            <fmt:formatNumber value="${d.subtotale}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                        </span>
                    </div>
                </c:forEach>
            </div>

            <div class="confirm-total">
                <span class="confirm-total-label">Totale</span>
                <span class="confirm-total-value">
                    <fmt:formatNumber value="${ordine.totaleOrdine}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                </span>
            </div>

            <div class="confirm-actions">
                <a class="confirm-btn-primary" href="${pageContext.request.contextPath}/ordini">
                    I miei ordini
                </a>
                <a class="confirm-btn-secondary" href="${pageContext.request.contextPath}/catalogo">
                    Continua lo shopping
                </a>
            </div>

        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

</body>
</html>
