<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Storico ordini. ${ordine.statoCssClass} controlla il colore del badge stato.
     Il pulsante "Annulla" è visibile solo se ${ordine.inElaborazione} è true. --%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>I tuoi ordini – SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ordini.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main>
    <div class="ordini-page">
        <!-- stato vuoto o elenco ordini a seconda del contenuto di ordini -->
        <c:choose>
            <c:when test="${empty ordini}">
                <div class="ordini-card page-card">
                    <h1 class="ordini-title page-title">I tuoi ordini</h1>
                    <div class="ordini-empty-state empty-state">
                        <div class="ordini-empty-icon empty-icon">
                            <i class="ti ti-package"></i>
                        </div>
                        <p class="ordini-empty-kicker empty-kicker">Nessun ordine</p>
                        <p class="ordini-empty-text">Non hai ancora effettuato ordini. Esplora il catalogo per trovare
                            le scarpe perfette per te.</p>
                        <div class="ordini-empty-actions">
                            <a class="ordini-cta" href="${pageContext.request.contextPath}/catalogo">
                                Vai al catalogo
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="ordini-card page-card">
                    <h1 class="ordini-title page-title">I tuoi ordini</h1>
                    <p class="ordini-subtitle page-subtitle">Consulta lo stato e i dettagli dei tuoi acquisti</p>
                    <!-- elenco ordini -->
                    <div class="ordini-list">
                        <c:forEach var="ordine" items="${ordini}">
                            <div class="ordine-card ${ordine.statoCssClass}">
                                <div class="ordine-header">
                                    <div class="ordine-header-left">
                                        <span class="ordine-num">Ordine <strong>#${ordine.id}</strong></span>
                                        <div class="ordine-meta">
                                        <span class="ordine-data">
                                            <i class="ti ti-calendar"
                                               aria-hidden="true"></i> ${ordine.dataOrdineFormattata}
                                        </span>
                                            <span class="ordine-sep" aria-hidden="true">·</span>
                                            <span class="ordine-articoli-count">
                                            ${ordine.numeroArticoli}
                                            <c:choose>
                                                <c:when test="${ordine.numeroArticoli == 1}">articolo</c:when>
                                                <c:otherwise>articoli</c:otherwise>
                                            </c:choose>
                                        </span>
                                        </div>
                                    </div>
                                    <div class="ordine-header-right">
                                        <!-- badge stato: varia tra consegnato, spedito, in elaborazione, annullato -->
                                        <c:choose>
                                            <c:when test="${ordine.consegnato}">
                                            <span class="ordine-stato stato-consegnato">
                                                <i class="ti ti-circle-check" aria-hidden="true"></i> Consegnato
                                            </span>
                                            </c:when>
                                            <c:when test="${ordine.spedito and not ordine.consegnato}">
                                            <span class="ordine-stato stato-spedito">
                                                <i class="ti ti-truck" aria-hidden="true"></i> Spedito
                                            </span>
                                            </c:when>
                                            <c:when test="${ordine.inElaborazione}">
                                            <span class="ordine-stato stato-elaborazione">
                                                <i class="ti ti-clock" aria-hidden="true"></i> In elaborazione
                                            </span>
                                            </c:when>
                                            <c:when test="${ordine.annullato}">
                                            <span class="ordine-stato stato-annullato">
                                                <i class="ti ti-circle-off" aria-hidden="true"></i> Annullato
                                            </span>
                                            </c:when>
                                        </c:choose>
                                        <!-- pulsante annulla: visibile solo se l'ordine è ancora in elaborazione -->
                                        <c:if test="${ordine.inElaborazione}">
                                            <form class="ordine-annulla-form" method="post"
                                                  action="${pageContext.request.contextPath}/modifica-ordine">
                                                <input type="hidden" name="action" value="annulla"/>
                                                <input type="hidden" name="idOrdine" value="${ordine.id}"/>
                                                <button class="btn-danger" type="submit"
                                                        data-confirm="Sei sicuro di voler annullare questo ordine? L'operazione non è reversibile.">
                                                    <i class="ti ti-x"></i> Annulla
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                                <!-- indirizzo: può mancare se eliminato dopo la creazione dell'ordine -->
                                <c:if test="${not empty ordine.indirizzo}">
                                    <div class="ordine-indirizzo">
                                        <i class="ti ti-map-pin" aria-hidden="true"></i>
                                            ${ordine.indirizzo.via}, ${ordine.indirizzo.citta},
                                            ${ordine.indirizzo.cap} (${ordine.indirizzo.provincia})
                                    </div>
                                </c:if>
                                <div class="ordine-items">
                                    <c:forEach var="item" items="${ordine.dettagliOrdine}">
                                        <a class="ordine-item"
                                           href="${pageContext.request.contextPath}/prodotto?id=${item.prodotto.id}">
                                            <c:choose>
                                                <c:when test="${not empty item.prodotto and not empty item.prodotto.imgPath}">
                                                    <img class="ordine-item-thumb"
                                                         src="${pageContext.request.contextPath}${item.prodotto.imgPath}"
                                                         alt="${item.prodotto.nome}">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="ordine-item-thumb-placeholder">
                                                        <i class="ti ti-shoe" aria-hidden="true"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="ordine-item-info">
                                                <span class="ordine-prodotto-nome"><c:out
                                                        value="${item.prodotto.nome}"/></span>
                                                <span class="ordine-item-meta">
                                                    Taglia&nbsp;${item.taglia}&nbsp;&middot;&nbsp;Qt&agrave;&nbsp;${item.quantita}
                                                    <c:if test="${not empty item.prodotto.brand}">&nbsp;&middot;&nbsp;<c:out
                                                            value="${item.prodotto.brand}"/></c:if>
                                                </span>
                                            </div>
                                            <span class="ordine-item-subtotale">
                                                <fmt:formatNumber value="${item.subtotale}" type="number"
                                                                  minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                            </span>
                                        </a>
                                    </c:forEach>
                                </div>
                                <div class="ordine-footer">
                                    <span class="ordine-totale-label">Totale ordine</span>
                                    <span class="ordine-totale-amount">
                                    <fmt:formatNumber value="${ordine.totaleOrdine}" type="number"
                                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
</body>
</html>
