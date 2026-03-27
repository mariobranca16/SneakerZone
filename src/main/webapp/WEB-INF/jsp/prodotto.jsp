<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- Dettaglio prodotto. ${prodotto} è caricato con eager loading (taglie, categorie, immagine).
     Mostra form carrello, form wishlist (solo utenti loggati) e sezione recensioni. --%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${prodotto.nome}"/> - SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/prodotto.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="prodotto-page">
    <a class="back-link" href="${pageContext.request.contextPath}/catalogo">
        <i class="ti ti-arrow-left" aria-hidden="true"></i> Catalogo
    </a>

    <!-- messaggi flash: ${messaggio} per aggiunta al carrello, ${erroreCarrello} se disponibilità insufficiente -->
    <c:if test="${not empty messaggio}">
        <div class="alert alert-success">${messaggio}</div>
    </c:if>
    <c:if test="${not empty erroreCarrello}">
        <div class="alert alert-error">${erroreCarrello}</div>
    </c:if>

    <section class="prodotto-dettaglio">

        <div class="prodotto-immagini">
            <div class="immagine-principale">
                <c:choose>
                    <c:when test="${not empty prodotto.imgPath}">
                        <img src="${pageContext.request.contextPath}${prodotto.imgPath}"
                             alt="${prodotto.nome}">
                    </c:when>
                    <c:otherwise>
                        <div class="no-immagine">
                            <i class="ti ti-photo"></i>
                            <p>Immagine non disponibile</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="prodotto-info">
            <p class="prodotto-brand"><c:out value="${prodotto.brand}"/></p>
            <h1 class="prodotto-nome"><c:out value="${prodotto.nome}"/></h1>
            <p class="prodotto-colore"><strong>Colore:</strong> <c:out value="${prodotto.colore}"/></p>
            <p class="prodotto-prezzo">
                <fmt:formatNumber value="${prodotto.costo}" minFractionDigits="2" maxFractionDigits="2"/>&nbsp;&euro;
            </p>

            <!-- tag categorie: ogni tag linka al catalogo filtrato per quella categoria -->
            <c:if test="${not empty prodotto.categorie}">
                <div class="prodotto-categorie">
                    <c:forEach var="cat" items="${prodotto.categorie}">
                        <a href="${pageContext.request.contextPath}/catalogo?categoria=${cat.nome}"
                           class="tag-categoria">${cat.nome}</a>
                    </c:forEach>
                </div>
            </c:if>

            <!-- form carrello: prodotto.js intercetta il submit per validare taglia e quantità -->
            <form method="post" action="${pageContext.request.contextPath}/carrello" class="form-carrello"
                  id="formCarrello">
                <input type="hidden" name="azione" value="aggiungi"/>
                <input type="hidden" name="id" value="${prodotto.id}"/>
                <input type="hidden" name="origine" value="prodotto"/>

                <!-- selezione taglia: mostra solo le taglie con quantita > 0 (${prodotto.taglie} include anche quelle esaurite) -->
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
                <div class="selezione-quantita">
                    <label for="quantita">Quantit&agrave;:</label>
                    <input type="number" name="quantita" id="quantita" value="1" min="1" max="10" required>
                </div>
                <button type="submit" class="btn-carrello">
                    <i class="ti ti-shopping-cart" aria-hidden="true"></i> Aggiungi al carrello
                </button>
            </form>

            <!-- form wishlist: solo utenti loggati; wishlist.js intercetta via Fetch per aggiornare il badge -->
            <c:if test="${not empty sessionScope.utenteConnesso}">
                <form method="post" action="${pageContext.request.contextPath}/add-to-wishlist" class="form-wishlist">
                    <input type="hidden" name="idProdotto" value="${prodotto.id}"/>
                    <button type="submit" class="btn-primary">
                        <i class="ti ti-hearts" aria-hidden="true"></i> Aggiungi alla wishlist
                    </button>
                </form>
            </c:if>

            <c:if test="${not empty prodotto.descrizione}">
                <div class="prodotto-descrizione">
                    <h3>Descrizione</h3>
                    <p><c:out value="${prodotto.descrizione}"/></p>
                </div>
            </c:if>
        </div>
    </section>

    <!-- sezione recensioni: il form appare solo se l'utente può ancora recensire (puoRecensire) -->
    <section class="sezione-recensioni">
        <h2>Recensioni</h2>

        <!-- messaggi esito: param.successoRecensione=1 dopo redirect, ${erroreRecensione} per errori -->
        <c:if test="${param.successoRecensione == '1'}">
            <div class="alert alert-success">Recensione pubblicata con successo!</div>
        </c:if>
        <c:if test="${not empty erroreRecensione}">
            <div class="alert alert-error"><c:out value="${erroreRecensione}"/></div>
        </c:if>
        <c:if test="${param.erroreRecensione == '1' && empty erroreRecensione}">
            <div class="alert alert-error">Errore nell'invio della recensione. Verifica i dati e riprova.</div>
        </c:if>

        <!-- form recensione: visibile solo se l'utente può ancora recensire -->
        <c:if test="${puoRecensire}">
            <div class="form-recensione-wrapper">
                <h3>Scrivi una recensione</h3>
                <form method="post" action="${pageContext.request.contextPath}/aggiungi-recensione"
                      class="form-recensione" id="formRecensione">
                    <input type="hidden" name="id" value="${prodotto.id}"/>
                    <div class="recensione-campo">
                        <label for="titolo">Titolo <span class="obbligatorio">*</span></label>
                        <!-- campo titolo: ripopolato dopo errore -->
                        <input type="text" id="titolo" name="titolo" maxlength="255" required
                               placeholder="Riassumi la tua esperienza"
                               value="${fn:escapeXml(recensioneTitolo)}">
                        <c:if test="${not empty erroreTitoloRecensione}">
                            <p class="recensione-errore-campo">${erroreTitoloRecensione}</p>
                        </c:if>
                    </div>
                    <!-- widget stelle: prodotto.js gestisce hover/click; il valore va nel campo hidden -->
                    <div class="recensione-campo">
                        <label>Valutazione <span class="obbligatorio">*</span></label>
                        <div class="stelle-input" id="stelleInput" role="group"
                             aria-label="Valutazione da 1 a 5 stelle">
                            <input type="hidden" name="valutazione" id="valutazioneHidden"
                                   value="${fn:escapeXml(recensioneValutazione)}">
                            <c:forEach begin="1" end="5" var="s">
                                <button type="button" class="stella-input" data-valore="${s}" aria-label="${s} stelle">
                                    <span aria-hidden="true">&#9734;</span>
                                </button>
                            </c:forEach>
                        </div>
                            <%-- classe "visibile": aggiunta dal server dopo errore, o da prodotto.js al submit --%>
                        <p class="stelle-errore<c:if test="${not empty erroreValutazioneRecensione}"> visibile</c:if>"
                           id="stelleErrore">
                            <c:choose>
                                <c:when test="${not empty erroreValutazioneRecensione}">${erroreValutazioneRecensione}</c:when>
                                <c:otherwise>Seleziona una valutazione.</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="recensione-campo">
                        <label for="commento">Commento</label>
                        <!-- campo commento: ripopolato dopo errore -->
                        <textarea id="commento" name="commento" rows="4" maxlength="2000"
                                  placeholder="Descrivi la tua esperienza con questo prodotto...">${fn:escapeXml(recensioneCommento)}</textarea>
                        <c:if test="${not empty erroreCommentoRecensione}">
                            <p class="recensione-errore-campo">${erroreCommentoRecensione}</p>
                        </c:if>
                    </div>
                    <button type="submit" class="btn-primary">Pubblica recensione</button>
                </form>
            </div>
        </c:if>

        <c:if test="${haGiaRecensito}">
            <p class="info-recensione">Hai già recensito questo prodotto.</p>
        </c:if>

        <!-- elenco recensioni oppure messaggio "nessuna recensione" -->
        <c:choose>
            <c:when test="${not empty recensioni}">
                <div class="lista-recensioni">
                    <c:forEach var="rec" items="${recensioni}">
                        <div class="recensione-card">
                            <div class="recensione-header">
                                <div class="recensione-meta">
                                    <span class="recensione-autore"><c:out value="${emailUtenti[rec.idUtente]}"/></span>
                                    <span class="recensione-data">
                                        <fmt:parseDate value="${rec.dataRecensione}" pattern="yyyy-MM-dd" var="parsedDate"
                                                       type="date"/>
                                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
                                    </span>
                                </div>
                                <span class="recensione-titolo"><c:out value="${rec.titolo}"/></span>
                            </div>
                            <!-- stelle di visualizzazione -->
                            <div class="recensione-stelle" aria-label="Valutazione: ${rec.valutazione} su 5 stelle">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= rec.valutazione}">
                                            <span class="stella piena" aria-hidden="true">&#9733;</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stella vuota" aria-hidden="true">&#9734;</span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <c:if test="${not empty rec.commento}">
                                <p class="recensione-commento"><c:out value="${rec.commento}"/></p>
                            </c:if>
                            <!-- pulsante elimina: visibile solo all'autore oppure a un admin -->
                            <c:if test="${not empty sessionScope.utenteConnesso && (rec.idUtente == sessionScope.utenteConnesso.id || sessionScope.utenteConnesso.admin)}">
                                <form method="post" action="${pageContext.request.contextPath}/rimuovi-recensione"
                                      class="form-rimuovi-recensione">
                                    <input type="hidden" name="idRecensione" value="${rec.id}"/>
                                    <button type="submit" class="btn-rimuovi-recensione">
                                        <i class="ti ti-trash" aria-hidden="true"></i> Elimina
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
        <a href="${pageContext.request.contextPath}/catalogo"><i class="ti ti-arrow-left" aria-hidden="true"></i> Torna
            al catalogo</a>
    </div>
</main>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/prodotto.js"></script>
</body>
</html>
