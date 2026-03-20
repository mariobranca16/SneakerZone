<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${titoloPagina}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-prodotti.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/admin/layout_admin.jsp"/>

<c:if test="${not empty sessionScope.flashErrore}">
    <div class="alert alert-error">
        <c:out value="${sessionScope.flashErrore}"/>
    </div>
    <c:remove var="flashErrore" scope="session"/>
</c:if>


<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">Catalogo prodotti</h1>
        <p class="admin-page-subtitle">
            <c:choose>
                <c:when test="${empty prodotti}">Nessun prodotto nel catalogo</c:when>
                <c:otherwise>${fn:length(prodotti)} prodotti nel catalogo</c:otherwise>
            </c:choose>
        </p>
    </div>
    <div class="admin-page-actions">
        <a class="btn btn--primary" href="${pageContext.request.contextPath}/admin/prodotto">
            <i class="ti ti-plus"></i> Nuovo prodotto
        </a>
    </div>
</div>


<div class="admin-card admin-card--flush">

    <c:choose>
        <c:when test="${empty prodotti}">
            <div class="prod-empty">
                <i class="ti ti-package"></i>
                <p>Nessun prodotto nel catalogo.</p>
                <a class="btn btn--primary" href="${pageContext.request.contextPath}/admin/prodotto">
                    <i class="ti ti-plus"></i> Aggiungi il primo prodotto
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="admin-table-wrap prod-table-wrap">
                <table class="admin-table prod-table">
                    <thead>
                    <tr>
                        <th class="col-thumb"></th>
                        <th class="col-info">Prodotto</th>
                        <th class="col-brand col-center">Brand</th>
                        <th class="col-price col-center">Prezzo</th>
                        <th class="col-actions col-center">Azioni</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${prodotti}">
                        <tr>
                            <td class="col-thumb">
                                <c:choose>
                                    <c:when test="${not empty p.imgPath}">
                                        <img class="prod-thumb"
                                             src="${pageContext.request.contextPath}${p.imgPath}"
                                             alt="${p.nome}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="prod-thumb prod-thumb-empty">
                                            <i class="ti ti-tag"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="col-info">
                                <span class="prod-nome">${p.nome}</span>
                                <c:if test="${not empty p.colore}">
                                    <span class="prod-colore">${p.colore}</span>
                                </c:if>
                            </td>
                            <td class="col-brand col-center">
                                <span class="prod-brand">${p.brand}</span>
                            </td>
                            <td class="col-price col-center">
                                    <span class="prod-price">
                                        <fmt:formatNumber value="${p.costo}" type="number"
                                                          minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                                    </span>
                            </td>
                            <td class="col-actions col-center">
                                <div class="admin-table-actions">
                                    <a class="btn btn--small btn--edit"
                                       href="${pageContext.request.contextPath}/admin/prodotto?id=${p.id}">
                                        Modifica
                                    </a>
                                    <form class="inline-form" method="post"
                                          action="${pageContext.request.contextPath}/admin/prodotti">
                                        <input type="hidden" name="id" value="${p.id}">
                                        <input type="hidden" name="azione" value="elimina">
                                        <button class="btn btn--small btn--danger" type="submit"
                                                onclick="return confirm('Eliminare il prodotto &quot;${p.nome}&quot;?')">
                                            Elimina
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

</div>

</div>
</main>

<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>

</body>
</html>
