/* ---- Gestione indirizzo checkout ---- */
(function () {
    var indirizzoFormWrap = document.getElementById('indirizzoFormWrap');
    var formIndirizzo = document.getElementById('formIndirizzo');

    window.selezionaIndirizzoCheckout = function (card) {
        document.querySelectorAll('.profilo-addr-card').forEach(function (c) {
            c.classList.remove('selected');
            var r = c.querySelector('.addr-radio');
            if (r) r.checked = false;
        });
        card.classList.add('selected');
        var radio = card.querySelector('.addr-radio');
        if (radio) radio.checked = true;
        document.getElementById('ck-destinatario').value = card.dataset.destinatario;
        document.getElementById('ck-via').value = card.dataset.via;
        document.getElementById('ck-cap').value = card.dataset.cap;
        document.getElementById('ck-citta').value = card.dataset.citta;
        document.getElementById('ck-provincia').value = card.dataset.provincia;
        document.getElementById('ck-paese').value = card.dataset.paese;
        if (indirizzoFormWrap) indirizzoFormWrap.classList.remove('open');
    };

    window.apriNuovoIndirizzo = function () {
        if (!formIndirizzo) return;
        formIndirizzo.reset();
        document.getElementById('indirizzoId').value = '';
        document.getElementById('indirizzoFrom').value = '';
        document.getElementById('indirizzoFormTitolo').textContent = 'Nuovo indirizzo';
        document.getElementById('btnSalvaIndirizzo').textContent = 'Salva indirizzo';
        document.getElementById('btnAnnullaEdit').hidden = false;
        formIndirizzo.action = indirizzoFormWrap.dataset.actionNuovo;
        indirizzoFormWrap.classList.add('open');
        indirizzoFormWrap.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    };

    window.apriEditIndirizzo = function (card) {
        document.getElementById('indirizzoId').value = card.dataset.id;
        document.getElementById('destinatario').value = card.dataset.destinatario;
        document.getElementById('via').value = card.dataset.via;
        document.getElementById('cap').value = card.dataset.cap;
        document.getElementById('citta').value = card.dataset.citta;
        document.getElementById('provincia').value = card.dataset.provincia;
        document.getElementById('paese').value = card.dataset.paese;
        document.getElementById('indirizzoFrom').value = 'checkout';
        document.getElementById('indirizzoFormTitolo').textContent = 'Modifica indirizzo';
        document.getElementById('btnSalvaIndirizzo').textContent = 'Salva modifiche';
        document.getElementById('btnAnnullaEdit').hidden = false;
        formIndirizzo.action = indirizzoFormWrap.dataset.actionModifica;
        indirizzoFormWrap.classList.add('open');
        indirizzoFormWrap.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    };

    window.chiudiEditIndirizzo = function () {
        if (!formIndirizzo) return;
        formIndirizzo.reset();
        document.getElementById('indirizzoId').value = '';
        document.getElementById('indirizzoFrom').value = '';
        document.getElementById('indirizzoFormTitolo').textContent = 'Nuovo indirizzo';
        document.getElementById('btnSalvaIndirizzo').textContent = 'Salva indirizzo';
        document.getElementById('btnAnnullaEdit').hidden = true;
        formIndirizzo.action = indirizzoFormWrap.dataset.actionNuovo;
        indirizzoFormWrap.classList.remove('open');
    };

    /* auto-seleziona il primo indirizzo (precompilato) se esiste */
    var firstCard = document.querySelector('.profilo-addr-card');
    if (firstCard) {
        selezionaIndirizzoCheckout(firstCard);
    } else if (indirizzoFormWrap) {
        /* nessun indirizzo salvato: apri subito il form */
        indirizzoFormWrap.classList.add('open');
        document.getElementById('btnAnnullaEdit').hidden = true;
    }
}());

/* ---- Checkout form ---- */
document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    document.querySelectorAll('.js-card-number').forEach(function (input) {
        input.addEventListener('input', function () {
            var v = this.value.replace(/\D/g, '').substring(0, 16);
            this.value = v.replace(/(.{4})/g, '$1 ').trim();
        });
    });

    document.querySelectorAll('.js-card-expiry').forEach(function (input) {
        input.addEventListener('input', function () {
            var v = this.value.replace(/\D/g, '').substring(0, 4);
            this.value = v.length >= 3 ? v.substring(0, 2) + '/' + v.substring(2) : v;
        });
    });

    var inputValidation = window.ValidazioneInput;
    if (!inputValidation) {
        return;
    }

    var checkoutForm = document.querySelector('.checkout-form');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function (e) {
            checkoutForm.querySelectorAll('.pay-error.js-err').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            var ckDestinatario = document.getElementById('ck-destinatario');
            if (!ckDestinatario || !ckDestinatario.value.trim()) {
                var addrSection = document.getElementById('checkout-addr-section');
                var errAddr = document.createElement('span');
                errAddr.className = 'pay-error js-err';
                errAddr.textContent = 'Seleziona o inserisci un indirizzo di spedizione.';
                if (addrSection) addrSection.appendChild(errAddr);
                valido = false;
            }

            function mostraErrore(inputId, msg) {
                var input = document.getElementById(inputId);
                if (!input) return;
                var gruppo = input.closest('.pay-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'pay-error js-err';
                span.textContent = msg;
                gruppo.appendChild(span);
                valido = false;
            }

            var nomeCarta = document.getElementById('nomeCarta');
            if (nomeCarta) {
                var nomeVal = inputValidation.normalizeText(nomeCarta.value);
                if (!nomeVal) {
                    mostraErrore('nomeCarta', 'Il nome sulla carta e obbligatorio.');
                } else if (!inputValidation.isNomeCartaValido(nomeVal)) {
                    mostraErrore('nomeCarta', 'Inserisci nome e cognome come appaiono sulla carta (es. Mario Rossi).');
                }
            }

            var numeroCarta = document.getElementById('numeroCarta');
            if (numeroCarta && !inputValidation.isNumeroCartaValido(numeroCarta.value)) {
                mostraErrore('numeroCarta', 'Inserisci un numero di carta valido (16 cifre).');
            }

            var scadenza = document.getElementById('scadenza');
            if (scadenza) {
                var erroreScadenza = inputValidation.getErroreScadenzaCarta(scadenza.value);
                if (erroreScadenza) {
                    mostraErrore('scadenza', erroreScadenza);
                }
            }

            var cvv = document.getElementById('cvv');
            if (cvv && !inputValidation.isCvvValido(cvv.value)) {
                mostraErrore('cvv', 'CVV non valido (3 o 4 cifre).');
            }

            if (!valido) e.preventDefault();
        });
    }
});
