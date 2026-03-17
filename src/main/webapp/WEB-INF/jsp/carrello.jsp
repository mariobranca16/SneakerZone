<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrello</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrello.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="cart-page">
        <div class="cart-card">
            <h1 class="cart-title">Il tuo carrello</h1>

            <c:if test="${not empty erroreCarrello}">
                <div class="alert alert-error">${erroreCarrello}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty carrello.prodotti}">
                    <div class="cart-empty-state">
                        <div class="cart-empty-icon" aria-hidden="true">
                            <i class="ti ti-shopping-bag"></i>
                        </div>
                        <p class="cart-empty-kicker">Nessun articolo</p>
                        <p class="cart-empty">Il carrello &egrave; vuoto. Scopri le ultime sneaker e aggiungi i tuoi
                            preferiti.</p>
                        <div class="cart-empty-actions">
                            <a class="cart-cta cart-cta-primary" href="${pageContext.request.contextPath}/catalogo">Vai
                                al catalogo</a>
                            <a class="cart-cta cart-cta-secondary" href="${pageContext.request.contextPath}/home">Torna
                                alla home</a>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="cart-table-wrap">
                        <table class="cart-table">
                            <thead>
                            <tr>
                                <th>Prodotto</th>
                                <th>Taglia</th>
                                <th>Quantit&agrave;</th>
                                <th>Subtotale</th>
                                <th>Azione</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="item" items="${carrello.prodotti}">
                                <tr>
                                    <td>
                                        <div class="cart-item-info">
                                            <c:if test="${not empty item.prodotto.imgPath}">
                                                <a href="${pageContext.request.contextPath}/prodotto?id=${item.prodotto.id}"
                                                   aria-hidden="true" tabindex="-1">
                                                    <img class="cart-item-thumb"
                                                         src="${pageContext.request.contextPath}${item.prodotto.imgPath}"
                                                         alt="${fn:escapeXml(item.prodotto.nome)}">
                                                </a>
                                            </c:if>
                                            <a class="cart-item-name"
                                               href="${pageContext.request.contextPath}/prodotto?id=${item.prodotto.id}">
                                                    ${item.prodotto.nome}
                                            </a>
                                        </div>
                                    </td>
                                    <td>${item.taglia}</td>
                                    <td>
                                        <form class="qty-form" method="post"
                                              action="${pageContext.request.contextPath}/carrello">
                                            <input type="hidden" name="azione" value="aggiorna"/>
                                            <input type="hidden" name="id" value="${item.prodotto.id}"/>
                                            <input type="hidden" name="taglia" value="${item.taglia}"/>
                                            <input class="qty-input" type="number" name="quantita"
                                                   value="${item.quantita}" min="1" max="99"
                                                   aria-label="Quantità di ${item.prodotto.nome}"/>
                                        </form>
                                    </td>
                                    <td class="cart-price"><fmt:formatNumber
                                            value="${item.subtotale}" minFractionDigits="2"
                                            maxFractionDigits="2"/> &euro;
                                    </td>
                                    <td class="cart-action-cell">
                                        <form class="inline-form" method="post"
                                              action="${pageContext.request.contextPath}/carrello">
                                            <input type="hidden" name="azione" value="rimuovi"/>
                                            <input type="hidden" name="id" value="${item.prodotto.id}"/>
                                            <input type="hidden" name="taglia" value="${item.taglia}"/>
                                            <button class="btn-danger" type="submit"
                                                    data-confirm="Vuoi rimuovere questo articolo dal carrello?">Rimuovi
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="cart-totale">
                        <span class="cart-totale-label">Totale:</span>
                        <span class="cart-totale-valore">
                            <span id="cart-totale-valore"><fmt:formatNumber value="${carrello.totale}"
                                                                            minFractionDigits="2"
                                                                            maxFractionDigits="2"/></span>&nbsp;&euro;
                        </span>
                    </div>

                    <form class="cart-checkout-form" method="get" action="${pageContext.request.contextPath}/checkout">
                        <button class="btn-primary" type="submit">Procedi all'ordine</button>
                    </form>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty carrello.prodotti}">
                <a class="cart-link cart-link-home" href="${pageContext.request.contextPath}/home">Torna alla home</a>
            </c:if>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/carrello.js"></script>
</body>
</html>
