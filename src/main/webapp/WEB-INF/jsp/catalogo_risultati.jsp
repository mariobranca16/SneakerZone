<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Frammento dei risultati: incluso da catalogo.jsp al primo caricamento,
     poi restituito direttamente dalla servlet nelle richieste AJAX di catalogo.js. --%>
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
                                              maxFractionDigits="2"/><span class="currency">&euro;</span>
                        </div>
                        <div class="prodotto-azioni">
                            <a class="btn-primary"
                               href="${pageContext.request.contextPath}/prodotto?id=${prodotto.id}">Scopri</a>
                            <!-- pulsante wishlist: visibile solo agli utenti loggati -->
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
