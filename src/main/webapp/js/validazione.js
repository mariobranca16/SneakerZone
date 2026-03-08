document.addEventListener('DOMContentLoaded', function() {

    /* --- Form registrazione --- */
    var regForm = document.getElementById('formRegistrazione');
    if (regForm) {
        regForm.addEventListener('submit', function(e) {
            regForm.querySelectorAll('.field-error').forEach(function(el) { el.remove(); });
            var valido = true;

            function mostraErrore(nomeCampo, messaggio) {
                var input = regForm.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
                valido = false;
            }

            var nome        = regForm.querySelector('[name="nome"]').value.trim();
            var cognome     = regForm.querySelector('[name="cognome"]').value.trim();
            var email       = regForm.querySelector('[name="email"]').value.trim();
            var password    = regForm.querySelector('[name="passwordHash"]').value.trim();
            var telefono    = regForm.querySelector('[name="telefono"]').value.trim();
            var dataNascita = regForm.querySelector('[name="dataNascita"]').value;

            if (nome === '') mostraErrore('nome', 'Campo obbligatorio');
            if (cognome === '') mostraErrore('cognome', 'Campo obbligatorio');

            if (email === '') {
                mostraErrore('email', 'Campo obbligatorio');
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                mostraErrore('email', 'Formato email non valido');
            }

            if (password === '') {
                mostraErrore('passwordHash', 'Campo obbligatorio');
            } else if (password.length < 8) {
                mostraErrore('passwordHash', 'Minimo 8 caratteri');
            }

            if (telefono === '') {
                mostraErrore('telefono', 'Campo obbligatorio');
            } else if (!/^[+]?[\d\s\-]{8,15}$/.test(telefono)) {
                mostraErrore('telefono', 'Numero di telefono non valido');
            }

            if (dataNascita === '') {
                mostraErrore('dataNascita', 'Campo obbligatorio');
            } else {
                var oggi    = new Date();
                var nascita = new Date(dataNascita);
                var eta     = oggi.getFullYear() - nascita.getFullYear();
                var mesi    = oggi.getMonth() - nascita.getMonth();
                if (mesi < 0 || (mesi === 0 && oggi.getDate() < nascita.getDate())) eta--;
                if (eta < 18) mostraErrore('dataNascita', 'Devi avere almeno 18 anni');
            }

            if (!valido) e.preventDefault();
        });
    }

    /* --- Form indirizzo di spedizione --- */
    var addrForm = document.getElementById('formIndirizzo');
    if (addrForm) {
        addrForm.addEventListener('submit', function(e) {
            addrForm.querySelectorAll('.field-error').forEach(function(el) { el.remove(); });
            var valido = true;

            function mostraErrore(nomeCampo, messaggio) {
                var input = addrForm.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
                valido = false;
            }

            var destinatario = addrForm.querySelector('[name="destinatario"]').value.trim();
            var via          = addrForm.querySelector('[name="via"]').value.trim();
            var cap          = addrForm.querySelector('[name="cap"]').value.trim();
            var citta        = addrForm.querySelector('[name="citta"]').value.trim();
            var provincia    = addrForm.querySelector('[name="provincia"]').value.trim();
            var paese        = addrForm.querySelector('[name="paese"]').value.trim();

            if (destinatario === '') mostraErrore('destinatario', 'Campo obbligatorio');
            if (via === '') mostraErrore('via', 'Campo obbligatorio');

            if (cap === '') {
                mostraErrore('cap', 'Campo obbligatorio');
            } else if (!/^\d{5}$/.test(cap)) {
                mostraErrore('cap', 'Il CAP deve essere di 5 cifre');
            }

            if (citta === '') mostraErrore('citta', 'Campo obbligatorio');

            if (provincia === '') {
                mostraErrore('provincia', 'Campo obbligatorio');
            } else if (provincia.length > 5) {
                mostraErrore('provincia', 'Massimo 5 caratteri');
            }

            if (paese === '') mostraErrore('paese', 'Campo obbligatorio');

            if (!valido) e.preventDefault();
        });
    }
});
