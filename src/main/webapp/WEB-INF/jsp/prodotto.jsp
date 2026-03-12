<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${prodotto.nome}"/> - SneakerZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/prodotto.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp" />

<main class="prodotto-page">

    <!-- Messaggi -->
    <c:if test="${not empty messaggio}">
        <div class="alert alert-success">${messaggio}</div>
    </c:if>
    <c:if test="${not empty erroreCarrello}">
        <div class="alert alert-error">${erroreCarrello}</div>
    </c:if>

    <!-- Sezione prodotto -->
    <section class="prodotto-dettaglio">

        <!-- Galleria immagini -->
        <div class="prodotto-immagini">
            <div class="immagine-principale">
                <c:choose>
                    <c:when test="${not empty prodotto.immagini}">
                        <img id="imgPrincipale" src="${pageContext.request.contextPath}${prodotto.immagini[0].imgPath}" alt="${prodotto.nome}">
                    </c:when>
                    <c:otherwise>
                        <div class="no-immagine">
                            <i class="fas fa-image"></i>
                            <p>Immagine non disponibile</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <c:if test="${not empty prodotto.immagini && fn:length(prodotto.immagini) > 1}">
                <div class="miniature">
                    <c:forEach var="img" items="${prodotto.immagini}">
                        <img src="${pageContext.request.contextPath}${img.imgPath}" alt="${img.descrizione}"
                             class="miniatura">
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <!-- Info prodotto -->
        <div class="prodotto-info">
            <p class="prodotto-brand"><c:out value="${prodotto.brand}"/></p>
            <h1 class="prodotto-nome"><c:out value="${prodotto.nome}"/></h1>
            <p class="prodotto-colore"><strong>Colore:</strong> <c:out value="${prodotto.colore}"/></p>

            <p class="prodotto-prezzo">
                <fmt:formatNumber value="${prodotto.costo}" minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
            </p>

            <!-- Categorie -->
            <c:if test="${not empty prodotto.categorie}">
                <div class="prodotto-categorie">
                    <c:forEach var="cat" items="${prodotto.categorie}">
                        <a href="${pageContext.request.contextPath}/catalogo?categoria=${cat.nome}" class="tag-categoria">${cat.nome}</a>
                    </c:forEach>
                </div>
            </c:if>

            <!-- Form aggiungi al carrello -->
            <form method="post" action="${pageContext.request.contextPath}/carrello" class="form-carrello" id="formCarrello">
                <input type="hidden" name="azione" value="aggiungi"/>
                <input type="hidden" name="id" value="${prodotto.id}"/>
                <input type="hidden" name="origine" value="prodotto"/>

                <!-- Selezione taglia -->
                <c:if test="${not empty prodotto.taglie}">
                    <div class="selezione-taglia">
                        <label for="taglia">Taglia:</label>
                        <select name="taglia" id="taglia" required>
                            <option value="">Seleziona</option>
                            <c:forEach var="t" items="${prodotto.taglie}">
                                <c:if test="${t.quantita > 0}">
                                    <option value="${t.taglia}">${t.taglia}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>

                <!-- Quantita -->
                <div class="selezione-quantita">
                    <label for="quantita">Quantit&agrave;:</label>
                    <input type="number" name="quantita" id="quantita" value="1" min="1" max="10" required>
                </div>

                <button type="submit" class="btn-carrello">
                    <i class="fas fa-shopping-cart" aria-hidden="true"></i> Aggiungi al carrello
                </button>
            </form>

            <!-- Wishlist -->
            <c:if test="${not empty sessionScope.utenteConnesso}">
                <form method="post" action="${pageContext.request.contextPath}/add-to-wishlist" class="form-wishlist">
                    <input type="hidden" name="idProdotto" value="${prodotto.id}"/>
                    <button type="submit" class="btn-wishlist">
                        <i class="fas fa-heart" aria-hidden="true"></i> Aggiungi alla wishlist
                    </button>
                </form>
            </c:if>

            <!-- Descrizione -->
            <c:if test="${not empty prodotto.descrizione}">
                <div class="prodotto-descrizione">
                    <h3>Descrizione</h3>
                    <p><c:out value="${prodotto.descrizione}"/></p>
                </div>
            </c:if>
        </div>
    </section>

    <!-- Sezione recensioni -->
    <section class="sezione-recensioni">
        <h2>Recensioni</h2>

        <c:if test="${param.successoRecensione == '1'}">
            <div class="alert alert-success">Recensione pubblicata con successo!</div>
        </c:if>
        <c:if test="${param.erroreRecensione == '1'}">
            <div class="alert alert-error">Errore nell'invio della recensione. Verifica i dati e riprova.</div>
        </c:if>

        <c:if test="${puoRecensire}">
            <div class="form-recensione-wrapper">
                <h3>Scrivi una recensione</h3>
                <form method="post" action="${pageContext.request.contextPath}/aggiungi-recensione" class="form-recensione" id="formRecensione">
                    <input type="hidden" name="id" value="${prodotto.id}"/>

                    <div class="recensione-campo">
                        <label for="titolo">Titolo <span class="obbligatorio">*</span></label>
                        <input type="text" id="titolo" name="titolo" maxlength="255" required placeholder="Riassumi la tua esperienza">
                    </div>

                    <div class="recensione-campo">
                        <label>Valutazione <span class="obbligatorio">*</span></label>
                        <div class="stelle-input" id="stelleInput" role="group" aria-label="Valutazione da 1 a 5 stelle">
                            <input type="hidden" name="valutazione" id="valutazioneHidden">
                            <c:forEach begin="1" end="5" var="s">
                                <i class="far fa-star" data-valore="${s}" role="button" aria-label="${s} stelle" tabindex="0"></i>
                            </c:forEach>
                        </div>
                        <p class="stelle-errore" id="stelleErrore" style="display:none;">Seleziona una valutazione.</p>
                    </div>

                    <div class="recensione-campo">
                        <label for="commento">Commento</label>
                        <textarea id="commento" name="commento" rows="4" maxlength="2000" placeholder="Descrivi la tua esperienza con questo prodotto..."></textarea>
                    </div>

                    <button type="submit" class="btn-recensione">Pubblica recensione</button>
                </form>
            </div>
        </c:if>

        <c:if test="${haGiaRecensito}">
            <p class="info-recensione">Hai già recensito questo prodotto.</p>
        </c:if>

        <c:choose>
            <c:when test="${not empty recensioni}">
                <div class="lista-recensioni">
                    <c:forEach var="rec" items="${recensioni}">
                        <div class="recensione-card">
                            <div class="recensione-header">
                                <span class="recensione-titolo"><c:out value="${rec.titolo}"/></span>
                                <span class="recensione-data">
                                    <fmt:parseDate value="${rec.dataRecensione}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
                                </span>
                            </div>
                            <div class="recensione-stelle" aria-label="Valutazione: ${rec.valutazione} su 5 stelle">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= rec.valutazione}">
                                            <i class="fas fa-star" aria-hidden="true"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="far fa-star" aria-hidden="true"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <c:if test="${not empty rec.commento}">
                                <p class="recensione-commento"><c:out value="${rec.commento}"/></p>
                            </c:if>
                            <c:if test="${not empty sessionScope.utenteConnesso && (rec.idUtente == sessionScope.utenteConnesso.id || sessionScope.utenteConnesso.admin)}">
                                <form method="post" action="${pageContext.request.contextPath}/rimuovi-recensione" class="form-rimuovi-recensione">
                                    <input type="hidden" name="idRecensione" value="${rec.id}"/>
                                    <button type="submit" class="btn-rimuovi-recensione">
                                        <i class="fas fa-trash-alt" aria-hidden="true"></i> Elimina
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p class="nessuna-recensione">Nessuna recensione per questo prodotto.</p>
            </c:otherwise>
        </c:choose>
    </section>

    <div class="torna-catalogo">
        <a href="${pageContext.request.contextPath}/catalogo"><i class="fas fa-arrow-left" aria-hidden="true"></i> Torna al catalogo</a>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />

<script src="${pageContext.request.contextPath}/js/prodotto.js"></script>
</body>
</html>
