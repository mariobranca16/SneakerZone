class WishlistAjax {

    static init() {
        document.querySelectorAll('.form-wishlist').forEach(function(form) {
            form.addEventListener('submit', function(e) {
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
                .then(function(res) {
                    return res.json().then(function(data) {
                        if (!data.success && data.redirect) {
                            window.location.href = data.redirect;
                            return;
                        }
                        if (data.success) {
                            var icon = btn.querySelector('i');
                            if (icon) {
                                icon.classList.remove('fa-regular');
                                icon.classList.add('fa-solid');
                            }
                            btn.title = 'Aggiunto alla wishlist';
                        }
                        btn.disabled = false;
                    });
                })
                .catch(function() {
                    btn.disabled = false;
                    form.submit();
                });
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    WishlistAjax.init();
});
