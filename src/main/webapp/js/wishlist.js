/*
 * Gestisce l'aggiunta alla wishlist con richiesta AJAX e aggiorna il badge senza ricaricare la pagina.
 * In caso di errore usa il submit normale del form.
 */

// aggiorna il badge wishlist nella navbar
function aggiornaBadgeWishlist(count) {
    // recupera il badge attuale e il link della wishlist
    var badge = document.getElementById('wishlist-badge');
    var link = document.getElementById('wishlist-link');

    // interrompe il flusso se il link non è presente
    if (!link) return;

    // mostra o aggiorna il badge se il conteggio è maggiore di zero
    if (count > 0) {
        // crea il badge se non esiste ancora
        if (!badge) {
            badge = document.createElement('span');
            badge.className = 'cart-badge';
            badge.id = 'wishlist-badge';
            link.appendChild(badge);
        }

        // aggiorna il numero mostrato nel badge
        badge.textContent = count;
        // rimuove il badge se la wishlist è vuota
    } else if (badge) {
        badge.remove();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // recupera tutti i form usati per aggiungere prodotti alla wishlist
    var forms = document.querySelectorAll('.form-wishlist');

    // collega il submit AJAX a ogni form wishlist
    for (var i = 0; i < forms.length; i++) {
        forms[i].addEventListener('submit', function (e) {
            // blocca il submit classico per provare prima la chiamata AJAX
            e.preventDefault();

            // recupera gli elementi necessari dal form corrente
            var form = this;
            var pulsante = form.querySelector('button[type="submit"]');
            var idProdotto = form.querySelector('[name="idProdotto"]').value;

            // disabilita il pulsante per evitare invii ripetuti
            if (pulsante) {
                pulsante.disabled = true;
            }

            // invia la richiesta al server in formato urlencoded
            fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: 'idProdotto=' + encodeURIComponent(idProdotto)
            })
                .then(function (response) {
                    // converte la risposta in json
                    return response.json();
                })
                .then(function (data) {
                    // gestisce il redirect, ad esempio verso il login, se richiesto dal server
                    if (!data.success && data.redirect) {
                        window.location.href = data.redirect;
                        return;
                    }

                    // riabilita il pulsante dopo la risposta del server
                    if (pulsante) {
                        pulsante.disabled = false;
                    }

                    // mostra notifica e aggiorna il badge in caso di inserimento riuscito
                    if (data.success) {
                        mostraNotifica('Prodotto aggiunto alla wishlist');
                        aggiornaBadgeWishlist(data.count);
                    }
                })
                .catch(function () {
                    // riabilita il pulsante prima del fallback
                    if (pulsante) {
                        pulsante.disabled = false;
                    }

                    // usa il submit classico se la chiamata AJAX fallisce
                    form.submit();
                });
        });
    }
});