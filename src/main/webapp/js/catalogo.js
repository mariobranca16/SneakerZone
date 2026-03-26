/*
 * Gestisce l'aggiornamento dei risultati del catalogo senza ricaricare la pagina.
 * Il server restituisce solo l'html della lista prodotti.
 */

document.addEventListener('DOMContentLoaded', function () {
    // recupera il form dei filtri
    var filtriForm = document.getElementById('filtriForm');

    // interrompe l'esecuzione se il form non è presente
    if (!filtriForm) return;

    // recupera i campi usati per costruire i filtri
    var campoRicerca = document.getElementById('q');
    var campoPrezzoMin = filtriForm.querySelector('input[name="prezzoMin"]');
    var campoPrezzoMax = filtriForm.querySelector('input[name="prezzoMax"]');
    var areaRisultati = document.getElementById('risultatiProdotti');
    var btnAzzera = document.getElementById('btnAzzera');

    // carica i prodotti in base ai filtri selezionati
    function caricaProdotti() {
        // recupera il testo di ricerca
        var q = campoRicerca ? campoRicerca.value.trim() : '';

        // recupera la categoria selezionata
        var radioCategoria = filtriForm.querySelector('input[name="categoria"]:checked');
        var categoria = radioCategoria ? radioCategoria.value : '';

        // recupera il genere selezionato
        var radioGenere = filtriForm.querySelector('input[name="genere"]:checked');
        var genere = radioGenere ? radioGenere.value : '';

        // recupera gli estremi del prezzo
        var prezzoMin = campoPrezzoMin ? campoPrezzoMin.value.trim() : '';
        var prezzoMax = campoPrezzoMax ? campoPrezzoMax.value.trim() : '';

        // prepara i parametri della query solo per i filtri valorizzati
        var params = ['ajax=1'];

        if (q) {
            params.push('q=' + encodeURIComponent(q));
        }
        if (categoria) {
            params.push('categoria=' + encodeURIComponent(categoria));
        }
        if (genere) {
            params.push('genere=' + encodeURIComponent(genere));
        }
        if (prezzoMin) {
            params.push('prezzoMin=' + encodeURIComponent(prezzoMin));
        }
        if (prezzoMax) {
            params.push('prezzoMax=' + encodeURIComponent(prezzoMax));
        }

        // costruisce l'url della richiesta a partire dall'action del form
        var url = filtriForm.action;
        if (params.length > 0) {
            url += '?' + params.join('&');
        }

        // invia la richiesta ajax al server
        fetch(url)
            .then(function (response) {
                // converte la risposta in html testuale
                return response.text();
            })
            .then(function (html) {
                // aggiorna l'area risultati con il contenuto restituito dal server
                if (areaRisultati) {
                    areaRisultati.innerHTML = html;
                }
            })
            .catch(function () {
                // in caso di errore resta visibile l'ultimo risultato caricato
            });
    }

    // recupera tutti i radio button dei filtri
    var radioButtons = filtriForm.querySelectorAll('input[type="radio"]');

    // ricarica i prodotti quando cambia un filtro radio
    for (var i = 0; i < radioButtons.length; i++) {
        radioButtons[i].addEventListener('change', caricaProdotti);
    }

    // recupera i campi testuali e numerici del form
    var campiTesto = filtriForm.querySelectorAll('input[type="text"], input[type="number"]');

    // ricarica i prodotti quando cambia un campo di testo o numero
    for (var j = 0; j < campiTesto.length; j++) {
        campiTesto[j].addEventListener('change', caricaProdotti);
    }

    // gestisce il reset dei filtri tramite il pulsante dedicato
    if (btnAzzera) {
        btnAzzera.addEventListener('click', function (e) {
            // blocca il comportamento di default del pulsante
            e.preventDefault();

            // svuota i campi di ricerca e prezzo
            if (campoRicerca) {
                campoRicerca.value = '';
            }
            if (campoPrezzoMin) {
                campoPrezzoMin.value = '';
            }
            if (campoPrezzoMax) {
                campoPrezzoMax.value = '';
            }

            // ripristina i radio button sul valore vuoto
            for (var k = 0; k < radioButtons.length; k++) {
                radioButtons[k].checked = radioButtons[k].value === '';
            }

            // ricarica i prodotti senza filtri
            caricaProdotti();
        });
    }
});