/*
 * Gestisce la validazione del form carrello
 * e la selezione del voto a stelle nella recensione.
 */

document.addEventListener('DOMContentLoaded', function () {
    // recupera il form usato per aggiungere il prodotto al carrello
    var formCarrello = document.getElementById('formCarrello');

    // applica la validazione client solo se il form è presente
    if (formCarrello) {
        formCarrello.addEventListener('submit', function (e) {
            // rimuove eventuali errori mostrati in precedenza
            var errors = formCarrello.querySelectorAll('.form-error-msg');
            for (var i = 0; i < errors.length; i++) {
                errors[i].remove();
            }

            // recupera i campi da controllare prima dell'invio
            var taglia = document.getElementById('taglia');
            var quantita = document.getElementById('quantita');

            // controlla che sia stata selezionata una taglia
            if (taglia && taglia.value === '') {
                e.preventDefault();

                // crea e mostra il messaggio di errore nella sezione taglia
                var error = document.createElement('p');
                error.className = 'form-error-msg';
                error.textContent = 'Seleziona una taglia prima di procedere.';

                var contenitoreTaglia = taglia.closest('.selezione-taglia');
                if (contenitoreTaglia) {
                    contenitoreTaglia.appendChild(error);
                }

                // porta il focus sul campo non valido e interrompe il flusso
                taglia.focus();
                return;
            }

            // controlla che la quantità sia numerica e maggiore di zero
            if (quantita) {
                var quantitaValore = parseInt(quantita.value, 10);

                if (isNaN(quantitaValore) || quantitaValore < 1) {
                    e.preventDefault();
                    quantita.value = 1;
                }
            }
        });
    }

    // recupera gli elementi usati dal sistema di valutazione a stelle
    var stelleInput = document.getElementById('stelleInput');
    var valutazioneHidden = document.getElementById('valutazioneHidden');
    var stelleErrore = document.getElementById('stelleErrore');
    var formRecensione = document.getElementById('formRecensione');

    // inizializza la selezione stelle solo se i campi necessari sono presenti
    if (stelleInput && valutazioneHidden) {
        // recupera tutte le stelle cliccabili
        var stelle = stelleInput.querySelectorAll('.stella-input');

        // aggiorna lo stato visivo delle stelle in base al valore corrente
        function aggiornaStelle(valore) {
            for (var j = 0; j < stelle.length; j++) {
                var valoreStella = parseInt(stelle[j].getAttribute('data-valore'), 10);

                if (valoreStella <= valore) {
                    stelle[j].classList.add('attiva');
                } else {
                    stelle[j].classList.remove('attiva');
                }
            }
        }

        // allinea subito le stelle all'eventuale valore già presente
        aggiornaStelle(parseInt(valutazioneHidden.value, 10) || 0);

        // collega gli eventi di anteprima e selezione alle singole stelle
        for (var k = 0; k < stelle.length; k++) {
            stelle[k].addEventListener('mouseover', function () {
                // mostra un'anteprima visiva del voto durante il passaggio del mouse
                aggiornaStelle(parseInt(this.getAttribute('data-valore'), 10));
            });

            stelle[k].addEventListener('mouseout', function () {
                // ripristina il voto realmente selezionato quando il mouse esce
                aggiornaStelle(parseInt(valutazioneHidden.value, 10) || 0);
            });

            stelle[k].addEventListener('click', function () {
                // salva il voto selezionato nel campo hidden
                valutazioneHidden.value = this.getAttribute('data-valore');
                aggiornaStelle(parseInt(valutazioneHidden.value, 10));

                // nasconde l'eventuale errore dopo una selezione valida
                if (stelleErrore) {
                    stelleErrore.classList.remove('visibile');
                }
            });
        }
    }

    // controlla che la recensione venga inviata solo con una valutazione selezionata
    if (formRecensione && valutazioneHidden) {
        formRecensione.addEventListener('submit', function (e) {
            if (!valutazioneHidden.value) {
                e.preventDefault();

                // mostra l'errore se nessuna stella è stata selezionata
                if (stelleErrore) {
                    stelleErrore.classList.add('visibile');
                }

                // porta in vista il blocco stelle per guidare l'utente
                if (stelleInput) {
                    stelleInput.scrollIntoView();
                }
            }
        });
    }
});