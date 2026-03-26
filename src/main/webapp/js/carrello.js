/*
 * Gestisce l'aggiornamento quantità e la rimozione dei prodotti dal carrello.
 * In caso di errore nella chiamata AJAX, usa il submit normale del form.
 */

// controlla che la quantità resti nel range consentito
function controllaQuantita(input) {
    var valore = parseInt(input.value, 10);

    // forza il minimo se il valore non è valido o troppo basso
    if (isNaN(valore) || valore < 1) {
        input.value = 1;
        // forza il massimo se il valore supera il limite previsto
    } else if (valore > 99) {
        input.value = 99;
    }
}

// aggiorna il totale complessivo mostrato nel riepilogo del carrello
function aggiornaTotale(totale) {
    // recupera l'elemento che mostra il totale
    var totaleElemento = document.getElementById('cart-totale-valore');

    // aggiorna il testo solo se l'elemento è presente
    if (totaleElemento) {
        totaleElemento.textContent = parseFloat(totale).toFixed(2);
    }
}

// invia la richiesta AJAX per rimuovere un prodotto dal carrello
function inviaRimozione(form) {
    // recupera i dati necessari dal form
    var id = form.querySelector('[name="id"]').value;
    var taglia = form.querySelector('[name="taglia"]').value;

    // costruisce i parametri della richiesta POST
    var params = 'azione=rimuovi&id=' + encodeURIComponent(id) +
        '&taglia=' + encodeURIComponent(taglia) +
        '&ajax=1';

    // recupera la riga del prodotto per aggiornare il DOM
    var riga = form.closest('tr');

    // invia la richiesta al server in formato urlencoded
    fetch(form.action, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
        .then(function (response) {
            // converte la risposta in json
            return response.json();
        })
        .then(function (data) {
            // interrompe il flusso se il server segnala errore
            if (!data.success) {
                return;
            }

            // rimuove la riga del prodotto dal carrello
            if (riga) {
                riga.remove();
            }

            // ricarica la pagina se il carrello è diventato vuoto
            if (data.vuoto) {
                window.location.reload();
                // altrimenti aggiorna solo il totale
            } else {
                aggiornaTotale(data.totale);
            }
        })
        .catch(function () {
            // usa il submit classico se la chiamata AJAX fallisce
            form.submit();
        });
}

// invia la richiesta AJAX per aggiornare la quantità di un prodotto
function inviaAggiornamento(form) {
    // recupera i dati necessari dal form
    var id = form.querySelector('[name="id"]').value;
    var taglia = form.querySelector('[name="taglia"]').value;
    var quantita = form.querySelector('[name="quantita"]').value;

    // costruisce i parametri della richiesta POST
    var params = 'azione=aggiorna&id=' + encodeURIComponent(id) +
        '&taglia=' + encodeURIComponent(taglia) +
        '&quantita=' + encodeURIComponent(quantita) +
        '&ajax=1';

    // recupera la riga del prodotto per aggiornare il DOM
    var riga = form.closest('tr');

    // invia la richiesta al server in formato urlencoded
    fetch(form.action, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
        .then(function (response) {
            // converte la risposta in json
            return response.json();
        })
        .then(function (data) {
            // interrompe il flusso se il server segnala errore
            if (!data.success) {
                return;
            }

            // gestisce il caso in cui il server rimuova il prodotto dal carrello
            if (data.rimosso) {
                if (riga) {
                    riga.remove();
                }

                // ricarica la pagina se il carrello è diventato vuoto
                if (data.vuoto) {
                    window.location.reload();
                    return;
                }
                // altrimenti aggiorna subtotal e quantità mostrata
            } else {
                // recupera gli elementi da aggiornare nella riga corrente
                var cellaPrezzo = riga ? riga.querySelector('.cart-price') : null;
                var inputQuantita = form.querySelector('.qty-input');

                // aggiorna il subtotale del prodotto
                if (cellaPrezzo) {
                    cellaPrezzo.textContent = parseFloat(data.subtotale).toFixed(2) + ' €';
                }

                // riallinea il valore dell'input con quello confermato dal server
                if (inputQuantita) {
                    inputQuantita.value = data.nuovaQuantita;
                }
            }

            // aggiorna sempre il totale finale del carrello
            aggiornaTotale(data.totale);
        })
        .catch(function () {
            // usa il submit classico se la chiamata AJAX fallisce
            form.submit();
        });
}

// collega gli eventi ai form e agli input del carrello
document.addEventListener('DOMContentLoaded', function () {
    // recupera tutti gli input quantità
    var inputsQuantita = document.querySelectorAll('.qty-input');

    // invia l'aggiornamento quando cambia la quantità
    for (var i = 0; i < inputsQuantita.length; i++) {
        inputsQuantita[i].addEventListener('change', function () {
            // normalizza prima il valore inserito
            controllaQuantita(this);

            // recupera il form associato all'input e invia l'aggiornamento
            var form = this.closest('.qty-form');
            if (form) {
                inviaAggiornamento(form);
            }
        });
    }

    // recupera tutti i form usati per l'aggiornamento quantità
    var formsQuantita = document.querySelectorAll('.qty-form');

    // intercetta il submit per usare AJAX al posto dell'invio classico
    for (var j = 0; j < formsQuantita.length; j++) {
        formsQuantita[j].addEventListener('submit', function (e) {
            e.preventDefault();
            inviaAggiornamento(this);
        });
    }

    // recupera tutti i form usati per la rimozione dei prodotti
    var formsRimozione = document.querySelectorAll('.inline-form');

    // intercetta il submit per usare AJAX nella rimozione
    for (var k = 0; k < formsRimozione.length; k++) {
        formsRimozione[k].addEventListener('submit', function (e) {
            e.preventDefault();
            inviaRimozione(this);
        });
    }
});