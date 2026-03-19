function mostraNotificaWishlist(testo) {
    var notifica = document.createElement('div');
    notifica.className = 'alert alert-success';
    notifica.textContent = testo;
    document.body.appendChild(notifica);
    setTimeout(function () {
        notifica.style.transition = 'opacity 0.4s';
        notifica.style.opacity = '0';
        setTimeout(function () { notifica.remove(); }, 400);
    }, 3000);
}

function aggiornaBadgeWishlist(count) {
    var badge = document.getElementById('wishlist-badge');
    var link = document.querySelector('a.topbar-icon-btn[aria-label="Wishlist"]');
    if (!link) return;
    if (count > 0) {
        if (!badge) {
            badge = document.createElement('span');
            badge.className = 'cart-badge';
            badge.id = 'wishlist-badge';
            link.appendChild(badge);
        }
        badge.textContent = count;
    } else if (badge) {
        badge.remove();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.form-wishlist').forEach(function(form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            var btn = form.querySelector('button[type="submit"]');
            var idProdotto = form.querySelector('[name="idProdotto"]').value;

            btn.disabled = true;

            fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: 'idProdotto=' + encodeURIComponent(idProdotto)
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (!data.success && data.redirect) {
                    window.location.href = data.redirect;
                    return;
                }
                if (data.success) {
                    mostraNotificaWishlist('Prodotto aggiunto alla wishlist');
                    aggiornaBadgeWishlist(data.count);
                    btn.disabled = false;
                } else {
                    btn.disabled = false;
                }
            })
            .catch(function() {
                btn.disabled = false;
                form.submit();
            });
        });
    });
});
