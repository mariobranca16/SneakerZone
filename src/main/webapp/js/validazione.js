function normalizeText(value) {
    return value == null ? '' : value.trim();
}

function isEmailValida(value) {
    var email = normalizeText(value);
    return email.length <= 100 && /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/.test(email);
}

function isTelefonoValido(value) {
    var telefono = normalizeText(value).replace(/[\s-]+/g, '');
    return /^\+?\d{8,13}$/.test(telefono);
}

function isPasswordForte(value) {
    var p = value || '';
    if (p.length < 8 || p.length > 64) return false;
    if (/\s/.test(p)) return false;
    return /[A-Z]/.test(p) && /[a-z]/.test(p) && /\d/.test(p) && /[^A-Za-z0-9]/.test(p);
}

function isNomeValido(value) {
    var nome = normalizeText(value);
    return nome.length >= 2 && nome.length <= 50 && /^[A-Za-zÀ-ÿ]+([ '\-][A-Za-zÀ-ÿ]+)*$/.test(nome);
}

function isViaValida(value) {
    var via = normalizeText(value);
    if (via.length < 5 || via.length > 100) return false;
    return /[A-Za-zÀ-ÿ]/.test(via) && /\d/.test(via);
}

function isCapValido(value) {
    return /^\d{5}$/.test(normalizeText(value));
}

function isProvinciaValida(value) {
    return /^[A-Za-z]{2,5}$/.test(normalizeText(value));
}

function isLocalitaValida(value) {
    var localita = normalizeText(value);
    return localita.length >= 2 && localita.length <= 100 && /[A-Za-zÀ-ÿ]/.test(localita);
}

function isDestinatarioValido(value) {
    var dest = normalizeText(value);
    return dest.length >= 4 && dest.length <= 100 && /[A-Za-zÀ-ÿ]/.test(dest) && dest.indexOf(' ') !== -1;
}

function isNomeCartaValido(value) {
    var nome = normalizeText(value);
    return nome.length >= 3 && nome.length <= 26 && /[A-Za-zÀ-ÿ]/.test(nome) && nome.indexOf(' ') !== -1;
}

function isNumeroCartaValido(value) {
    return /^\d{16}$/.test(normalizeText(value).replace(/[\s-]+/g, ''));
}

function isCvvValido(value) {
    return /^\d{3,4}$/.test(normalizeText(value));
}

function getErroreScadenzaCarta(value) {
    var scadenza = normalizeText(value);
    if (!/^\d{2}\/\d{2}$/.test(scadenza)) {
        return 'Formato non valido. Usa MM/AA.';
    }
    var mese = parseInt(scadenza.slice(0, 2), 10);
    var anno = 2000 + parseInt(scadenza.slice(3), 10);
    var oggi = new Date();
    if (anno < oggi.getFullYear() || (anno === oggi.getFullYear() && mese < oggi.getMonth() + 1)) {
        return 'La carta e scaduta.';
    }
    if (anno > oggi.getFullYear() + 15) {
        return 'Data di scadenza non realistica.';
    }
    return null;
}

function isMaggiorenneData(value) {
    var testo = normalizeText(value);
    if (!testo) return false;
    var nascita = new Date(testo);
    if (isNaN(nascita.getTime())) return false;
    var oggi = new Date();
    var eta = oggi.getFullYear() - nascita.getFullYear();
    var mesi = oggi.getMonth() - nascita.getMonth();
    if (mesi < 0 || (mesi === 0 && oggi.getDate() < nascita.getDate())) {
        eta--;
    }
    return eta >= 18;
}

window.ValidazioneInput = {
    normalizeText: normalizeText,
    isEmailValida: isEmailValida,
    isTelefonoValido: isTelefonoValido,
    isPasswordForte: isPasswordForte,
    isNomeValido: isNomeValido,
    isViaValida: isViaValida,
    isCapValido: isCapValido,
    isProvinciaValida: isProvinciaValida,
    isLocalitaValida: isLocalitaValida,
    isNomeCartaValido: isNomeCartaValido,
    isDestinatarioValido: isDestinatarioValido,
    isNumeroCartaValido: isNumeroCartaValido,
    isCvvValido: isCvvValido,
    getErroreScadenzaCarta: getErroreScadenzaCarta,
    isMaggiorenneData: isMaggiorenneData
};

function aggiungiErrore(input, messaggio) {
    var gruppo = input.closest('.form-group') || input.parentNode;
    var span = document.createElement('span');
    span.className = 'field-error';
    span.textContent = messaggio;
    gruppo.appendChild(span);
}

document.addEventListener('DOMContentLoaded', function () {
    var regForm = document.getElementById('formRegistrazione');
    if (regForm) {
        regForm.addEventListener('submit', function (e) {
            regForm.querySelectorAll('.field-error').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            function mostraErrore(nomeCampo, messaggio) {
                var input = regForm.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;
                aggiungiErrore(input, messaggio);
                valido = false;
            }

            var nome = normalizeText(regForm.querySelector('[name="nome"]').value);
            var cognome = normalizeText(regForm.querySelector('[name="cognome"]').value);
            var email = normalizeText(regForm.querySelector('[name="email"]').value);
            var password = regForm.querySelector('[name="password"]').value;
            var telefono = regForm.querySelector('[name="telefono"]').value;
            var dataNascita = regForm.querySelector('[name="dataNascita"]').value;

            if (!nome) {
                mostraErrore('nome', 'Campo obbligatorio');
            } else if (!isNomeValido(nome)) {
                mostraErrore('nome', 'Il nome deve avere 2-50 caratteri e contenere lettere reali.');
            }

            if (!cognome) {
                mostraErrore('cognome', 'Campo obbligatorio');
            } else if (!isNomeValido(cognome)) {
                mostraErrore('cognome', 'Il cognome deve avere 2-50 caratteri e contenere lettere reali.');
            }

            if (!email) {
                mostraErrore('email', 'Campo obbligatorio');
            } else if (!isEmailValida(email)) {
                mostraErrore('email', 'Formato email non valido');
            }

            if (!password.trim()) {
                mostraErrore('password', 'Campo obbligatorio');
            } else if (!isPasswordForte(password)) {
                mostraErrore('password', 'Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo');
            }

            if (!normalizeText(telefono)) {
                mostraErrore('telefono', 'Campo obbligatorio');
            } else if (!isTelefonoValido(telefono)) {
                mostraErrore('telefono', 'Numero di telefono non valido');
            }

            if (!dataNascita) {
                mostraErrore('dataNascita', 'Campo obbligatorio');
            } else if (!isMaggiorenneData(dataNascita)) {
                mostraErrore('dataNascita', 'Devi avere almeno 18 anni');
            }

            if (!valido) e.preventDefault();
        });
    }

    var addrForm = document.getElementById('formIndirizzo');
    if (addrForm) {
        addrForm.addEventListener('submit', function (e) {
            addrForm.querySelectorAll('.field-error').forEach(function (el) {
                el.remove();
            });
            var valido = true;

            function mostraErrore(nomeCampo, messaggio) {
                var input = addrForm.querySelector('[name="' + nomeCampo + '"]');
                if (!input) return;
                aggiungiErrore(input, messaggio);
                valido = false;
            }

            var destinatario = normalizeText(addrForm.querySelector('[name="destinatario"]').value);
            var via = normalizeText(addrForm.querySelector('[name="via"]').value);
            var cap = normalizeText(addrForm.querySelector('[name="cap"]').value);
            var citta = normalizeText(addrForm.querySelector('[name="citta"]').value);
            var provincia = normalizeText(addrForm.querySelector('[name="provincia"]').value);
            var paese = normalizeText(addrForm.querySelector('[name="paese"]').value);

            if (!destinatario) {
                mostraErrore('destinatario', 'Campo obbligatorio');
            } else if (!isDestinatarioValido(destinatario)) {
                mostraErrore('destinatario', 'Inserisci nome e cognome del destinatario');
            }

            if (!via) {
                mostraErrore('via', 'Campo obbligatorio');
            } else if (!isViaValida(via)) {
                mostraErrore('via', 'Inserisci un indirizzo completo di numero civico (es. Via Roma 1)');
            }

            if (!cap) {
                mostraErrore('cap', 'Campo obbligatorio');
            } else if (!isCapValido(cap)) {
                mostraErrore('cap', 'Il CAP deve essere di 5 cifre');
            }

            if (!citta) {
                mostraErrore('citta', 'Campo obbligatorio');
            } else if (!isLocalitaValida(citta)) {
                mostraErrore('citta', 'La citta deve contenere lettere reali');
            }

            if (!provincia) {
                mostraErrore('provincia', 'Campo obbligatorio');
            } else if (!isProvinciaValida(provincia)) {
                mostraErrore('provincia', 'La provincia deve avere 2-5 lettere (es. RM)');
            }

            if (!paese) {
                mostraErrore('paese', 'Campo obbligatorio');
            } else if (!isLocalitaValida(paese)) {
                mostraErrore('paese', 'Il paese deve contenere lettere reali');
            }

            if (!valido) e.preventDefault();
        });
    }
});
