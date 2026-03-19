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
                btn.disabled = false;
                if (data.success) {
                    mostraNotifica('Prodotto aggiunto alla wishlist');
                    aggiornaBadgeWishlist(data.count);
                }
            })
            .catch(function() {
                btn.disabled = false;
                form.submit();
            });
        });
    });
});
