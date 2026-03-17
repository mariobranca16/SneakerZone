<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catalogo Prodotti</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="catalogo-page">
        <h1 class="catalogo-title">Catalogo</h1>

        <div class="catalogo-layout">


            <aside class="catalogo-sidebar">
                <form method="get" action="${pageContext.request.contextPath}/catalogo" id="filtriForm"
                      data-ctx="${pageContext.request.contextPath}">


                    <div class="sidebar-section">
                        <label for="q" class="sidebar-label">Cerca</label>
                        <div class="sidebar-search-wrap">
                            <i class="ti ti-search sidebar-search-icon" aria-hidden="true"></i>
                            <input class="sidebar-input" type="text" id="q" name="q"
                                   placeholder="Nome o brand…"
                                   value="${fn:escapeXml(filtroQ)}">
                        </div>
                    </div>


                    <div class="sidebar-section">
                        <div class="sidebar-label">Categoria</div>
                        <ul class="sidebar-radio-list">
                            <li>
                                <label class="sidebar-radio-item">
                                    <input type="radio" name="categoria" value=""
                                    ${empty filtroCategoria ? 'checked' : ''}>
                                    <span>Tutte</span>
                                </label>
                            </li>
                            <c:forEach var="cat" items="${tutteCategorie}">
                                <li>
                                    <label class="sidebar-radio-item">
                                        <input type="radio" name="categoria" value="${cat.nome}"
                                            ${filtroCategoria == cat.nome ? 'checked' : ''}>
                                        <span>${cat.nome}</span>
                                    </label>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>


                    <div class="sidebar-section">
                        <div class="sidebar-label">Genere</div>
                        <ul class="sidebar-radio-list">
                            <li>
                                <label class="sidebar-radio-item">
                                    <input type="radio" name="genere" value=""
                                    ${empty filtroGenere ? 'checked' : ''}>
                                    <span>Tutti</span>
                                </label>
                            </li>
                            <li>
                                <label class="sidebar-radio-item">
                                    <input type="radio" name="genere" value="Uomo"
                                    ${filtroGenere == 'Uomo' ? 'checked' : ''}>
                                    <span>Uomo</span>
                                </label>
                            </li>
                            <li>
                                <label class="sidebar-radio-item">
                                    <input type="radio" name="genere" value="Donna"
                                    ${filtroGenere == 'Donna' ? 'checked' : ''}>
                                    <span>Donna</span>
                                </label>
                            </li>
                            <li>
                                <label class="sidebar-radio-item">
                                    <input type="radio" name="genere" value="Unisex"
                                    ${filtroGenere == 'Unisex' ? 'checked' : ''}>
                                    <span>Unisex</span>
                                </label>
                            </li>
                        </ul>
                    </div>


                    <div class="sidebar-section">
                        <div class="sidebar-label">Prezzo (&euro;)</div>
                        <div class="sidebar-prezzo-row">
                            <input class="sidebar-input sidebar-input--prezzo" type="number"
                                   name="prezzoMin" min="0" step="1"
                                   aria-label="Prezzo minimo in euro"
                                   placeholder="Min"
                                   value="${fn:escapeXml(filtroPrezzoMin)}">
                            <span class="sidebar-prezzo-sep" aria-hidden="true">&ndash;</span>
                            <input class="sidebar-input sidebar-input--prezzo" type="number"
                                   name="prezzoMax" min="0" step="1"
                                   aria-label="Prezzo massimo in euro"
                                   placeholder="Max"
                                   value="${fn:escapeXml(filtroPrezzoMax)}">
                        </div>
                    </div>


                    <div class="sidebar-actions">
                        <a id="btnAzzera" class="sidebar-btn sidebar-btn--reset"
                           href="${pageContext.request.contextPath}/catalogo">
                            <i class="ti ti-x"></i> Azzera filtri
                        </a>
                    </div>

                </form>
            </aside>


            <div class="catalogo-risultati">

                <c:if test="${not empty messaggio}">
                    <div class="alert alert-success">${messaggio}</div>
                </c:if>
                <c:if test="${not empty erroreCarrello}">
                    <div class="alert alert-error">${erroreCarrello}</div>
                </c:if>

                <div class="catalogo-risultati-header">
                    <span class="catalogo-count">${fn:length(prodotti)} prodott${fn:length(prodotti) == 1 ? 'o' : 'i'} trovat${fn:length(prodotti) == 1 ? 'o' : 'i'}</span>
                </div>

                <c:choose>
                    <c:when test="${empty prodotti}">
                        <div class="catalogo-empty">
                            <i class="ti ti-package catalogo-empty-icon" aria-hidden="true"></i>
                            <p>Nessun prodotto trovato con i filtri selezionati.</p>
                            <a href="${pageContext.request.contextPath}/catalogo">Mostra tutti i prodotti</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="catalogo-grid">
                            <c:forEach var="prodotto" items="${prodotti}">
                                <div class="prodotto-card">

                                    <a class="prodotto-thumb-link"
                                       href="${pageContext.request.contextPath}/prodotto?id=${prodotto.id}"
                                       aria-hidden="true" tabindex="-1">
                                        <div class="prodotto-thumb">
                                            <c:if test="${not empty prodotto.imgPath}">
                                                <img src="${pageContext.request.contextPath}${prodotto.imgPath}"
                                                     alt="${prodotto.nome}" loading="lazy">
                                            </c:if>
                                        </div>
                                    </a>

                                    <div class="prodotto-body">
                                        <h2 class="prodotto-nome">
                                            <a class="prodotto-nome-link"
                                               href="${pageContext.request.contextPath}/prodotto?id=${prodotto.id}">
                                                <c:out value="${prodotto.nome}"/>
                                            </a>
                                        </h2>

                                        <div class="prodotto-meta">
                                            <span><b>Brand:</b> <c:out value="${prodotto.brand}"/></span>
                                            <span><b>Colore:</b> <c:out value="${prodotto.colore}"/></span>
                                        </div>

                                        <c:if test="${not empty prodotto.descrizione}">
                                            <div class="prodotto-descrizione">
                                                <c:out value="${prodotto.descrizione}"/>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="prodotto-footer">
                                        <div class="prodotto-prezzo">
                                            <fmt:formatNumber value="${prodotto.costo}" minFractionDigits="2"
                                                              maxFractionDigits="2"/><span
                                                class="currency">&euro;</span>
                                        </div>

                                        <div class="prodotto-azioni">
                                            <form method="post" action="${pageContext.request.contextPath}/carrello">
                                                <input type="hidden" name="azione" value="aggiungi"/>
                                                <input type="hidden" name="id" value="${prodotto.id}"/>
                                                <input type="hidden" name="origine" value="catalogo"/>
                                                <input type="hidden" name="taglia"
                                                       value="${prodotto.primaTagliaDisponibile}"/>
                                                <input type="hidden" name="quantita" value="1"/>
                                                <button class="btn-primary" type="submit"
                                                        aria-label="Aggiungi ${prodotto.nome} al carrello">Aggiungi al
                                                    carrello
                                                </button>
                                            </form>

                                            <c:if test="${not empty sessionScope.utenteConnesso}">
                                                <form class="form-wishlist" method="post"
                                                      action="${pageContext.request.contextPath}/add-to-wishlist">
                                                    <input type="hidden" name="idProdotto" value="${prodotto.id}"/>
                                                    <button class="btn-primary" type="submit"
                                                            aria-label="Aggiungi ${prodotto.nome} alla wishlist">
                                                        <i class="ti ti-heart"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>

                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>


        </div>


        <a class="catalogo-link-back" href="${pageContext.request.contextPath}/home">Torna alla home</a>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>


<script>var isLoggedIn = ${not empty sessionScope.utenteConnesso};</script>
<script src="${pageContext.request.contextPath}/js/catalogo.js"></script>

</body>
</html>
