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
    <div class="alert alert-success">
        <i class="ti ti-circle-check"></i>&nbsp; Modifiche salvate con successo.
    </div>
</c:if>

<main>
    <div class="account-wrap" data-tab="${fn:escapeXml(tabAttiva)}">


        <h1 class="account-page-title page-title">Profilo</h1>


        <div class="account-tabs">
            <button class="tab-btn active" type="button" data-section="dati-personali">
                <i class="ti ti-user-circle"></i>
                <span>Dati personali</span>
            </button>
            <button class="tab-btn" type="button" data-section="password">
                <i class="ti ti-lock"></i>
                <span>Sicurezza</span>
            </button>
            <button class="tab-btn" type="button" data-section="indirizzo">
                <i class="ti ti-map-pin"></i>
                <span>Indirizzi</span>
            </button>
        </div>


        <div class="account-section page-card active" id="section-dati-personali">
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


        <div class="account-section page-card" id="section-password" hidden>
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


        <div class="account-section page-card" id="section-indirizzo" hidden>
            <div class="section-header">
                <h1 class="section-title">Indirizzo di spedizione</h1>
                <p class="section-subtitle">Gestisci i tuoi indirizzi di consegna</p>
            </div>


            <c:if test="${not empty indirizzi}">
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
                                      class="form-inline-del">
                                    <input type="hidden" name="idIndirizzo" value="${ind.id}">
                                    <button type="submit" class="btn-addr-del"
                                            data-confirm="Eliminare questo indirizzo?">
                                        Elimina
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>


            <button type="button" class="btn-aggiungi-indirizzo" onclick="apriNuovoIndirizzo()">
                Aggiungi un nuovo indirizzo
            </button>

            <div class="profilo-edit-section" id="indirizzoFormWrap"
                 data-action-nuovo="${pageContext.request.contextPath}/aggiungi-indirizzo"
                 data-action-modifica="${pageContext.request.contextPath}/myAccount/indirizzo/modifica"
                 data-apri-edit="${not empty apriEditIndirizzo}">
                <h3 class="edit-section-title" id="indirizzoFormTitolo">Nuovo indirizzo</h3>
                <form id="formIndirizzo" class="account-form" method="post"
                      action="${pageContext.request.contextPath}/aggiungi-indirizzo" novalidate>
                    <input type="hidden" name="idIndirizzo" id="indirizzoId">
                    <input type="hidden" name="from" value="profile">

                    <div class="form-group full">
                        <label for="destinatario">Destinatario</label>
                        <input type="text" id="destinatario" name="destinatario"
                               placeholder="Nome e cognome del destinatario" required>
                        <c:if test="${not empty erroreDestinatario}">
                            <span class="field-error">${erroreDestinatario}</span>
                        </c:if>
                    </div>
                    <div class="form-group full">
                        <label for="via">Via / Indirizzo</label>
                        <input type="text" id="via" name="via"
                               placeholder="Es. Via Roma 12" required>
                        <c:if test="${not empty erroreVia}">
                            <span class="field-error">${erroreVia}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="cap">CAP</label>
                        <input type="text" id="cap" name="cap"
                               placeholder="Es. 20100" maxlength="10" required>
                        <c:if test="${not empty erroreCap}">
                            <span class="field-error">${erroreCap}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="citta">Citt&agrave;</label>
                        <input type="text" id="citta" name="citta"
                               placeholder="Es. Milano" required>
                        <c:if test="${not empty erroreCitta}">
                            <span class="field-error">${erroreCitta}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="provincia">Provincia</label>
                        <input type="text" id="provincia" name="provincia"
                               placeholder="Es. MI" maxlength="5" list="list-province" required>
                        <datalist id="list-province"></datalist>
                        <c:if test="${not empty erroreProvincia}">
                            <span class="field-error">${erroreProvincia}</span>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="paese">Paese</label>
                        <input type="text" id="paese" name="paese"
                               placeholder="Es. Italia" list="list-nazioni" required>
                        <datalist id="list-nazioni"></datalist>
                        <c:if test="${not empty errorePaese}">
                            <span class="field-error">${errorePaese}</span>
                        </c:if>
                    </div>
                    <div class="account-actions">
                        <button type="submit" class="btn-primary" id="btnSalvaIndirizzo">Salva indirizzo</button>
                        <button type="button" class="btn-secondary" id="btnAnnullaEdit"
                                onclick="chiudiEditIndirizzo()" hidden>Annulla
                        </button>
                    </div>
                </form>
            </div>
        </div>


    </div>

</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/validazione.js"></script>
<script src="${pageContext.request.contextPath}/js/autocomplete.js"></script>
<script src="${pageContext.request.contextPath}/js/account.js"></script>

</body>
</html>
