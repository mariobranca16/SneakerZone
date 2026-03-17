document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('formCarrello');
    if (form) {
        form.addEventListener('submit', function (e) {
            form.querySelectorAll('.form-error-msg').forEach(function (el) {
                el.remove();
            });

            var taglia = document.getElementById('taglia');
            var quantita = document.getElementById('quantita');

            if (taglia && taglia.value === '') {
                e.preventDefault();
                var msg = document.createElement('p');
                msg.className = 'form-error-msg';
                msg.textContent = 'Seleziona una taglia prima di procedere.';
                taglia.closest('.selezione-taglia').appendChild(msg);
                taglia.focus();
                return;
            }

            var q = parseInt(quantita.value);
            if (isNaN(q) || q < 1) {
                e.preventDefault();
                quantita.value = 1;
            }
        });
    }
    var stelleInput = document.getElementById('stelleInput');
    if (stelleInput) {
        var stelle = stelleInput.querySelectorAll('.stella-input');
        var valutazioneHidden = document.getElementById('valutazioneHidden');
        var stelleErrore = document.getElementById('stelleErrore');

        function aggiornaStelle(valore) {
            stelle.forEach(function (s) {
                var v = parseInt(s.getAttribute('data-valore'));
                var attiva = v <= valore;
                s.classList.toggle('attiva', attiva);
                s.innerHTML = attiva
                    ? '<span aria-hidden="true">&#9733;</span>'
                    : '<span aria-hidden="true">&#9734;</span>';
            });
        }

        aggiornaStelle(parseInt(valutazioneHidden.value) || 0);

        stelle.forEach(function (stella) {
            stella.addEventListener('mouseover', function () {
                aggiornaStelle(parseInt(stella.getAttribute('data-valore')));
            });

            stella.addEventListener('mouseout', function () {
                aggiornaStelle(parseInt(valutazioneHidden.value) || 0);
            });

            stella.addEventListener('click', function () {
                valutazioneHidden.value = stella.getAttribute('data-valore');
                aggiornaStelle(parseInt(valutazioneHidden.value));
                if (stelleErrore) stelleErrore.style.display = 'none';
            });

            stella.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    stella.click();
                }
            });
        });

        var formRecensione = document.getElementById('formRecensione');
        if (formRecensione) {
            formRecensione.addEventListener('submit', function (e) {
                if (!valutazioneHidden.value) {
                    e.preventDefault();
                    if (stelleErrore) stelleErrore.style.display = 'block';
                    stelleInput.scrollIntoView({behavior: 'smooth', block: 'center'});
                }
            });
        }
    }
});
