var patterns = {
    email: /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/,
    phone: /^\+?\d{8,13}$/,
    upper: /[A-Z]/,
    lower: /[a-z]/,
    digit: /\d/,
    special: /[^A-Za-z0-9]/,
    whitespace: /\s/,
    name: /^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$/,
    street: /^[A-Za-zÀ-ÿ0-9 .,'/\-]+$/,
    cap: /^\d{5}$/,
    province: /^[A-Za-z]{2,5}$/,
    locality: /^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$/,
    fullName: /^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*\s+[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$/,
    cardNumber: /^\d{16}$/,
    cardExpiry: /^\d{2}\/\d{2}$/,
    cvv: /^\d{3,4}$/
};

function normalizeText(value) {
    return value == null ? '' : value.trim();
}

function normalizePhone(value) {
    return normalizeText(value).replace(/[\s-]+/g, '');
}

function normalizeCardNumber(value) {
    return normalizeText(value).replace(/[\s-]+/g, '');
}

function hasText(value) {
    return normalizeText(value) !== '';
}

function isEmailValida(value) {
    var email = normalizeText(value);
    return email.length <= 100 && email.indexOf('..') === -1 && patterns.email.test(email);
}

function isTelefonoValido(value) {
    var telefono = normalizePhone(value);
    return telefono.length <= 13 && patterns.phone.test(telefono);
}

function isPasswordForte(value) {
    var password = value || '';
    if (password.length < 8 || password.length > 64) return false;
    if (patterns.whitespace.test(password)) return false;
    return patterns.upper.test(password) &&
        patterns.lower.test(password) &&
        patterns.digit.test(password) &&
        patterns.special.test(password);
}

function isNomeValido(value) {
    var nome = normalizeText(value);
    return nome.length >= 2 && nome.length <= 50 && patterns.name.test(nome);
}

function isViaValida(value) {
    var via = normalizeText(value);
    if (via.length < 5 || via.length > 100) return false;
    if (!patterns.street.test(via)) return false;
    return /[A-Za-zÀ-ÿ]/.test(via) && /\d/.test(via);
}

function isCapValido(value) {
    return patterns.cap.test(normalizeText(value));
}

function isProvinciaValida(value) {
    return patterns.province.test(normalizeText(value));
}

function isLocalitaValida(value) {
    var localita = normalizeText(value);
    return localita.length >= 2 && localita.length <= 100 && patterns.locality.test(localita);
}

function isNomeCartaValido(value) {
    var nome = normalizeText(value);
    return nome.length >= 3 && nome.length <= 26 && patterns.fullName.test(nome);
}

function isDestinatarioValido(value) {
    var destinatario = normalizeText(value);
    return destinatario.length >= 4 && destinatario.length <= 100 && patterns.fullName.test(destinatario);
}

function isNumeroCartaValido(value) {
    return patterns.cardNumber.test(normalizeCardNumber(value));
}

function isCvvValido(value) {
    return patterns.cvv.test(normalizeText(value));
}

function getErroreScadenzaCarta(value) {
    var scadenza = normalizeText(value);
    var oggi;
    var mese;
    var anno;

    if (!patterns.cardExpiry.test(scadenza)) {
        return 'Formato non valido. Usa MM/AA.';
    }

    mese = parseInt(scadenza.slice(0, 2), 10);
    anno = 2000 + parseInt(scadenza.slice(3), 10);
    oggi = new Date();

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
    var oggi;
    var nascita;
    var eta;
    var mesi;

    if (!testo) {
        return false;
    }

    oggi = new Date();
    nascita = new Date(testo);
    if (Number.isNaN(nascita.getTime())) {
        return false;
    }

    eta = oggi.getFullYear() - nascita.getFullYear();
    mesi = oggi.getMonth() - nascita.getMonth();
    if (mesi < 0 || (mesi === 0 && oggi.getDate() < nascita.getDate())) {
        eta--;
    }
    return eta >= 18;
}

window.ValidazioneInput = {
    patterns: patterns,
    normalizeText: normalizeText,
    normalizePhone: normalizePhone,
    normalizeCardNumber: normalizeCardNumber,
    hasText: hasText,
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
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
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

            if (email === '') {
                mostraErrore('email', 'Campo obbligatorio');
            } else if (!isEmailValida(email)) {
                mostraErrore('email', 'Formato email non valido');
            }

            if (password.trim() === '') {
                mostraErrore('password', 'Campo obbligatorio');
            } else if (!isPasswordForte(password)) {
                mostraErrore('password', 'Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo');
            }

            if (normalizeText(telefono) === '') {
                mostraErrore('telefono', 'Campo obbligatorio');
            } else if (!isTelefonoValido(telefono)) {
                mostraErrore('telefono', 'Numero di telefono non valido');
            }

            if (dataNascita === '') {
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
                var gruppo = input.closest('.form-group') || input.parentNode;
                var span = document.createElement('span');
                span.className = 'field-error';
                span.textContent = messaggio;
                gruppo.appendChild(span);
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

            if (cap === '') {
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
