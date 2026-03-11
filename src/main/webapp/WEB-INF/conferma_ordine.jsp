<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Ordine Confermato – SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/conferma_ordine.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main>
    <div class="confirm-page">


        <div class="checkout-steps">
            <div class="step step-done">
                <span class="step-num"><i class="ti ti-check"></i></span>
                <span class="step-label">Carrello</span>
            </div>
            <div class="step-line step-line-done"></div>
            <div class="step step-done">
                <span class="step-num"><i class="ti ti-check"></i></span>
                <span class="step-label">Checkout</span>
            </div>
            <div class="step-line step-line-done"></div>
            <div class="step step-done">
                <span class="step-num"><i class="ti ti-check"></i></span>
                <span class="step-label">Conferma</span>
            </div>
        </div>


        <div class="confirm-success-banner">
            <div class="confirm-success-icon" aria-hidden="true">
                <i class="ti ti-circle-check"></i>
            </div>
            <div>
                <h1 class="confirm-title">Grazie per il tuo ordine!</h1>
                <p class="confirm-subtitle">
                    L&rsquo;ordine &egrave; stato confermato e verr&agrave; elaborato al pi&ugrave; presto.
                    Puoi seguire lo stato nella sezione <em>I miei ordini</em>.
                </p>
            </div>
        </div>


        <div class="confirm-card">


            <div class="confirm-meta-grid">
                <div class="confirm-meta-item">
                    <span class="confirm-meta-label">Numero ordine</span>
                    <span class="confirm-meta-value">#${ordine.id}</span>
                </div>
                <div class="confirm-meta-item">
                    <span class="confirm-meta-label">Data</span>
                    <span class="confirm-meta-value">${dataOrdine}</span>
                </div>
                <div class="confirm-meta-item">
                    <span class="confirm-meta-label">Stato</span>
                    <span class="confirm-status-badge">${ordine.stato.label}</span>
                </div>
                <div class="confirm-meta-item">
                    <span class="confirm-meta-label">Articoli</span>
                    <span class="confirm-meta-value">${fn:length(ordine.dettagliOrdine)}</span>
                </div>
            </div>


            <h2 class="confirm-section-title">Riepilogo prodotti</h2>
            <div class="confirm-table-wrap">
                <table class="confirm-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Taglia</th>
                        <th>Quantit&agrave;</th>
                        <th>Prezzo&nbsp;unit.</th>
                        <th>Subtotale</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:set var="totale" value="0"/>
                    <c:forEach var="d" items="${ordine.dettagliOrdine}" varStatus="vs">
                        <c:set var="totale" value="${totale + (d.costo * d.quantita)}"/>
                        <tr>
                            <td class="confirm-row-num">${vs.count}</td>
                            <td>${d.taglia}</td>
                            <td>${d.quantita}</td>
                            <td>
                                <fmt:formatNumber value="${d.costo}" type="number"
                                                  minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                            </td>
                            <td class="confirm-subtotale">
                                <fmt:formatNumber value="${d.subtotale}" type="number"
                                                  minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>


            <div class="confirm-total-row">
                <span class="confirm-total-label">Totale ordine</span>
                <span class="confirm-total-value">
                    <fmt:formatNumber value="${totale}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
                </span>
            </div>


            <div class="confirm-actions">
                <a class="confirm-btn-primary" href="${pageContext.request.contextPath}/ordini">
                    I miei ordini
                </a>
                <a class="confirm-btn-secondary" href="${pageContext.request.contextPath}/home">
                    Torna alla home
                </a>
            </div>

        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

</body>
</html>
