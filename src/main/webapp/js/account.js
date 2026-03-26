/*
 * Gestione tab account, form indirizzo inline e validazione client dei form principali.
 */

// seleziona tab e sezioni associate
var tabBtns = document.querySelectorAll('.tab-btn');
var sections = document.querySelectorAll('.account-section');

// attiva la sezione richiesta e disattiva le altre
function mostraSezione(sectionId) {
    // aggiorna lo stato attivo dei pulsanti
    tabBtns.forEach(function (tab) {
        tab.classList.toggle('active', tab.dataset.section === sectionId);
    });

    // mostra solo la sezione collegata alla tab corrente
    sections.forEach(function (section) {
        var attiva = section.id === 'section-' + sectionId;
        section.classList.toggle('active', attiva);
        section.hidden = !attiva;
    });
}

// cambia tab e chiude l'eventuale form indirizzo aperto
tabBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
        mostraSezione(btn.dataset.section);
        chiudiEditIndirizzo();
    });
});

// determina la tab iniziale dando priorità alla URL e poi al server
var wrap = document.querySelector('.account-wrap');
var tabAttiva = wrap ? wrap.dataset.tab : '';
var urlParams = new URLSearchParams(window.location.search);
var sezioneAttiva = urlParams.get('section') || tabAttiva;

// apre la sezione iniziale se presente
if (sezioneAttiva) {
    mostraSezione(sezioneAttiva);
}

// recupera gli elementi usati dal form indirizzo inline
var indirizzoFormWrap = document.getElementById('indirizzoFormWrap');
var formIndirizzo = document.getElementById('formIndirizzo');

// prepara il form per l'inserimento di un nuovo indirizzo
function apriNuovoIndirizzo() {
    // interrompe l'esecuzione se il form non è presente
    if (!formIndirizzo) return;

    // resetta i campi e svuota l'id dell'indirizzo
    formIndirizzo.reset();
    document.getElementById('indirizzoId').value = '';

    // imposta titolo, pulsante e action per il nuovo inserimento
    apriFormIndirizzo(
        'Nuovo indirizzo',
        'Salva indirizzo',
        indirizzoFormWrap.dataset.actionNuovo
    );
}

// carica i dati della card nel form e apre la modalità modifica
function apriEditIndirizzo(card) {
    // copia nel form i valori presenti nei data-attribute
    document.getElementById('indirizzoId').value = card.dataset.id;
    document.getElementById('destinatario').value = card.dataset.destinatario;
    document.getElementById('via').value = card.dataset.via;
    document.getElementById('cap').value = card.dataset.cap;
    document.getElementById('citta').value = card.dataset.citta;
    document.getElementById('provincia').value = card.dataset.provincia;
    document.getElementById('paese').value = card.dataset.paese;

    // imposta il form in modalità modifica
    apriFormIndirizzo(
        'Modifica indirizzo',
        'Salva modifiche',
        indirizzoFormWrap.dataset.actionModifica
    );
}

// chiude il form indirizzo
function chiudiEditIndirizzo() {
    chiudiFormIndirizzo();
}

// riapre il form in modifica dopo un redirect con errore
if (indirizzoFormWrap && indirizzoFormWrap.dataset.apriEdit === 'true') {
    apriFormIndirizzo(
        'Modifica indirizzo',
        'Salva modifiche',
        indirizzoFormWrap.dataset.actionModifica
    );
}

// recupera il form per il cambio password
var formPassword = document.getElementById('formPassword');

// applica la validazione client al cambio password
if (formPassword) {
    formPassword.addEventListener('submit', function (e) {
        // rimuove eventuali errori mostrati in precedenza
        formPassword.querySelectorAll('.field-error').forEach(function (el) {
            el.remove();
        });

        var valid = true;

        // aggiunge il messaggio di errore sotto il campo non valido
        function mostraErrorePassword(inputId, message) {
            var input = document.getElementById(inputId);
            if (!input) return;

            var group = input.closest('.form-group') || input.parentNode;
            var error = document.createElement('span');
            error.className = 'field-error';
            error.textContent = message;
            group.appendChild(error);
            valid = false;
        }

        // recupera i valori inseriti nel form
        var attuale = document.getElementById('passwordAttuale').value;
        var nuova = document.getElementById('nuovaPassword').value;
        var conferma = document.getElementById('confermaPassword').value;

        // controlla la presenza della password attuale
        if (!attuale) {
            mostraErrorePassword('passwordAttuale', 'Inserisci la password attuale.');
        }

        // controlla presenza e formato della nuova password
        if (!nuova) {
            mostraErrorePassword('nuovaPassword', 'Inserisci una nuova password.');
        } else if (!isPasswordForte(nuova)) {
            mostraErrorePassword(
                'nuovaPassword',
                'Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo, senza spazi.'
            );
        }

        // controlla la corrispondenza tra nuova password e conferma
        if (nuova && nuova !== conferma) {
            mostraErrorePassword('confermaPassword', 'Le password non coincidono.');
        }

        // blocca l'invio del form in presenza di errori
        if (!valid) {
            e.preventDefault();
        }
    });
}

// recupera il form dei dati personali
var formDatiPersonali = document.getElementById('formDatiPersonali');

// applica la validazione client ai dati personali
if (formDatiPersonali) {
    formDatiPersonali.addEventListener('submit', function (e) {
        // rimuove eventuali errori del tentativo precedente
        formDatiPersonali.querySelectorAll('.field-error').forEach(function (el) {
            el.remove();
        });

        var valid = true;

        // aggiunge il messaggio di errore sotto il campo non valido
        function mostraErroreDati(inputId, message) {
            var input = document.getElementById(inputId);
            if (!input) return;

            var group = input.closest('.form-group') || input.parentNode;
            var error = document.createElement('span');
            error.className = 'field-error';
            error.textContent = message;
            group.appendChild(error);
            valid = false;
        }

        // recupera e normalizza i valori da controllare
        var nome = normalizeText(document.getElementById('nome').value);
        var cognome = normalizeText(document.getElementById('cognome').value);
        var email = normalizeText(document.getElementById('email').value);
        var telefono = document.getElementById('telefono').value;
        var dataNascita = document.getElementById('dataDiNascita').value;

        // controlla presenza e formato del nome
        if (!nome) {
            mostraErroreDati('nome', 'Il nome è obbligatorio.');
        } else if (!isNomeValido(nome)) {
            mostraErroreDati('nome', 'Il nome deve avere 2-50 caratteri e contenere lettere reali.');
        }

        // controlla presenza e formato del cognome
        if (!cognome) {
            mostraErroreDati('cognome', 'Il cognome è obbligatorio.');
        } else if (!isNomeValido(cognome)) {
            mostraErroreDati('cognome', 'Il cognome deve avere 2-50 caratteri e contenere lettere reali.');
        }

        // controlla presenza e formato dell'email
        if (!email) {
            mostraErroreDati('email', "L'email è obbligatoria.");
        } else if (!isEmailValida(email)) {
            mostraErroreDati('email', 'Formato email non valido.');
        }

        // controlla presenza e formato del telefono
        if (!normalizeText(telefono)) {
            mostraErroreDati('telefono', 'Il telefono è obbligatorio.');
        } else if (!isTelefonoValido(telefono)) {
            mostraErroreDati('telefono', 'Numero di telefono non valido.');
        }

        // controlla presenza della data e maggiore età
        if (!dataNascita) {
            mostraErroreDati('dataDiNascita', 'La data di nascita è obbligatoria.');
        } else if (!isMaggiorenneData(dataNascita)) {
            mostraErroreDati('dataDiNascita', 'Devi avere almeno 18 anni.');
        }

        // blocca l'invio del form in presenza di errori
        if (!valid) {
            e.preventDefault();
        }
    });
}