<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
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
    <div class="account-wrap" data-tab="${tabAttiva}">


        <div class="account-hero">
            <div class="account-hero-avatar"><i class="ti ti-user"></i></div>
            <div class="account-hero-info">
                <span class="account-hero-name"><c:out value="${utente.nome}"/> <c:out
                        value="${utente.cognome}"/></span>
                <span class="account-hero-email"><c:out value="${utente.email}"/></span>
            </div>
        </div>


        <div class="account-tabs" role="tablist">
            <button class="tab-btn active" data-section="dati-personali" role="tab">
                <i class="ti ti-user-circle"></i>
                <span>Dati personali</span>
            </button>
            <button class="tab-btn" data-section="password" role="tab">
                <i class="ti ti-lock"></i>
                <span>Sicurezza</span>
            </button>
            <button class="tab-btn" data-section="indirizzo" role="tab">
                <i class="ti ti-map-pin"></i>
                <span>Indirizzi</span>
            </button>
            <button class="tab-btn" data-section="pagamento" role="tab">
                <i class="ti ti-credit-card"></i>
                <span>Pagamento</span>
            </button>
        </div>


        <div class="account-section active" id="section-dati-personali" role="tabpanel">
            <div class="section-header">
                <h1 class="section-title">Dati personali</h1>
                <p class="section-subtitle">Gestisci le informazioni del tuo profilo</p>
            </div>

            <form id="formDatiPersonali" class="account-form"
                  action="${pageContext.request.contextPath}/myAccount/datiPersonali" method="post" novalidate>

                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome"
                           value="${not empty formNome ? formNome : utente.nome}" required>
                    <c:if test="${not empty erroreNome}">
                        <span class="field-error">${erroreNome}</span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome"
                           value="${not empty formCognome ? formCognome : utente.cognome}" required>
                    <c:if test="${not empty erroreCognome}">
                        <span class="field-error">${erroreCognome}</span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email"
                           value="${not empty formEmail ? formEmail : utente.email}" required>
                    <c:if test="${not empty erroreEmail}">
                        <span class="field-error">${erroreEmail}</span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono"
                           value="${not empty formTelefono ? formTelefono : utente.telefono}">
                    <c:if test="${not empty erroreTelefono}">
                        <span class="field-error">${erroreTelefono}</span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="dataDiNascita">Data di nascita</label>
                    <input type="date" id="dataDiNascita" name="dataDiNascita"
                           value="${not empty formDataNascita ? formDataNascita : utente.dataDiNascita}">
                    <c:if test="${not empty erroreDataNascita}">
                        <span class="field-error">${erroreDataNascita}</span>
                    </c:if>
                </div>

                <div class="account-actions">
                    <button class="btn-primary" type="submit">
                        Salva modifiche
                    </button>
                </div>
            </form>
        </div>


        <div class="account-section" id="section-password" role="tabpanel">
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
                        <span class="field-error">${errorePasswordAttuale}</span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="nuovaPassword">Nuova password</label>
                    <input type="password" id="nuovaPassword" name="nuovaPassword" required autocomplete="new-password"
                           minlength="8" maxlength="64">
                    <span class="field-hint">8-64 caratteri, con maiuscola, minuscola, numero e simbolo, senza spazi.</span>
                    <c:if test="${not empty erroreNuovaPassword}">
                        <span class="field-error">${erroreNuovaPassword}</span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="confermaPassword">Conferma nuova password</label>
                    <input type="password" id="confermaPassword" name="confermaPassword" required
                           autocomplete="new-password">
                    <c:if test="${not empty erroreConfermaPassword}">
                        <span class="field-error">${erroreConfermaPassword}</span>
                    </c:if>
                </div>

                <div class="account-actions">
                    <button class="btn-primary" type="submit">
                        <i class="ti ti-key"></i> Aggiorna password
                    </button>
                </div>
            </form>
        </div>


        <div class="account-section" id="section-indirizzo" role="tabpanel">
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
                                    <span class="addr-destinatario">${ind.destinatario}</span>
                                    <span class="addr-detail">${ind.via}</span>
                                    <span class="addr-detail">
                                        ${ind.cap} ${ind.citta} (${ind.provincia}), ${ind.paese}
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


        <div class="account-section" id="section-pagamento" role="tabpanel">
            <div class="section-header">
                <h1 class="section-title">Dati di pagamento</h1>
                <p class="section-subtitle">Il tuo metodo di pagamento predefinito</p>
            </div>

            <c:if test="${not empty metodoPagamento}">
                <div class="saved-payment-row">
                    <i class="ti ti-credit-card saved-payment-icon"></i>
                    <div class="saved-payment-info">
                        <span class="saved-payment-number">${metodoPagamento.numeroMascherato}</span>
                        <span class="saved-payment-meta">${metodoPagamento.nomeCarta}&nbsp;&middot;&nbsp;Scad.&nbsp;${metodoPagamento.scadenza}</span>
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
                        <span class="field-error">${erroreNomeCarta}</span>
                    </c:if>
                </div>

                <div class="form-group full">
                    <label for="numeroCarta">Numero carta</label>
                    <input type="text" id="numeroCarta" name="numeroCarta" class="js-card-number"
                           placeholder="${not empty metodoPagamento ? metodoPagamento.numeroMascherato : '1234 5678 9012 3456'}"
                           maxlength="19" autocomplete="cc-number">
                    <c:if test="${not empty metodoPagamento}">
                        <span class="field-hint">Lascia vuoto per mantenere la carta attuale.</span>
                    </c:if>
                    <c:if test="${not empty erroreNumeroCarta}">
                        <span class="field-error">${erroreNumeroCarta}</span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="scadenzaCarta">Scadenza</label>
                    <input type="text" id="scadenzaCarta" name="scadenza" class="js-card-expiry"
                           value="${not empty metodoPagamento ? metodoPagamento.scadenza : ''}"
                           placeholder="MM/AA" maxlength="5" autocomplete="cc-exp" required>
                    <c:if test="${not empty erroreScadenza}">
                        <span class="field-error">${erroreScadenza}</span>
                    </c:if>
                </div>

                <div class="form-group">
                    <label>CVV</label>
                    <input type="text" value="Non memorizzato" disabled class="input-disabled">
                    <span class="field-hint">Il CVV viene richiesto solo al momento del pagamento.</span>
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
<script src="${pageContext.request.contextPath}/js/account.js"></script>

</body>
</html>
