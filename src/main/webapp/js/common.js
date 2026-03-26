/*
 * Raccoglie utility comuni del sito:
 * gestione alert, dropdown, conferme e form indirizzo inline.
 */

// rimuove automaticamente gli alert dopo alcuni secondi
function autoHideAlerts() {
    // seleziona tutti gli alert temporanei mostrati nella pagina
    document.querySelectorAll('.alert-success, .alert-error, .alert-danger').forEach(function (alert) {
        // rimuove ogni alert dopo 3 secondi
        setTimeout(function () {
            alert.remove();
        }, 3000);
    });
}

// mostra una notifica temporanea di successo
function mostraNotifica(text) {
    // crea l'elemento html della notifica
    var notification = document.createElement('div');
    notification.className = 'alert alert-success';
    notification.textContent = text;
    document.body.appendChild(notification);

    // rimuove la notifica dopo 3 secondi
    setTimeout(function () {
        notification.remove();
    }, 3000);
}

// alterna la visibilità del campo password e aggiorna l'icona
function togglePassword(inputId, btn) {
    // recupera input e pulsante associati
    var input = document.getElementById(inputId);
    if (!input || !btn) return;

    // recupera l'icona interna del pulsante
    var icon = btn.querySelector('i');

    // mostra la password se il campo è attualmente nascosto
    if (input.type === 'password') {
        input.type = 'text';

        // aggiorna l'icona sulla modalità visibile
        if (icon) {
            icon.classList.remove('ti-eye');
            icon.classList.add('ti-eye-off');
        }
    } else {
        // ripristina il campo password in modalità nascosta
        input.type = 'password';

        // aggiorna l'icona sulla modalità nascosta
        if (icon) {
            icon.classList.remove('ti-eye-off');
            icon.classList.add('ti-eye');
        }
    }
}

// inizializza il menu dropdown dell'utente
function initDropdown() {
    // recupera il pulsante di apertura e il contenitore del dropdown
    var toggle = document.querySelector('.user-menu-toggle');
    var dropdown = toggle ? toggle.closest('.dropdown') : null;

    // interrompe il flusso se gli elementi necessari non sono presenti
    if (!toggle || !dropdown) return;

    // apre o chiude il menu al click sul pulsante
    toggle.addEventListener('click', function (e) {
        // evita la chiusura immediata dovuta al listener globale
        e.stopPropagation();

        // aggiorna lo stato visivo del dropdown
        var open = dropdown.classList.toggle('is-open');
        toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    });

    // chiude il menu quando avviene un click fuori dal dropdown
    document.addEventListener('click', function () {
        dropdown.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
    });
}

// inizializza la conferma per i pulsanti che la richiedono
function initConfirmButtons() {
    // intercetta i click sugli elementi con attributo data-confirm
    document.addEventListener('click', function (e) {
        var pulsante = e.target.closest('[data-confirm]');
        if (!pulsante) return;

        // recupera il messaggio di conferma dal data-attribute
        var message = pulsante.getAttribute('data-confirm');

        // blocca l'azione se la conferma viene annullata
        if (!confirm(message)) {
            e.preventDefault();
            e.stopPropagation();
        }
    });
}

// inizializza le utility comuni al caricamento della pagina
document.addEventListener('DOMContentLoaded', function () {
    // attiva la chiusura automatica degli alert
    autoHideAlerts();

    // attiva il dropdown utente
    initDropdown();

    // attiva le conferme sui pulsanti dedicati
    initConfirmButtons();
});

// apre il form indirizzo inline in inserimento o modifica
function apriFormIndirizzo(titolo, testoPulsante, action) {
    // recupera contenitore e form indirizzo
    var wrap = document.getElementById('indirizzoFormWrap');
    var form = document.getElementById('formIndirizzo');

    // interrompe il flusso se gli elementi non sono presenti
    if (!wrap || !form) return;

    // aggiorna titolo, testo del pulsante e visibilità del tasto annulla
    document.getElementById('indirizzoFormTitolo').textContent = titolo;
    document.getElementById('btnSalvaIndirizzo').textContent = testoPulsante;
    document.getElementById('btnAnnullaEdit').hidden = false;

    // imposta l'action corretta del form
    form.action = action;

    // apre il contenitore e lo porta in vista
    wrap.classList.add('open');
    wrap.scrollIntoView();
}

// chiude il form indirizzo e ripristina lo stato iniziale
function chiudiFormIndirizzo() {
    // recupera contenitore e form indirizzo
    var wrap = document.getElementById('indirizzoFormWrap');
    var form = document.getElementById('formIndirizzo');

    // interrompe il flusso se gli elementi non sono presenti
    if (!wrap || !form) return;

    // resetta i campi del form
    form.reset();

    // ripristina i campi nascosti e i testi di default
    document.getElementById('indirizzoId').value = '';
    document.getElementById('indirizzoFrom').value = '';
    document.getElementById('indirizzoFormTitolo').textContent = 'Nuovo indirizzo';
    document.getElementById('btnSalvaIndirizzo').textContent = 'Salva indirizzo';
    document.getElementById('btnAnnullaEdit').hidden = true;

    // ripristina l'action del nuovo inserimento e chiude il form
    form.action = wrap.dataset.actionNuovo;
    wrap.classList.remove('open');
}