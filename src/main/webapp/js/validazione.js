/*
 * Raccoglie funzioni di supporto per la validazione client
 * e applica i controlli ai form di registrazione e indirizzo.
 */

// controlla il formato base dell'email
function isEmailValida(value) {
    // normalizza il valore prima del controllo
    var email = value.trim();

    // blocca email troppo lunghe
    if (email.length > 100) return false;

    // blocca due punti consecutivi nella stringa
    if (email.indexOf('..') !== -1) return false;

    // richiede parte locale valida + @ + dominio + estensione finale di almeno 2 lettere
    return /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/.test(email);
}

// controlla il formato del numero di telefono
function isTelefonoValido(value) {
    // rimuove spazi e trattini prima della validazione
    var telefono = value.trim().replace(/[\s-]+/g, '');

    // accetta prefisso + opzionale + da 8 a 13 cifre
    return /^\+?\d{8,13}$/.test(telefono);
}

// controlla i requisiti minimi di robustezza della password
function isPasswordForte(value) {
    var password = value || '';

    // controlla la lunghezza minima e massima
    if (password.length < 8 || password.length > 64) return false;

    // blocca password con spazi
    if (/\s/.test(password)) return false;

    // richiede almeno una maiuscola + una minuscola + una cifra + un carattere non alfanumerico
    return /[A-Z]/.test(password) &&
        /[a-z]/.test(password) &&
        /\d/.test(password) &&
        /[^A-Za-z0-9]/.test(password);
}

// controlla il formato di nome e cognome singoli
function isNomeValido(value) {
    // normalizza il valore prima del controllo
    var nome = value.trim();

    // controlla la lunghezza consentita
    if (nome.length < 2 || nome.length > 50) return false;

    // accetta solo lettere con gruppi separati da spazio, apostrofo o trattino
    return /^[A-Za-z]+([ '\-][A-Za-z]+)*$/.test(nome);
}

// controlla che l'indirizzo contenga testo e numero civico
function isViaValida(value) {
    // normalizza il valore prima del controllo
    var via = value.trim();

    // controlla la lunghezza consentita
    if (via.length < 5 || via.length > 100) return false;

    // stessa restrizione di caratteri applicata lato server (lettere, cifre, spazi e punteggiatura comune)
    if (!/^[A-Za-z0-9 .,'\/\-]+$/.test(via)) return false;

    // richiede almeno una lettera + almeno una cifra
    return /[A-Za-z]/.test(via) && /\d/.test(via);
}

// controlla che il cap abbia esattamente 5 cifre
function isCapValido(value) {
    return /^\d{5}$/.test(value.trim());
}

// controlla il formato della provincia
function isProvinciaValida(value) {
    // accetta solo lettere senza spazi o simboli, da 2 a 5 caratteri
    return /^[A-Za-z]{2,5}$/.test(value.trim());
}

// controlla il formato di città e paese
function isLocalitaValida(value) {
    // normalizza il valore prima del controllo
    var localita = value.trim();

    // controlla la lunghezza consentita
    if (localita.length < 2 || localita.length > 100) return false;

    // accetta solo lettere con gruppi separati da spazio, apostrofo o trattino
    return /^[A-Za-z]+([ '\-][A-Za-z]+)*$/.test(localita);
}

// controlla che il destinatario contenga almeno nome e cognome
function isDestinatarioValido(value) {
    // normalizza il valore prima del controllo
    var destinatario = value.trim();

    // controlla la lunghezza consentita
    if (destinatario.length < 4 || destinatario.length > 100) return false;

    // richiede almeno due gruppi di parole separati da almeno uno spazio
    return /^[A-Za-z]+([ '\-][A-Za-z]+)*\s+[A-Za-z]+([ '\-][A-Za-z]+)*$/.test(destinatario);
}

// controlla che il nome sulla carta contenga almeno nome e cognome
function isNomeCartaValido(value) {
    // normalizza il valore prima del controllo
    var nomeCarta = value.trim();

    // controlla la lunghezza consentita
    if (nomeCarta.length < 3 || nomeCarta.length > 26) return false;

    // richiede almeno due gruppi di parole separati da almeno uno spazio
    return /^[A-Za-z]+([ '\-][A-Za-z]+)*\s+[A-Za-z]+([ '\-][A-Za-z]+)*$/.test(nomeCarta);
}

// controlla che il numero carta abbia 16 cifre effettive
function isNumeroCartaValido(value) {
    // accetta solo 16 cifre dopo la rimozione di spazi e trattini
    return /^\d{16}$/.test(value.trim().replace(/[\s-]+/g, ''));
}

// controlla il formato del cvv
function isCvvValido(value) {
    return /^\d{3,4}$/.test(value.trim());
}

// controlla il formato e la validità temporale della scadenza carta
function isScadenzaCartaValida(value) {
    // normalizza il valore prima del controllo
    var scadenza = value.trim();

    // richiede esattamente 2 cifre + / + 2 cifre
    if (!/^\d{2}\/\d{2}$/.test(scadenza)) return false;

    // estrae mese e anno dal valore inserito
    var mese = parseInt(scadenza.slice(0, 2), 10);
    var anno = 2000 + parseInt(scadenza.slice(3), 10);

    // blocca mesi fuori intervallo
    if (mese < 1 || mese > 12) return false;

    var oggi = new Date();

    // blocca carte già scadute
    if (anno < oggi.getFullYear()) return false;
    if (anno === oggi.getFullYear() && mese < oggi.getMonth() + 1) return false;

    return true;
}

// controlla che la data inserita corrisponda a un utente maggiorenne
function isMaggiorenneData(value) {
    // normalizza il valore prima del controllo
    var testo = value.trim();

    // blocca il valore vuoto
    if (!testo) return false;

    // converte il testo in data e controlla che sia valida
    var nascita = new Date(testo);
    if (isNaN(nascita.getTime())) return false;

    // calcola l'età e controlla il minimo
    var eta = new Date().getFullYear() - nascita.getFullYear();
    return eta >= 18;
}

// applica la validazione client ai form quando la pagina è pronta
document.addEventListener('DOMContentLoaded', function () {
    // recupera il form di login
    var formLogin = document.getElementById('formLogin');

    // applica la validazione client solo se il form è presente
    if (formLogin) {
        formLogin.addEventListener('submit', function (e) {
            // rimuove eventuali errori client del tentativo precedente
            var erroriPrecedenti = formLogin.querySelectorAll('.field-error.js-err');
            for (var n = 0; n < erroriPrecedenti.length; n++) {
                erroriPrecedenti[n].remove();
            }

            var valid = true;

            function mostraErrore(inputId, message) {
                var input = document.getElementById(inputId);
                if (!input) return;
                var group = input.closest('.form-group') || input.parentNode;
                var error = document.createElement('span');
                error.className = 'field-error js-err';
                error.textContent = message;
                group.appendChild(error);
                valid = false;
            }

            var email = document.getElementById('email').value.trim();
            var password = document.getElementById('password').value;

            // controlla presenza e formato dell'email
            if (!email) {
                mostraErrore('email', 'Campo obbligatorio');
            } else if (!isEmailValida(email)) {
                mostraErrore('email', 'Formato email non valido');
            }

            // controlla che la password non sia vuota
            if (!password) {
                mostraErrore('password', 'Campo obbligatorio');
            }

            // blocca l'invio del form in presenza di errori
            if (!valid) {
                e.preventDefault();
            }
        });
    }

    // recupera il form di registrazione
    var formRegistrazione = document.getElementById('formRegistrazione');

    // applica la validazione client solo se il form è presente
    if (formRegistrazione) {
        formRegistrazione.addEventListener('submit', function (e) {
            // rimuove gli errori del tentativo precedente
            var erroriPrecedenti = formRegistrazione.querySelectorAll('.field-error');
            for (var n = 0; n < erroriPrecedenti.length; n++) {
                erroriPrecedenti[n].remove();
            }

            // inizializza il flag usato per decidere l'invio del form
            var valid = true;

            // mostra l'errore sul campo richiesto e aggiorna lo stato del form
            function mostraErrore(nomeCampo, message) {
                var input = formRegistrazione.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;

                var group = input.closest('.form-group') || input.parentNode;
                var error = document.createElement('span');
                error.className = 'field-error';
                error.textContent = message;
                group.appendChild(error);
                valid = false;
            }

            // recupera e normalizza i valori da controllare
            var nome = formRegistrazione.querySelector('[name="nome"]').value.trim();
            var cognome = formRegistrazione.querySelector('[name="cognome"]').value.trim();
            var email = formRegistrazione.querySelector('[name="email"]').value.trim();
            var password = formRegistrazione.querySelector('[name="password"]').value;
            var telefono = formRegistrazione.querySelector('[name="telefono"]').value;
            var dataNascita = formRegistrazione.querySelector('[name="dataNascita"]').value;

            // controlla presenza e formato del nome
            if (!nome) {
                mostraErrore('nome', 'Campo obbligatorio');
            } else if (!isNomeValido(nome)) {
                mostraErrore('nome', 'Il nome deve avere 2-50 caratteri e contenere lettere reali.');
            }

            // controlla presenza e formato del cognome
            if (!cognome) {
                mostraErrore('cognome', 'Campo obbligatorio');
            } else if (!isNomeValido(cognome)) {
                mostraErrore('cognome', 'Il cognome deve avere 2-50 caratteri e contenere lettere reali.');
            }

            // controlla presenza e formato dell'email
            if (!email) {
                mostraErrore('email', 'Campo obbligatorio');
            } else if (!isEmailValida(email)) {
                mostraErrore('email', 'Formato email non valido');
            }

            // controlla presenza e robustezza della password
            if (!password.trim()) {
                mostraErrore('password', 'Campo obbligatorio');
            } else if (!isPasswordForte(password)) {
                mostraErrore('password', 'Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo');
            }

            // controlla presenza e formato del telefono
            if (!telefono.trim()) {
                mostraErrore('telefono', 'Campo obbligatorio');
            } else if (!isTelefonoValido(telefono)) {
                mostraErrore('telefono', 'Numero di telefono non valido');
            }

            // controlla presenza della data e maggiore età
            if (!dataNascita) {
                mostraErrore('dataNascita', 'Campo obbligatorio');
            } else if (!isMaggiorenneData(dataNascita)) {
                mostraErrore('dataNascita', 'Devi avere almeno 18 anni');
            }

            // blocca l'invio del form in presenza di errori
            if (!valid) {
                e.preventDefault();
            }
        });
    }

    // recupera il form indirizzo
    var formIndirizzo = document.getElementById('formIndirizzo');

    // applica la validazione client solo se il form è presente
    if (formIndirizzo) {
        formIndirizzo.addEventListener('submit', function (e) {
            // rimuove gli errori del tentativo precedente
            var erroriPrecedenti = formIndirizzo.querySelectorAll('.field-error');
            for (var n = 0; n < erroriPrecedenti.length; n++) {
                erroriPrecedenti[n].remove();
            }

            // inizializza il flag usato per decidere l'invio del form
            var valid = true;

            // mostra l'errore sul campo richiesto e aggiorna lo stato del form
            function mostraErrore(nomeCampo, message) {
                var input = formIndirizzo.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;

                var group = input.closest('.form-group') || input.parentNode;
                var error = document.createElement('span');
                error.className = 'field-error';
                error.textContent = message;
                group.appendChild(error);
                valid = false;
            }

            // recupera e normalizza i valori da controllare
            var destinatario = formIndirizzo.querySelector('[name="destinatario"]').value.trim();
            var via = formIndirizzo.querySelector('[name="via"]').value.trim();
            var cap = formIndirizzo.querySelector('[name="cap"]').value.trim();
            var citta = formIndirizzo.querySelector('[name="citta"]').value.trim();
            var provincia = formIndirizzo.querySelector('[name="provincia"]').value.trim();
            var paese = formIndirizzo.querySelector('[name="paese"]').value.trim();

            // controlla presenza e formato del destinatario
            if (!destinatario) {
                mostraErrore('destinatario', 'Campo obbligatorio');
            } else if (!isDestinatarioValido(destinatario)) {
                mostraErrore('destinatario', 'Inserisci nome e cognome del destinatario');
            }

            // controlla presenza e completezza dell'indirizzo
            if (!via) {
                mostraErrore('via', 'Campo obbligatorio');
            } else if (!isViaValida(via)) {
                mostraErrore('via', 'Inserisci un indirizzo completo di numero civico');
            }

            // controlla presenza e formato del cap
            if (!cap) {
                mostraErrore('cap', 'Campo obbligatorio');
            } else if (!isCapValido(cap)) {
                mostraErrore('cap', 'Il CAP deve essere di 5 cifre');
            }

            // controlla presenza e formato della città
            if (!citta) {
                mostraErrore('citta', 'Campo obbligatorio');
            } else if (!isLocalitaValida(citta)) {
                mostraErrore('citta', 'La città deve contenere lettere reali');
            }

            // controlla presenza e formato della provincia
            if (!provincia) {
                mostraErrore('provincia', 'Campo obbligatorio');
            } else if (!isProvinciaValida(provincia)) {
                mostraErrore('provincia', 'La provincia deve avere 2-5 lettere');
            }

            // controlla presenza e formato del paese
            if (!paese) {
                mostraErrore('paese', 'Campo obbligatorio');
            } else if (!isLocalitaValida(paese)) {
                mostraErrore('paese', 'Il paese deve contenere lettere reali');
            }

            // blocca l'invio del form in presenza di errori
            if (!valid) {
                e.preventDefault();
            }
        });
    }
});
