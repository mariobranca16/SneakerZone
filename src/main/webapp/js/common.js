function autoHideAlerts() {
    document.querySelectorAll('.alert-success, .alert-error, .alert-danger').forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.4s';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 400);
        }, 3000);
    });
}
function mostraNotifica(testo) {
    var notifica = document.createElement('div');
    notifica.className = 'alert alert-success';
    notifica.textContent = testo;
    document.body.appendChild(notifica);
    setTimeout(function () {
        notifica.style.transition = 'opacity 0.4s';
        notifica.style.opacity = '0';
        setTimeout(function () {
            notifica.remove();
        }, 400);
    }, 3000);
}
function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    if (!input) return;
    var icon = btn.querySelector('i');
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('ti-eye', 'ti-eye-off');
    } else {
        input.type = 'password';
        icon.classList.replace('ti-eye-off', 'ti-eye');
    }
}
function initDropdown() {
    var toggle = document.querySelector('.user-menu-toggle');
    var dropdown = toggle && toggle.closest('.dropdown');
    if (!toggle || !dropdown) return;
    toggle.addEventListener('click', function (e) {
        e.stopPropagation();
        var isOpen = dropdown.classList.toggle('is-open');
        toggle.setAttribute('aria-expanded', isOpen);
    });
    document.addEventListener('click', function () {
        dropdown.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
    });
}
function initConfirmButtons() {
    document.addEventListener('click', function (e) {
        var btn = e.target.closest('[data-confirm]');
        if (!btn) return;
        var message = btn.getAttribute('data-confirm');
        if (!confirm(message)) {
            e.preventDefault();
            e.stopPropagation();
        }
    });
}
document.addEventListener('DOMContentLoaded', function () {
    autoHideAlerts();
    initDropdown();
    initConfirmButtons();
});
function apriFormIndirizzo(titolo, testoPulsante, action) {
    var wrap = document.getElementById('indirizzoFormWrap');
    var form = document.getElementById('formIndirizzo');
    if (!wrap || !form) return;
    document.getElementById('indirizzoFormTitolo').textContent = titolo;
    document.getElementById('btnSalvaIndirizzo').textContent = testoPulsante;
    document.getElementById('btnAnnullaEdit').hidden = false;
    form.action = action;
    wrap.classList.add('open');
    wrap.scrollIntoView({behavior: 'smooth', block: 'nearest'});
}
function chiudiFormIndirizzo() {
    var wrap = document.getElementById('indirizzoFormWrap');
    var form = document.getElementById('formIndirizzo');
    if (!wrap || !form) return;
    form.reset();
    document.getElementById('indirizzoId').value = '';
    var from = document.getElementById('indirizzoFrom');
    if (from) from.value = '';
    document.getElementById('indirizzoFormTitolo').textContent = 'Nuovo indirizzo';
    document.getElementById('btnSalvaIndirizzo').textContent = 'Salva indirizzo';
    document.getElementById('btnAnnullaEdit').hidden = true;
    form.action = wrap.dataset.actionNuovo;
    wrap.classList.remove('open');
}
