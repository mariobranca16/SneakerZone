<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista desideri - SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wishlist.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main>
    <div class="wishlist-page">
        <c:choose>
            <c:when test="${empty prodotti}">
                <div class="wishlist-card page-card">
                    <h1 class="wishlist-title page-title">I tuoi prodotti salvati</h1>
                    <div class="wishlist-empty-state empty-state">
                        <div class="wishlist-empty-icon empty-icon">
                            <i class="ti ti-heart"></i>
                        </div>
                        <p class="wishlist-empty-kicker empty-kicker">Nessun prodotto salvato</p>
                        <p class="wishlist-empty-text">Non hai ancora aggiunto prodotti alla tua wishlist. Esplora il
                            catalogo per trovare le scarpe che ami.</p>
                        <div class="wishlist-empty-actions">
                            <a class="btn-primary" href="${pageContext.request.contextPath}/catalogo">Vai al
                                catalogo</a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="wishlist-card page-card">
                    <h1 class="wishlist-title page-title">I tuoi prodotti salvati</h1>
                    <p class="wishlist-subtitle page-subtitle">Gestisci la tua lista dei desideri</p>
                    <c:if test="${param.erroreCarrello == '1'}">
                        <div class="alert alert-error">Non puoi aggiungere piu prodotti di quanti siano disponibili.
                        </div>
                    </c:if>
                    <c:if test="${param.successo == '1'}">
                        <div class="alert alert-success">Prodotto aggiunto al carrello con successo.</div>
                    </c:if>
                    <div class="wishlist-grid">
                        <c:forEach var="p" items="${prodotti}">
                            <div class="wishlist-item">
                                <div class="wishlist-item-top">
                                    <div class="wishlist-item-media">
                                        <c:if test="${not empty p.imgPath}">
                                            <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}">
                                                <img class="wishlist-item-thumb product-thumb"
                                                     src="${pageContext.request.contextPath}${p.imgPath}"
                                                     alt="${p.nome}">
                                            </a>
                                        </c:if>
                                        <div class="wishlist-item-text">
                                            <a class="wishlist-item-nome"
                                               href="${pageContext.request.contextPath}/prodotto?id=${p.id}">
                                                <c:out value="${p.nome}"/>
                                            </a>
                                            <span class="wishlist-item-brand"><c:out value="${p.brand}"/></span>
                                        </div>
                                    </div>
                                    <span class="wishlist-item-prezzo"><fmt:formatNumber value="${p.costo}"
                                                                                         type="number"
                                                                                         minFractionDigits="2"
                                                                                         maxFractionDigits="2"/><span
                                            class="currency">&euro;</span></span>
                                </div>
                                <div class="wishlist-item-actions">
                                    <c:set var="taglieDisponibili" value="${disponibilitaPerProdotto[p.id]}"/>
                                    <c:choose>
                                        <c:when test="${not empty taglieDisponibili}">
                                            <form class="wishlist-cart-form" method="post"
                                                  action="${pageContext.request.contextPath}/carrello">
                                                <input type="hidden" name="id" value="${p.id}"/>
                                                <input type="hidden" name="origine" value="wishlist"/>
                                                <div class="wishlist-cart-fields">
                                                    <div class="wishlist-field">
                                                        <label for="taglia-${p.id}">Taglia</label>
                                                        <select id="taglia-${p.id}" name="taglia" required>
                                                            <c:forEach var="taglia" items="${taglieDisponibili}">
                                                                <option value="${taglia.taglia}">${taglia.taglia}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <div class="wishlist-field">
                                                        <label for="qta-${p.id}">Qta</label>
                                                        <input id="qta-${p.id}" type="number" name="quantita" min="1"
                                                               value="1" required/>
                                                    </div>
                                                </div>
                                                <button class="btn-primary" type="submit">
                                                    <i class="ti ti-shopping-cart-plus"></i> Aggiungi al carrello
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="wishlist-unavailable">Non disponibile al momento</p>
                                        </c:otherwise>
                                    </c:choose>
                                    <form class="wishlist-remove-form" method="post"
                                          action="${pageContext.request.contextPath}/remove-from-wishlist">
                                        <input type="hidden" name="idProdotto" value="${p.id}">
                                        <button class="btn-danger" type="submit"
                                                data-confirm="Vuoi rimuovere questo prodotto dalla wishlist?">
                                            Rimuovi
                                        </button>
                                    </form>
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
