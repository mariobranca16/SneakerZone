document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    function inizializzaCardSelezionata(classeRadio, classeCard) {
        var radios = document.querySelectorAll('.' + classeRadio);
        radios.forEach(function (radio) {
            if (radio.checked) {
                var card = radio.closest('.' + classeCard);
                if (card) card.classList.add('selected');
            }
            radio.addEventListener('change', function () {
                document.querySelectorAll('.' + classeCard).forEach(function (c) {
                    c.classList.remove('selected');
                });
                var card = this.closest('.' + classeCard);
                if (card) card.classList.add('selected');
            });
        });
    }

    inizializzaCardSelezionata('address-radio', 'address-card');

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
