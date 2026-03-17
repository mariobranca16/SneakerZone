<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-prodotto.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>

<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Ordine #${ordine.id}</h1>
        <p class="admin-page-subtitle">${ordine.dataOrdineFormattata}</p>
    </div>
    <div class="admin-page-actions">
        <a class="btn btn--small" href="${pageContext.request.contextPath}/admin/ordini">
            <i class="ti ti-arrow-left"></i> Torna agli ordini
        </a>
    </div>
</div>

<div class="admin-card">


    <div class="admin-grid-3">

        <div class="order-info-col">
            <div class="order-info-label">Utente</div>
            <div class="order-info-value">${emailUtente}</div>
        </div>

        <div class="order-info-col">
            <div class="order-info-label">Indirizzo di spedizione</div>
            <c:choose>
                <c:when test="${ordine.indirizzo == null}">
                    <div class="order-info-value">—</div>
                </c:when>
                <c:otherwise>
                    <div class="order-info-value">
                            ${ordine.indirizzo.destinatario}<br>
                            ${ordine.indirizzo.via}<br>
                            ${ordine.indirizzo.citta} (${ordine.indirizzo.provincia}) ${ordine.indirizzo.cap}<br>
                            ${ordine.indirizzo.paese}
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="order-info-col">
            <form method="post" action="${pageContext.request.contextPath}/admin/ordini">
                <input type="hidden" name="id" value="${ordine.id}">
                <label class="label">Stato ordine</label>
                <select name="stato" class="select" style="width:auto; min-width:160px;">
                    <c:forEach var="s" items="${stati}">
                        <option value="${s}" <c:if test="${ordine.stato == s}">selected</c:if>>${s.label}</option>
                    </c:forEach>
                </select>
                <button class="btn btn--small btn--primary" type="submit" style="margin-top:18px;">
                    Aggiorna
                </button>
            </form>
        </div>

    </div>


    <c:if test="${not empty ordine.dettagliOrdine}">
        <div class="order-items">
            <c:forEach var="d" items="${ordine.dettagliOrdine}">
                <div class="order-item">
                    <c:choose>
                        <c:when test="${d.prodotto != null && not empty d.prodotto.imgPath}">
                            <img class="order-item-thumb"
                                 src="${pageContext.request.contextPath}${d.prodotto.imgPath}"
                                 alt="${d.prodotto.nome}">
                        </c:when>
                        <c:otherwise>
                            <div class="order-item-thumb-empty"><i class="ti ti-tag"></i></div>
                        </c:otherwise>
                    </c:choose>

                    <div class="order-item-info">
                        <div class="order-item-nome">
                            <c:choose>
                                <c:when test="${d.prodotto != null}">${d.prodotto.nome}</c:when>
                                <c:otherwise>Prodotto #${d.idProdotto}</c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${d.prodotto != null}">
                            <div class="order-item-meta">${d.prodotto.brand}<c:if test="${not empty d.prodotto.colore}">&nbsp;&middot;&nbsp;${d.prodotto.colore}</c:if></div>
                        </c:if>
                        <div class="order-item-pills">
                            <span class="order-item-pill">Taglia ${d.taglia}</span>
                            <span class="order-item-pill">Qty ${d.quantita}</span>
                        </div>
                    </div>

                    <div class="order-item-price">
                        <div class="order-item-sub">&euro;&nbsp;<fmt:formatNumber value="${d.subtotale}" type="number"
                                                                                  minFractionDigits="2"
                                                                                  maxFractionDigits="2"/></div>
                        <c:if test="${d.quantita > 1}">
                            <div class="order-item-unit">&euro;&nbsp;<fmt:formatNumber value="${d.costo}" type="number"
                                                                                       minFractionDigits="2"
                                                                                       maxFractionDigits="2"/> cad.
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>

            <div class="order-total-row">
                <span class="order-total-label">Totale ordine</span>
                <span class="order-total-value">&euro;&nbsp;<fmt:formatNumber value="${ordine.totaleOrdine}"
                                                                              type="number" minFractionDigits="2"
                                                                              maxFractionDigits="2"/></span>
            </div>
        </div>
    </c:if>

</div>

</div>
</main>

<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>

</body>
</html>
