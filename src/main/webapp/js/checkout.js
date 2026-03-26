/*
 * Gestisce la selezione degli indirizzi nel checkout,
 * il form inline per nuovo indirizzo o modifica
 * e la validazione dei dati di pagamento.
 */

document.addEventListener('DOMContentLoaded', function () {
    // recupera gli elementi principali usati nella pagina checkout
    var indirizzoFormWrap = document.getElementById('indirizzoFormWrap');
    var formIndirizzo = document.getElementById('formIndirizzo');
    var formCheckout = document.querySelector('.checkout-form');

    // espone la funzione globale per selezionare un indirizzo di spedizione
    window.selezionaIndirizzoCheckout = function (card) {
        // recupera tutte le card indirizzo mostrate nella pagina
        var cards = document.querySelectorAll('.profilo-addr-card');

        // rimuove la selezione precedente da tutte le card
        for (var i = 0; i < cards.length; i++) {
            cards[i].classList.remove('selected');

            // deseleziona anche l'eventuale radio associato
            var radio = cards[i].querySelector('.addr-radio');
            if (radio) {
                radio.checked = false;
            }
        }

        // interrompe il flusso se non è stata passata alcuna card
        if (!card) return;

        // applica lo stato selezionato alla card corrente
        card.classList.add('selected');

        // seleziona il radio collegato alla card scelta
        var radioSelezionato = card.querySelector('.addr-radio');
        if (radioSelezionato) {
            radioSelezionato.checked = true;
        }

        // recupera i campi nascosti o compilati del checkout
        var ckDestinatario = document.getElementById('ck-destinatario');
        var ckVia = document.getElementById('ck-via');
        var ckCap = document.getElementById('ck-cap');
        var ckCitta = document.getElementById('ck-citta');
        var ckProvincia = document.getElementById('ck-provincia');
        var ckPaese = document.getElementById('ck-paese');

        // copia nel checkout i dati dell'indirizzo selezionato
        if (ckDestinatario) ckDestinatario.value = card.dataset.destinatario || '';
        if (ckVia) ckVia.value = card.dataset.via || '';
        if (ckCap) ckCap.value = card.dataset.cap || '';
        if (ckCitta) ckCitta.value = card.dataset.citta || '';
        if (ckProvincia) ckProvincia.value = card.dataset.provincia || '';
        if (ckPaese) ckPaese.value = card.dataset.paese || '';

        // chiude il form indirizzo se era aperto
        if (indirizzoFormWrap) {
            indirizzoFormWrap.classList.remove('open');
        }
    };

    // espone la funzione globale per l'inserimento di un nuovo indirizzo
    window.apriNuovoIndirizzo = function () {
        // interrompe il flusso se form o contenitore non sono presenti
        if (!formIndirizzo || !indirizzoFormWrap) return;

        // resetta il form per partire da campi vuoti
        formIndirizzo.reset();

        // recupera i campi usati per distinguere nuovo inserimento e provenienza
        var indirizzoId = document.getElementById('indirizzoId');
        var indirizzoFrom = document.getElementById('indirizzoFrom');

        // svuota id e provenienza perché si tratta di un nuovo indirizzo
        if (indirizzoId) indirizzoId.value = '';
        if (indirizzoFrom) indirizzoFrom.value = '';

        // apre il form in modalità inserimento
        apriFormIndirizzo('Nuovo indirizzo', 'Salva indirizzo', indirizzoFormWrap.dataset.actionNuovo);
    };

    // espone la funzione globale per modificare un indirizzo esistente
    window.apriEditIndirizzo = function (card) {
        // interrompe il flusso se la card o il contenitore non sono presenti
        if (!card || !indirizzoFormWrap) return;

        // recupera i campi del form indirizzo
        var indirizzoId = document.getElementById('indirizzoId');
        var destinatario = document.getElementById('destinatario');
        var via = document.getElementById('via');
        var cap = document.getElementById('cap');
        var citta = document.getElementById('citta');
        var provincia = document.getElementById('provincia');
        var paese = document.getElementById('paese');
        var indirizzoFrom = document.getElementById('indirizzoFrom');

        // copia nel form i valori presenti nella card selezionata
        if (indirizzoId) indirizzoId.value = card.dataset.id || '';
        if (destinatario) destinatario.value = card.dataset.destinatario || '';
        if (via) via.value = card.dataset.via || '';
        if (cap) cap.value = card.dataset.cap || '';
        if (citta) citta.value = card.dataset.citta || '';
        if (provincia) provincia.value = card.dataset.provincia || '';
        if (paese) paese.value = card.dataset.paese || '';

        // salva l'origine del form per distinguere il flusso lato server
        if (indirizzoFrom) indirizzoFrom.value = 'checkout';

        // apre il form in modalità modifica
        apriFormIndirizzo('Modifica indirizzo', 'Salva modifiche', indirizzoFormWrap.dataset.actionModifica);
    };

    // espone la chiusura del form indirizzo anche fuori da questo file
    window.chiudiEditIndirizzo = chiudiFormIndirizzo;

    // seleziona automaticamente il primo indirizzo disponibile
    var primaCard = document.querySelector('.profilo-addr-card');
    if (primaCard) {
        selezionaIndirizzoCheckout(primaCard);
    } else if (indirizzoFormWrap) {
        // apre il form indirizzo se non esiste ancora nessun indirizzo salvato
        indirizzoFormWrap.classList.add('open');

        // nasconde il pulsante annulla perché non si tratta di una modifica
        var btnAnnullaEdit = document.getElementById('btnAnnullaEdit');
        if (btnAnnullaEdit) {
            btnAnnullaEdit.hidden = true;
        }
    }

    // recupera i campi del numero carta da formattare durante la digitazione
    var campiNumeroCarta = document.querySelectorAll('.js-card-number');

    // mantiene solo le cifre e inserisce uno spazio ogni 4 caratteri
    for (var j = 0; j < campiNumeroCarta.length; j++) {
        campiNumeroCarta[j].addEventListener('input', function () {
            var cifre = this.value.replace(/\D/g, '').substring(0, 16);
            this.value = cifre.replace(/(.{4})/g, '$1 ').trim();
        });
    }

    // recupera i campi della scadenza da formattare durante la digitazione
    var campiScadenza = document.querySelectorAll('.js-card-expiry');

    // mantiene solo le cifre e inserisce il separatore nel formato mm/aa
    for (var k = 0; k < campiScadenza.length; k++) {
        campiScadenza[k].addEventListener('input', function () {
            var cifre = this.value.replace(/\D/g, '').substring(0, 4);

            // completa il formato con slash quando sono presenti almeno 3 cifre
            if (cifre.length >= 3) {
                this.value = cifre.substring(0, 2) + '/' + cifre.substring(2);
            } else {
                this.value = cifre;
            }
        });
    }

    // applica la validazione client solo se il form checkout è presente
    if (formCheckout) {
        formCheckout.addEventListener('submit', function (e) {
            // rimuove gli errori client mostrati in precedenza
            var errors = formCheckout.querySelectorAll('.pay-error.js-err');
            for (var x = 0; x < errors.length; x++) {
                errors[x].remove();
            }

            // inizializza il flag usato per decidere l'invio del form
            var valid = true;

            // aggiunge un messaggio di errore sotto il campo non valido
            function mostraErrore(inputId, message) {
                var input = document.getElementById(inputId);
                if (!input) return;

                var group = input.closest('.pay-group') || input.parentNode;
                var error = document.createElement('span');
                error.className = 'pay-error js-err';
                error.textContent = message;
                group.appendChild(error);

                valid = false;
            }

            // controlla che sia presente un indirizzo di spedizione selezionato o compilato
            var campoDestinatario = document.getElementById('ck-destinatario');
            if (!campoDestinatario || !campoDestinatario.value.trim()) {
                var sezioneIndirizzo = document.getElementById('checkout-addr-section');
                var errorIndirizzo = document.createElement('span');
                errorIndirizzo.className = 'pay-error js-err';
                errorIndirizzo.textContent = 'Seleziona o inserisci un indirizzo di spedizione.';

                // mostra l'errore nella sezione indirizzo
                if (sezioneIndirizzo) {
                    sezioneIndirizzo.appendChild(errorIndirizzo);
                }

                valid = false;
            }

            // controlla presenza e formato del nome sulla carta
            var nomeCarta = document.getElementById('nomeCarta');
            if (nomeCarta) {
                var valoreNome = normalizeText(nomeCarta.value);

                if (!valoreNome) {
                    mostraErrore('nomeCarta', 'Il nome sulla carta è obbligatorio.');
                } else if (!isNomeCartaValido(valoreNome)) {
                    mostraErrore('nomeCarta', 'Inserisci nome e cognome come appaiono sulla carta.');
                }
            }

            // controlla il formato del numero carta
            var numeroCarta = document.getElementById('numeroCarta');
            if (numeroCarta && !isNumeroCartaValido(numeroCarta.value)) {
                mostraErrore('numeroCarta', 'Inserisci un numero di carta valido (16 cifre).');
            }

            // controlla il formato della data di scadenza
            var scadenza = document.getElementById('scadenza');
            if (scadenza && !isScadenzaCartaValida(scadenza.value)) {
                mostraErrore('scadenza', 'Inserisci una data di scadenza valida (MM/AA).');
            }

            // controlla il formato del cvv
            var cvv = document.getElementById('cvv');
            if (cvv && !isCvvValido(cvv.value)) {
                mostraErrore('cvv', 'CVV non valido (3 o 4 cifre).');
            }

            // blocca l'invio del form in presenza di errori
            if (!valid) {
                e.preventDefault();
            }
        });
    }
});