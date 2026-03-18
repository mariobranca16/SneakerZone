(function () {
    'use strict';

    var inputValidation = window.ValidazioneInput;
    var tabBtns = document.querySelectorAll('.tab-btn');
    var sections = document.querySelectorAll('.account-section');

    function mostraSezione(sectionId) {
        tabBtns.forEach(function (b) {
            b.classList.toggle('active', b.dataset.section === sectionId);
        });
        sections.forEach(function (s) {
            var attiva = s.id === 'section-' + sectionId;
            s.classList.toggle('active', attiva);
            s.hidden = !attiva;
        });
    }

    tabBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            mostraSezione(btn.dataset.section);
            chiudiEditIndirizzo();
        });
    });

    var wrap = document.querySelector('.account-wrap');
    var tabAttiva = wrap ? wrap.dataset.tab : '';
    var urlParams = new URLSearchParams(window.location.search);
    var sectionParam = urlParams.get('section') || tabAttiva;
    if (sectionParam) mostraSezione(sectionParam);

    var editWrap = document.getElementById('editIndirizzoWrap');
    if (editWrap && editWrap.dataset.apriEdit === 'true') {
        editWrap.classList.add('open');
    }

    window.apriEditIndirizzo = function (card) {
        document.getElementById('editIndirizzoId').value = card.dataset.id;
        document.getElementById('editDestinatario').value = card.dataset.destinatario;
        document.getElementById('editVia').value = card.dataset.via;
        document.getElementById('editCap').value = card.dataset.cap;
        document.getElementById('editCitta').value = card.dataset.citta;
        document.getElementById('editProvincia').value = card.dataset.provincia;
        document.getElementById('editPaese').value = card.dataset.paese;
        var currentEditWrap = document.getElementById('editIndirizzoWrap');
        currentEditWrap.classList.add('open');
        currentEditWrap.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    };

    window.chiudiEditIndirizzo = function () {
        var currentEditWrap = document.getElementById('editIndirizzoWrap');
        if (currentEditWrap) currentEditWrap.classList.remove('open');
    };

    if (!inputValidation) {
        return;
    }

    var formPassword = document.getElementById('formPassword');
    if (formPassword) {
        formPassword.addEventListener('submit', function (e) {
            formPassword.querySelectorAll('.field-error').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            function mostraErrorePassword(inputId, messaggio) {
                var input = document.getElementById(inputId);
                if (!input) return;
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
                valido = false;
            }

            var attuale = document.getElementById('passwordAttuale').value;
            var nuova = document.getElementById('nuovaPassword').value;
            var conferma = document.getElementById('confermaPassword').value;

            if (!attuale) mostraErrorePassword('passwordAttuale', 'Inserisci la password attuale.');
            if (!nuova) {
                mostraErrorePassword('nuovaPassword', 'Inserisci una nuova password.');
            } else if (!inputValidation.isPasswordForte(nuova)) {
                mostraErrorePassword('nuovaPassword', 'Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo, senza spazi.');
            }
            if (nuova && nuova !== conferma) mostraErrorePassword('confermaPassword', 'Le password non coincidono.');
            if (!valido) e.preventDefault();
        });
    }

    var formDatiPersonali = document.getElementById('formDatiPersonali');
    if (formDatiPersonali) {
        formDatiPersonali.addEventListener('submit', function (e) {
            formDatiPersonali.querySelectorAll('.field-error').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            function mostraErroreDati(inputId, messaggio) {
                var input = document.getElementById(inputId);
                if (!input) return;
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
                valido = false;
            }

            var nome = inputValidation.normalizeText(document.getElementById('nome').value);
            var cognome = inputValidation.normalizeText(document.getElementById('cognome').value);
            var email = inputValidation.normalizeText(document.getElementById('email').value);
            var telefono = document.getElementById('telefono').value;
            var dataNasc = document.getElementById('dataDiNascita').value;

            if (!nome) {
                mostraErroreDati('nome', 'Il nome e obbligatorio.');
            } else if (!inputValidation.isNomeValido(nome)) {
                mostraErroreDati('nome', 'Il nome deve avere 2-50 caratteri e contenere lettere reali.');
            }
            if (!cognome) {
                mostraErroreDati('cognome', 'Il cognome e obbligatorio.');
            } else if (!inputValidation.isNomeValido(cognome)) {
                mostraErroreDati('cognome', 'Il cognome deve avere 2-50 caratteri e contenere lettere reali.');
            }
            if (!email) {
                mostraErroreDati('email', "L'email e obbligatoria.");
            } else if (!inputValidation.isEmailValida(email)) {
                mostraErroreDati('email', 'Formato email non valido.');
            }
            if (!inputValidation.normalizeText(telefono)) {
                mostraErroreDati('telefono', 'Il telefono e obbligatorio.');
            } else if (!inputValidation.isTelefonoValido(telefono)) {
                mostraErroreDati('telefono', 'Numero di telefono non valido.');
            }
            if (!dataNasc) {
                mostraErroreDati('dataDiNascita', 'La data di nascita e obbligatoria.');
            } else if (!inputValidation.isMaggiorenneData(dataNasc)) {
                mostraErroreDati('dataDiNascita', 'Devi avere almeno 18 anni.');
            }
            if (!valido) e.preventDefault();
        });
    }

    var formModificaIndirizzo = document.getElementById('formModificaIndirizzo');
    if (formModificaIndirizzo) {
        formModificaIndirizzo.addEventListener('submit', function (e) {
            formModificaIndirizzo.querySelectorAll('.field-error').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            function mostraErroreIndirizzo(inputId, messaggio) {
                var input = document.getElementById(inputId);
                if (!input) return;
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
                valido = false;
            }

            var destinatario = inputValidation.normalizeText(document.getElementById('editDestinatario').value);
            var via = inputValidation.normalizeText(document.getElementById('editVia').value);
            var cap = inputValidation.normalizeText(document.getElementById('editCap').value);
            var citta = inputValidation.normalizeText(document.getElementById('editCitta').value);
            var provincia = inputValidation.normalizeText(document.getElementById('editProvincia').value);
            var paese = inputValidation.normalizeText(document.getElementById('editPaese').value);

            if (!destinatario) {
                mostraErroreIndirizzo('editDestinatario', 'Il destinatario e obbligatorio.');
            } else if (!inputValidation.isDestinatarioValido(destinatario)) {
                mostraErroreIndirizzo('editDestinatario', 'Inserisci nome e cognome del destinatario.');
            }
            if (!via) {
                mostraErroreIndirizzo('editVia', "L'indirizzo e obbligatorio.");
            } else if (!inputValidation.isViaValida(via)) {
                mostraErroreIndirizzo('editVia', 'Inserisci un indirizzo completo di numero civico (es. Via Roma 1).');
            }
            if (!cap) {
                mostraErroreIndirizzo('editCap', 'Il CAP e obbligatorio.');
            } else if (!inputValidation.isCapValido(cap)) {
                mostraErroreIndirizzo('editCap', 'Il CAP deve essere di 5 cifre.');
            }
            if (!citta) {
                mostraErroreIndirizzo('editCitta', 'La citta e obbligatoria.');
            } else if (!inputValidation.isLocalitaValida(citta)) {
                mostraErroreIndirizzo('editCitta', 'La citta deve contenere lettere reali.');
            }
            if (!provincia) {
                mostraErroreIndirizzo('editProvincia', 'La provincia e obbligatoria.');
            } else if (!inputValidation.isProvinciaValida(provincia)) {
                mostraErroreIndirizzo('editProvincia', 'La provincia deve avere 2-5 lettere (es. RM).');
            }
            if (!paese) {
                mostraErroreIndirizzo('editPaese', 'Il paese e obbligatorio.');
            } else if (!inputValidation.isLocalitaValida(paese)) {
                mostraErroreIndirizzo('editPaese', 'Il paese deve contenere lettere reali.');
            }
            if (!valido) e.preventDefault();
        });
    }

})();
