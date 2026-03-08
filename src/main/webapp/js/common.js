function autoHideAlerts() {
    document.querySelectorAll('.alert-success, .alert-error, .alert-danger').forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.4s';
            alert.style.opacity = '0';
            setTimeout(function() { alert.remove(); }, 400);
        }, 3000);
    });
}

function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    var icon = btn.querySelector('i');
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('fa-eye', 'fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.replace('fa-eye-slash', 'fa-eye');
    }
}

function initDropdown() {
    var toggle = document.querySelector('.user-menu-toggle');
    var dropdown = toggle && toggle.closest('.dropdown');
    if (!toggle || !dropdown) return;

    toggle.addEventListener('click', function(e) {
        e.stopPropagation();
        var isOpen = dropdown.classList.toggle('is-open');
        toggle.setAttribute('aria-expanded', isOpen);
    });

    document.addEventListener('click', function() {
        dropdown.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
    });
}

function initConfirmButtons() {
    document.addEventListener('click', function(e) {
        var btn = e.target.closest('[data-confirm]');
        if (!btn) return;
        var message = btn.getAttribute('data-confirm');
        if (!confirm(message)) {
            e.preventDefault();
            e.stopPropagation();
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    autoHideAlerts();
    initDropdown();
    initConfirmButtons();
});
