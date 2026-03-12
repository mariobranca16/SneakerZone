<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I tuoi ordini – SneakerZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ordini.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp" />

<main>
    <div class="ordini-page">
        <h1 class="ordini-title">I tuoi ordini</h1>
        <p class="ordini-subtitle">Consulta lo stato e i dettagli dei tuoi acquisti</p>

        <c:choose>
            <c:when test="${empty ordini}">
                <div class="ordini-empty-state">
                    <div class="ordini-empty-icon">
                        <i class="fa-solid fa-box-open"></i>
                    </div>
                    <p class="ordini-empty-kicker">Nessun ordine</p>
                    <p class="ordini-empty-text">Non hai ancora effettuato ordini. Esplora il catalogo per trovare le scarpe perfette per te.</p>
                    <div class="ordini-empty-actions">
                        <a class="ordini-cta" href="${pageContext.request.contextPath}/catalogo">
                            <i class="fas fa-store"></i> Vai al catalogo
                        </a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <div class="ordini-list">
                    <c:forEach var="ordine" items="${ordini}">

                        <%-- Variabili di stato per la timeline --%>
                        <c:set var="statoName"    value="${ordine.stato != null ? ordine.stato.name() : ''}"/>
                        <c:set var="isAnnullato"  value="${statoName == 'ANNULLATO'}"/>
                        <c:set var="isSpedito"    value="${statoName == 'SPEDITO' or statoName == 'CONSEGNATO'}"/>
                        <c:set var="isConsegnato" value="${statoName == 'CONSEGNATO'}"/>

                        <div class="ordine-card
                            ${statoName == 'CONSEGNATO'      ? 'ordine-consegnato'   : ''}
                            ${statoName == 'SPEDITO'         ? 'ordine-spedito'      : ''}
                            ${statoName == 'IN_ELABORAZIONE' ? 'ordine-elaborazione' : ''}
                            ${statoName == 'ANNULLATO'       ? 'ordine-annullato'    : ''}">

                            <%-- Header --%>
                            <div class="ordine-header">
                                <div class="ordine-header-left">
                                    <span class="ordine-id">
                                        <i class="fas fa-hashtag" aria-hidden="true"></i>${ordine.id}
                                    </span>
                                    <span class="ordine-data">
                                        <i class="fa-regular fa-calendar" aria-hidden="true"></i> ${ordine.dataOrdineFormattata}
                                    </span>
                                    <span class="ordine-articoli-count">
                                        <i class="fas fa-box" aria-hidden="true"></i>
                                        ${fn:length(ordine.dettagliOrdine)}
                                        <c:choose>
                                            <c:when test="${fn:length(ordine.dettagliOrdine) == 1}">articolo</c:when>
                                            <c:otherwise>articoli</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="ordine-header-right">
                                    <c:choose>
                                        <c:when test="${statoName == 'CONSEGNATO'}">
                                            <span class="ordine-stato stato-consegnato">
                                                <i class="fa-solid fa-circle-check" aria-hidden="true"></i> Consegnato
                                            </span>
                                        </c:when>
                                        <c:when test="${statoName == 'SPEDITO'}">
                                            <span class="ordine-stato stato-spedito">
                                                <i class="fa-solid fa-truck" aria-hidden="true"></i> Spedito
                                            </span>
                                        </c:when>
                                        <c:when test="${statoName == 'IN_ELABORAZIONE'}">
                                            <span class="ordine-stato stato-elaborazione">
                                                <i class="fa-solid fa-clock" aria-hidden="true"></i> In elaborazione
                                            </span>
                                        </c:when>
                                        <c:when test="${statoName == 'ANNULLATO'}">
                                            <span class="ordine-stato stato-annullato">
                                                <i class="fa-solid fa-ban" aria-hidden="true"></i> Annullato
                                            </span>
                                        </c:when>
                                    </c:choose>

                                    <c:if test="${statoName == 'IN_ELABORAZIONE'}">
                                        <form class="ordine-annulla-form" method="post" action="${pageContext.request.contextPath}/modifica-ordine">
                                            <input type="hidden" name="action" value="annulla"/>
                                            <input type="hidden" name="idOrdine" value="${ordine.id}"/>
                                            <button class="btn-danger" type="submit"
                                                    data-confirm="Sei sicuro di voler annullare questo ordine? L'operazione non è reversibile.">
                                                <i class="fa-solid fa-xmark"></i> Annulla
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>

                            <%-- Timeline avanzamento --%>
                            <c:choose>
                                <c:when test="${isAnnullato}">
                                    <div class="ordine-timeline ordine-timeline--annullato">
                                        <i class="fas fa-ban" aria-hidden="true"></i> Ordine annullato
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="ordine-timeline">
                                        <div class="tl-step tl-step--done">
                                            <span class="tl-dot"><i class="fas fa-clock" aria-hidden="true"></i></span>
                                            <span class="tl-label">Elaborazione</span>
                                        </div>
                                        <div class="tl-line ${isSpedito ? 'tl-line--done' : ''}"></div>
                                        <div class="tl-step ${isSpedito ? 'tl-step--done' : ''}">
                                            <span class="tl-dot"><i class="fas fa-truck" aria-hidden="true"></i></span>
                                            <span class="tl-label">Spedito</span>
                                        </div>
                                        <div class="tl-line ${isConsegnato ? 'tl-line--done' : ''}"></div>
                                        <div class="tl-step ${isConsegnato ? 'tl-step--done' : ''}">
                                            <span class="tl-dot"><i class="fas fa-circle-check" aria-hidden="true"></i></span>
                                            <span class="tl-label">Consegnato</span>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <%-- Indirizzo --%>
                            <c:if test="${not empty ordine.indirizzo}">
                                <div class="ordine-indirizzo">
                                    <i class="fa-solid fa-location-dot" aria-hidden="true"></i>
                                    ${ordine.indirizzo.via}, ${ordine.indirizzo.citta},
                                    ${ordine.indirizzo.cap} (${ordine.indirizzo.provincia})
                                </div>
                            </c:if>

                            <%-- Tabella prodotti --%>
                            <div class="ordine-table-wrap">
                                <table class="ordine-table">
                                    <thead>
                                        <tr>
                                            <th>Prodotto</th>
                                            <th>Taglia</th>
                                            <th>Qt&agrave;</th>
                                            <th>Prezzo</th>
                                            <th>Subtotale</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${ordine.dettagliOrdine}">
                                            <tr>
                                                <td>
                                                    <div class="ordine-item-cell">
                                                        <c:if test="${not empty item.prodotto and not empty item.prodotto.imgPath}">
                                                            <img class="ordine-item-thumb"
                                                                 src="${pageContext.request.contextPath}${item.prodotto.imgPath}"
                                                                 alt="${item.prodotto.nome}">
                                                        </c:if>
                                                        <span class="ordine-prodotto-nome"><c:out value="${item.prodotto.nome}"/></span>
                                                    </div>
                                                </td>
                                                <td>${item.taglia}</td>
                                                <td>${item.quantita}</td>
                                                <td class="ordine-prezzo">
                                                    <fmt:formatNumber value="${item.costo}" type="number"
                                                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                                </td>
                                                <td class="ordine-prezzo">
                                                    <fmt:formatNumber value="${item.quantita * item.costo}" type="number"
                                                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <%-- Footer --%>
                            <div class="ordine-footer">
                                <span class="ordine-totale">
                                    Totale ordine&nbsp;
                                    <strong>
                                        <fmt:formatNumber value="${ordine.calcolaTotaleOrdine()}" type="number"
                                                          minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                    </strong>
                                </span>
                            </div>

                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />

</body>
</html>
