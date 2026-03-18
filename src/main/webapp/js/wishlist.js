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
                    btn.textContent = 'Aggiunto';
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
