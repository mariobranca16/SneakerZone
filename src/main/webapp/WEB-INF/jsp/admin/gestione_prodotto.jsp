<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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

<c:if test="${not empty sessionScope.flashSuccesso}">
    <div class="alert alert-success"><c:out value="${sessionScope.flashSuccesso}"/></div>
    <c:remove var="flashSuccesso" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashErrore}">
    <div class="alert alert-error"><c:out value="${sessionScope.flashErrore}"/></div>
    <c:remove var="flashErrore" scope="session"/>
</c:if>

<div class="admin-page-header">
    <div>
        <h1 class="admin-page-title">${titoloPagina}</h1>
        <c:if test="${prodotto != null && prodotto.id > 0}">
            <p class="admin-page-subtitle">ID prodotto: ${prodotto.id}</p>
        </c:if>
    </div>
    <div class="admin-page-actions">
        <a class="btn btn--small" href="${pageContext.request.contextPath}/admin/prodotti">
            <i class="ti ti-arrow-left"></i> Torna ai prodotti
        </a>
    </div>
</div>


<div class="admin-card">
    <div class="admin-card-title"><i class="ti ti-package"></i> Dati prodotto e taglie</div>

    <form method="post" action="${pageContext.request.contextPath}/admin/prodotto" class="admin-form"
          enctype="multipart/form-data">
        <c:if test="${prodotto != null && prodotto.id > 0}">
            <input type="hidden" name="id" value="${prodotto.id}">
        </c:if>


        <div class="form-grid">

            <div class="form-row">
                <label class="label" for="nome">Nome</label>
                <input class="input" type="text" id="nome" name="nome"
                       value="${fn:escapeXml(prodotto.nome)}" maxlength="150" required>
                <c:if test="${not empty erroreNome}">
                    <div class="form-error"><c:out value="${erroreNome}"/></div>
                </c:if>
            </div>

            <div class="form-row">
                <label class="label" for="brand">Brand</label>
                <input class="input" type="text" id="brand" name="brand"
                       value="${fn:escapeXml(prodotto.brand)}" maxlength="100" required>
                <c:if test="${not empty erroreBrand}">
                    <div class="form-error"><c:out value="${erroreBrand}"/></div>
                </c:if>
            </div>

            <div class="form-row">
                <label class="label" for="colore">Colore</label>
                <input class="input" type="text" id="colore" name="colore"
                       value="${fn:escapeXml(prodotto.colore)}" maxlength="50">
                <c:if test="${not empty erroreColore}">
                    <div class="form-error"><c:out value="${erroreColore}"/></div>
                </c:if>
            </div>

            <div class="form-row">
                <label class="label" for="genere">Genere</label>
                <select class="input" id="genere" name="genere">
                    <option value="Unisex" ${prodotto.genere == 'Unisex' || empty prodotto.genere ? 'selected' : ''}>
                        Unisex
                    </option>
                    <option value="Uomo"   ${prodotto.genere == 'Uomo'   ? 'selected' : ''}>Uomo</option>
                    <option value="Donna"  ${prodotto.genere == 'Donna'  ? 'selected' : ''}>Donna</option>
                </select>
                <c:if test="${not empty erroreGenere}">
                    <div class="form-error"><c:out value="${erroreGenere}"/></div>
                </c:if>
            </div>

            <div class="form-row">
                <label class="label" for="costo">Prezzo (&euro;)</label>
                <input class="input" type="text" id="costo" name="costo"
                       value="${fn:escapeXml(not empty formCosto ? formCosto : prodotto.costo)}" placeholder="0.00"
                       required>
                <c:if test="${not empty erroreCosto}">
                    <div class="form-error"><c:out value="${erroreCosto}"/></div>
                </c:if>
            </div>

            <div class="form-row form-row--full">
                <label class="label" for="descrizione">Descrizione</label>
                <textarea class="textarea" id="descrizione" name="descrizione"
                          rows="4" maxlength="2000">${fn:escapeXml(prodotto.descrizione)}</textarea>
                <c:if test="${not empty erroreDescrizione}">
                    <div class="form-error"><c:out value="${erroreDescrizione}"/></div>
                </c:if>
            </div>

        </div>


        <div class="taglie-section">
            <div class="taglie-section-title">Taglie e disponibilit&agrave;</div>
            <div class="taglie-grid">
                <c:forEach var="t" items="${taglieDisponibili}">
                    <div class="taglia-row">
                        <span class="taglia-label">${t}</span>
                        <input class="input taglia-input" type="number" min="0"
                               name="q_${t}" value="${quantitaPerTaglia[t]}">
                    </div>
                </c:forEach>
            </div>
            <c:if test="${not empty erroreTaglie}">
                <div class="form-error"><c:out value="${erroreTaglie}"/></div>
            </c:if>
        </div>


        <div class="taglie-section">
            <div class="taglie-section-title">Categorie</div>
            <div class="taglie-grid">
                <c:forEach var="cat" items="${tutteCategorie}">
                    <div class="taglia-row">
                        <input type="checkbox" id="cat_${cat.id}" name="categoria_id"
                               value="${cat.id}"
                            ${idCategorieSelezionate.contains(cat.id) ? 'checked' : ''}>
                        <label class="taglia-label" for="cat_${cat.id}"><c:out value="${cat.nome}"/></label>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${not empty erroreCategorie}">
                <div class="form-error"><c:out value="${erroreCategorie}"/></div>
            </c:if>
        </div>

        <div class="form-row form-row--full">
            <label class="label" for="fileImmagine">Immagine prodotto</label>
            <input class="input" type="file" id="fileImmagine" name="fileImmagine"
                   accept="image/jpeg,image/png,image/webp">
            <c:if test="${not empty prodotto.imgPath}">
                <span class="field-hint">
                    Immagine attuale:
                    <img src="${pageContext.request.contextPath}${prodotto.imgPath}"
                         class="img-preview">
                </span>
            </c:if>
        </div>

        <div class="admin-actions">
            <button class="btn btn--primary" type="submit">
                Salva prodotto
            </button>
        </div>
    </form>
</div>


<c:if test="${prodotto != null && prodotto.id > 0}">
    <div class="admin-card admin-card--flush">
        <div class="admin-card-title">
            <i class="ti ti-star"></i> Recensioni
        </div>

        <c:choose>
            <c:when test="${empty recensioni}">
                <p class="admin-empty-note">
                    Nessuna recensione per questo prodotto.
                </p>
            </c:when>
            <c:otherwise>
                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                        <tr>
                            <th>Utente</th>
                            <th>Titolo</th>
                            <th>Voto</th>
                            <th>Data</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="rec" items="${recensioni}">
                            <tr>
                                <td><c:out value="${emailUtenti[rec.idUtente]}"/></td>
                                <td><c:out value="${rec.titolo}"/></td>
                                <td>
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${i <= rec.valutazione}">&#9733;</c:when>
                                            <c:otherwise>&#9734;</c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </td>
                                <td>
                                    <fmt:parseDate value="${rec.dataRecensione}" pattern="yyyy-MM-dd" var="parsedDate"
                                                   type="date"/>
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/rimuovi-recensione"
                                          onsubmit="return confirm('Eliminare questa recensione?')">
                                        <input type="hidden" name="idRecensione" value="${rec.id}"/>
                                        <button type="submit" class="btn btn--small btn--danger">
                                            <i class="ti ti-trash"></i> Elimina
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

</div>
</main>

<jsp:include page="/WEB-INF/jsp/admin/footer_admin.jsp"/>

</body>
</html>
