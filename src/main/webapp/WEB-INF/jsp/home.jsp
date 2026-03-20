<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SneakerZone - Home</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<c:if test="${not empty messaggioHome}">
    <div class="alert alert-success"><c:out value="${messaggioHome}"/></div>
</c:if>
<main>
    <section class="hero">
        <div class="hero-content">
            <h1>Scopri le tue sneakers preferite</h1>
            <p>La collezione più esclusiva, solo su SneakerZone</p>
            <a href="${pageContext.request.contextPath}/catalogo" class="btn">Acquista ora</a>
        </div>
    </section>
    <section class="evidenza">
        <div class="evidenza-header">
            <h2>Prodotti in evidenza</h2>
            <a class="evidenza-link-tutti" href="${pageContext.request.contextPath}/catalogo">
                Vedi tutti <i class="ti ti-arrow-right" aria-hidden="true"></i>
            </a>
        </div>
        <div class="evidenza-grid">
            <c:forEach var="p" items="${prodottiInEvidenza}">
                <div class="ev-card">
                    <a class="ev-card-img-link" href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                       aria-hidden="true" tabindex="-1">
                        <div class="ev-card-img">
                            <c:if test="${not empty p.imgPath}">
                                <img src="${pageContext.request.contextPath}${p.imgPath}"
                                     alt="${p.nome}" loading="lazy">
                            </c:if>
                        </div>
                    </a>
                    <div class="ev-card-body">
                        <span class="ev-card-brand">${p.brand}</span>
                        <h3 class="ev-card-nome">
                            <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}">${p.nome}</a>
                        </h3>
                        <div class="ev-card-footer">
                            <span class="ev-card-prezzo"><fmt:formatNumber value="${p.costo}" minFractionDigits="2"
                                                                           maxFractionDigits="2"/>&nbsp;&euro;</span>
                            <a class="ev-card-btn"
                               href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                               aria-label="Scopri ${p.nome}">
                                Scopri
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</main>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
</body>
</html>
