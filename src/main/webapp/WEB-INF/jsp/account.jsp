<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="ctx" content="${pageContext.request.contextPath}">
    <title>Profilo utente - SneakerZone</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons@latest/iconfont/tabler-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/account.css">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>


<c:if test="${modificaEffettuata}">
    <div class="alert alert-success" id="successAlert">
        <i class="ti ti-circle-check"></i>&nbsp; Modifiche salvate con successo.
    </div>
</c:if>

<main>
    <div class="account-wrap" data-tab="${fn:escapeXml(tabAttiva)}">


        <h1 class="account-page-title">Profilo</h1>


        <div class="account-tabs" role="tablist" aria-label="Sezioni account">
            <button class="tab-btn active" type="button" id="tab-dati-personali" data-section="dati-personali"
                    role="tab" aria-controls="section-dati-personali" aria-selected="true" tabindex="0">
                <i class="ti ti-user-circle"></i>
                <span>Dati personali</span>
            </button>
            <button class="tab-btn" type="button" id="tab-password" data-section="password"
                    role="tab" aria-controls="section-password" aria-selected="false" tabindex="-1">
                <i class="ti ti-lock"></i>
                <span>Sicurezza</span>
            </button>
            <button class="tab-btn" type="button" id="tab-indirizzo" data-section="indirizzo"
                    role="tab" aria-controls="section-indirizzo" aria-selected="false" tabindex="-1">
                <i class="ti ti-map-pin"></i>
                <span>Indirizzi</span>
            </button>
            <button class="tab-btn" type="button" id="tab-pagamento" data-section="pagamento"
                    role="tab" aria-controls="section-pagamento" aria-selected="false" tabindex="-1">
                <i class="ti ti-credit-card"></i>
                <span>Pagamento</span>
            </button>
        </div>


        <div class="account-section active" id="section-dati-personali" role="tabpanel"
             aria-labelledby="tab-dati-personali">
            <div class="section-header">
                <h1 class="section-title">Dati personali</h1>
                <p class="section-subtitle">Gestisci le informazioni del tuo profilo</p>
            </div>

            <form id="formDatiPersonali" class="account-form"
                  action="${pageContext.request.contextPath}/myAccount/datiPersonali" method="post" novalidate>

                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome"
                           value="${fn:escapeXml(not empty formNome ? formNome : utente.nome)}" required>
                    <c:if test="${not empty erroreNome}">
                        <span class="field-error"><c:out value="${erroreNome}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome"
                           value="${fn:escapeXml(not empty formCognome ? formCognome : utente.cognome)}" required>
                    <c:if test="${not empty erroreCognome}">
                        <span class="field-error"><c:out value="${erroreCognome}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email"
                           value="${fn:escapeXml(not empty formEmail ? formEmail : utente.email)}" required>
                    <c:if test="${not empty erroreEmail}">
                        <span class="field-error"><c:out value="${erroreEmail}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono"
                           value="${fn:escapeXml(not empty formTelefono ? formTelefono : utente.telefono)}">
                    <c:if test="${not empty erroreTelefono}">
                        <span class="field-error"><c:out value="${erroreTelefono}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="dataDiNascita">Data di nascita</label>
                    <input type="date" id="dataDiNascita" name="dataDiNascita"
                           value="${fn:escapeXml(not empty formDataNascita ? formDataNascita : utente.dataDiNascita)}">
                    <c:if test="${not empty erroreDataNascita}">
                        <span class="field-error"><c:out value="${erroreDataNascita}"/></span>
                    </c:if>
                </div>

                <div class="account-actions">
                    <button class="btn-primary" type="submit">
                        Salva modifiche
                    </button>
                </div>
            </form>
        </div>


        <div class="account-section" id="section-password" role="tabpanel"
             aria-labelledby="tab-password" hidden>
            <div class="section-header">
                <h1 class="section-title">Sicurezza</h1>
                <p class="section-subtitle">Modifica la tua password di accesso</p>
            </div>

            <form class="account-form" action="${pageContext.request.contextPath}/myAccount/password" method="post"
                  novalidate id="formPassword">

                <div class="form-group full">
                    <label for="passwordAttuale">Password attuale</label>
                    <input type="password" id="passwordAttuale" name="passwordAttuale" required
                           autocomplete="current-password">
                    <c:if test="${not empty errorePasswordAttuale}">
                        <span class="field-error"><c:out value="${errorePasswordAttuale}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="nuovaPassword">Nuova password</label>
                    <input type="password" id="nuovaPassword" name="nuovaPassword" required autocomplete="new-password"
                           minlength="8" maxlength="64">
                    <span class="field-hint">8-64 caratteri, con maiuscola, minuscola, numero e simbolo, senza spazi.</span>
                    <c:if test="${not empty erroreNuovaPassword}">
                        <span class="field-error"><c:out value="${erroreNuovaPassword}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="confermaPassword">Conferma nuova password</label>
                    <input type="password" id="confermaPassword" name="confermaPassword" required
                           autocomplete="new-password">
                    <c:if test="${not empty erroreConfermaPassword}">
                        <span class="field-error"><c:out value="${erroreConfermaPassword}"/></span>
                    </c:if>
                </div>

                <div class="account-actions">
                    <button class="btn-primary" type="submit">
                        <i class="ti ti-key"></i> Aggiorna password
                    </button>
                </div>
            </form>
        </div>


        <div class="account-section" id="section-indirizzo" role="tabpanel"
             aria-labelledby="tab-indirizzo" hidden>
            <div class="section-header">
                <h1 class="section-title">Indirizzo di spedizione</h1>
                <p class="section-subtitle">Gestisci i tuoi indirizzi di consegna</p>
            </div>


            <c:choose>
                <c:when test="${empty indirizzi}">
                    <p class="no-data-msg">
                        <i class="ti ti-info-circle"></i> Nessun indirizzo salvato.
                    </p>
                </c:when>
                <c:otherwise>
                    <div class="profilo-addr-list">
                        <c:forEach var="ind" items="${indirizzi}">
                            <div class="profilo-addr-card"
                                 data-id="${ind.id}"
                                 data-destinatario="${fn:escapeXml(ind.destinatario)}"
                                 data-via="${fn:escapeXml(ind.via)}"
                                 data-cap="${fn:escapeXml(ind.cap)}"
                                 data-citta="${fn:escapeXml(ind.citta)}"
                                 data-provincia="${fn:escapeXml(ind.provincia)}"
                                 data-paese="${fn:escapeXml(ind.paese)}">
                                <div class="profilo-addr-info">
                                    <span class="addr-destinatario"><c:out value="${ind.destinatario}"/></span>
                                    <span class="addr-detail"><c:out value="${ind.via}"/></span>
                                    <span class="addr-detail">
                                        <c:out value="${ind.cap}"/> <c:out value="${ind.citta}"/> (<c:out
                                            value="${ind.provincia}"/>), <c:out value="${ind.paese}"/>
                                    </span>
                                </div>
                                <div class="profilo-addr-actions">
                                    <button type="button" class="btn-addr-edit"
                                            onclick="apriEditIndirizzo(this.closest('.profilo-addr-card'))">
                                        <i class="ti ti-pencil"></i> Modifica
                                    </button>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/myAccount/indirizzo/elimina"
                                          class="form-inline-del"
                                          onsubmit="return confirm('Eliminare questo indirizzo?')">
                                        <input type="hidden" name="idIndirizzo" value="${ind.id}">
                                        <button type="submit" class="btn-addr-del">
                                            <i class="ti ti-trash"></i> Elimina
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>


            <div class="profilo-edit-section" id="editIndirizzoWrap" data-apri-edit="${not empty apriEditIndirizzo}">
                <h3 class="edit-section-title">
                    <i class="ti ti-pencil"></i> Modifica indirizzo
                </h3>
                <form id="formModificaIndirizzo" class="account-form" method="post"
                      action="${pageContext.request.contextPath}/myAccount/indirizzo/modifica" novalidate>
                    <input type="hidden" name="idIndirizzo" id="editIndirizzoId">

                    <div class="form-group full">
                        <label for="editDestinatario">Destinatario</label>
                        <input type="text" id="editDestinatario" name="destinatario" required>
                        <c:if test="${not empty erroreDestinatario}">
                            <span class="field-error">${erroreDestinatario}</span>
                        </c:if>
                    </div>
                    <div class="form-group full">
                        <label for="editVia">Via / Indirizzo</label>
                        <input type="text" id="editVia" name="via" required>
                        <c:if test="${not empty erroreVia}">
                            <span class="field-error">${erroreVia}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="editCap">CAP</label>
                        <input type="text" id="editCap" name="cap" maxlength="10" required>
                        <c:if test="${not empty erroreCap}">
                            <span class="field-error">${erroreCap}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="editCitta">Citt&agrave;</label>
                        <input type="text" id="editCitta" name="citta" required>
                        <c:if test="${not empty erroreCitta}">
                            <span class="field-error">${erroreCitta}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="editProvincia">Provincia</label>
                        <input type="text" id="editProvincia" name="provincia" maxlength="5" required>
                        <c:if test="${not empty erroreProvincia}">
                            <span class="field-error">${erroreProvincia}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="editPaese">Paese</label>
                        <input type="text" id="editPaese" name="paese" required>
                        <c:if test="${not empty errorePaese}">
                            <span class="field-error">${errorePaese}</span>
                        </c:if>
                    </div>
                    <div class="account-actions">
                        <button type="submit" class="btn-primary">
                            Salva modifiche
                        </button>
                        <button type="button" class="btn-secondary" onclick="chiudiEditIndirizzo()">
                            Annulla
                        </button>
                    </div>
                </form>
            </div>

            <a class="btn-add-addr" href="${pageContext.request.contextPath}/aggiungi-indirizzo?from=profile">
                <i class="ti ti-plus"></i> Aggiungi un nuovo indirizzo
            </a>
        </div>


        <div class="account-section" id="section-pagamento" role="tabpanel"
             aria-labelledby="tab-pagamento" hidden>
            <div class="section-header">
                <h1 class="section-title">Dati di pagamento</h1>
                <p class="section-subtitle">Il tuo metodo di pagamento predefinito</p>
            </div>

            <c:if test="${not empty metodoPagamento}">
                <div class="saved-payment-row">
                    <i class="ti ti-credit-card saved-payment-icon"></i>
                    <div class="saved-payment-info">
                        <span class="saved-payment-number"><c:out value="${metodoPagamento.numeroMascherato}"/></span>
                        <span class="saved-payment-meta"><c:out value="${metodoPagamento.nomeCarta}"/>&nbsp;&middot;&nbsp;Scad.&nbsp;<c:out
                                value="${metodoPagamento.scadenza}"/></span>
                    </div>
                </div>
                <p class="payment-edit-label">Aggiorna i dati della carta:</p>
            </c:if>
            <c:if test="${empty metodoPagamento}">
                <p class="no-data-msg">
                    <i class="ti ti-info-circle"></i> Nessun metodo di pagamento salvato.
                </p>
            </c:if>

            <form id="formPagamento" class="account-form"
                  action="${pageContext.request.contextPath}/myAccount/pagamento" method="post" novalidate>

                <div class="form-group full">
                    <label for="nomeCarta">Nome sulla carta</label>
                    <input type="text" id="nomeCarta" name="nomeCarta"
                           value="${not empty metodoPagamento ? fn:escapeXml(metodoPagamento.nomeCarta) : ''}"
                           placeholder="Es. Mario Rossi" autocomplete="cc-name" required>
                    <c:if test="${not empty erroreNomeCarta}">
                        <span class="field-error"><c:out value="${erroreNomeCarta}"/></span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="numeroCarta">Numero carta</label>
                    <input type="text" id="numeroCarta" name="numeroCarta" class="js-card-number"
                           placeholder="${fn:escapeXml(not empty metodoPagamento ? metodoPagamento.numeroMascherato : '1234 5678 9012 3456')}"
                           maxlength="19" autocomplete="cc-number">
                    <c:if test="${not empty metodoPagamento}">
                        <span class="field-hint">Lascia vuoto per mantenere la carta attuale.</span>
                    </c:if>
                    <c:if test="${not empty erroreNumeroCarta}">
                        <span class="field-error"><c:out value="${erroreNumeroCarta}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="scadenzaCarta">Scadenza</label>
                    <input type="text" id="scadenzaCarta" name="scadenza" class="js-card-expiry"
                           value="${fn:escapeXml(not empty metodoPagamento ? metodoPagamento.scadenza : '')}"
                           placeholder="MM/AA" maxlength="5" autocomplete="cc-exp" required>
                    <c:if test="${not empty erroreScadenza}">
                        <span class="field-error"><c:out value="${erroreScadenza}"/></span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="cvvInfo">CVV</label>
                    <input type="text" id="cvvInfo" value="Non memorizzato" disabled class="input-disabled"
                           aria-describedby="cvvInfoHint">
                    <span class="field-hint"
                          id="cvvInfoHint">Il CVV viene richiesto solo al momento del pagamento.</span>
                </div>

                <div class="account-actions">
                    <button class="btn-primary" type="submit">
                        ${not empty metodoPagamento ? 'Aggiorna carta' : 'Salva carta'}
                    </button>
                </div>
            </form>
        </div>

    </div>

</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
<script src="${pageContext.request.contextPath}/js/autocomplete.js"></script>
<script src="${pageContext.request.contextPath}/js/account.js"></script>

</body>
</html>
