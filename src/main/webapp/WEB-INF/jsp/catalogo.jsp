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
                <form method="get" action="${pageContext.request.contextPath}/catalogo" id="filtriForm">


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

                <div id="risultatiProdotti">
                    <jsp:include page="/WEB-INF/jsp/catalogo_risultati.jsp"/>
                </div>

            </div>


        </div>


        <a class="catalogo-link-back" href="${pageContext.request.contextPath}/home">Torna alla home</a>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>


<script src="${pageContext.request.contextPath}/js/catalogo.js"></script>

</body>
</html>
