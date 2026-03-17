function initWishlist() {
    document.querySelectorAll('.form-wishlist').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();

            var btn = form.querySelector('button[type="submit"]');
            var idProdotto = form.querySelector('[name="idProdotto"]').value;
            var action = form.getAttribute('action');

            btn.disabled = true;

            fetch(action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: 'idProdotto=' + encodeURIComponent(idProdotto)
            })
                .then(function (res) {
                    return res.json().then(function (data) {
                        if (!data.success && data.redirect) {
                            window.location.href = data.redirect;
                            return;
                        }
                        if (data.success) {
                            var icon = btn.querySelector('i');
                            if (icon) {
                                icon.classList.replace('ti-heart', 'ti-hearts');
                            }
                            btn.title = 'Aggiunto alla wishlist';
                            mostraToastWishlist('Prodotto aggiunto alla wishlist!', 'success');
                            aggiornaBadgeWishlist(data.count);
                        }
                        btn.disabled = false;
                    });
                })
                .catch(function () {
                    btn.disabled = false;
                    form.submit();
                });
        });
    });
}

function mostraToastWishlist(testo, tipo) {
    var toast = document.createElement('div');
    toast.className = 'alert alert-' + tipo;
    toast.textContent = testo;
    document.body.appendChild(toast);
    setTimeout(function () {
        toast.style.transition = 'opacity 0.4s';
        toast.style.opacity = '0';
        setTimeout(function () {
            toast.remove();
        }, 400);
    }, 2800);
}

function aggiornaBadgeWishlist(count) {
    var badge = document.getElementById('wishlist-badge');
    if (count > 0) {
        if (badge) {
            badge.textContent = count;
        } else {
            var link = document.querySelector('a[aria-label="Wishlist"]');
            if (link) {
                var span = document.createElement('span');
                span.className = 'cart-badge';
                span.id = 'wishlist-badge';
                span.textContent = count;
                link.appendChild(span);
            }
        }
    } else if (badge) {
        badge.remove();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    initWishlist();
});
