function enforceQtyBounds(input) {
    var val = parseInt(input.value);
    if (isNaN(val) || val < 1) input.value = 1;
    else if (val > 99) input.value = 99;
}

function aggiornaTotale(totale) {
    var el = document.getElementById('cart-totale-valore');
    if (el) el.textContent = parseFloat(totale).toFixed(2).replace('.', ',');
}

function initRimuovi() {
    document.querySelectorAll('.inline-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();

            var params = new URLSearchParams();
            new FormData(form).forEach(function (value, key) {
                params.append(key, value);
            });

            params.append('ajax', '1');
            fetch(form.getAttribute('action'), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
                .then(function (res) {
                    return res.json();
                })
                .then(function (data) {
                    if (!data.success) return;
                    var row = form.closest('tr');
                    if (row) row.remove();
                    if (data.vuoto) {
                        window.location.reload();
                    } else {
                        aggiornaTotale(data.totale);
                    }
                })
                .catch(function () {
                    form.submit();
                });
        });
    });
}

function initAggiorna() {
    document.querySelectorAll('.qty-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();

            var params = new URLSearchParams();
            new FormData(form).forEach(function (value, key) {
                params.append(key, value);
            });
            var row = form.closest('tr');

            params.append('ajax', '1');
            fetch(form.getAttribute('action'), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
                .then(function (res) {
                    return res.json();
                })
                .then(function (data) {
                    if (!data.success) return;
                    if (data.rimosso) {
                        if (row) row.remove();
                        if (data.vuoto) {
                            window.location.reload();
                            return;
                        }
                    } else {
                        var priceCell = row && row.querySelector('.cart-price');
                        if (priceCell) {
                            priceCell.textContent = parseFloat(data.subtotale).toFixed(2).replace('.', ',') + ' €';
                        }
                        var qtyInput = form.querySelector('.qty-input');
                        if (qtyInput) qtyInput.value = data.nuovaQuantita;
                    }
                    aggiornaTotale(data.totale);
                })
                .catch(function () {
                    form.submit();
                });
        });
    });
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.qty-input').forEach(function (input) {
        input.addEventListener('change', function () {
            enforceQtyBounds(this);
            var form = this.closest('.qty-form');
            if (form) form.requestSubmit();
        });
    });
    initRimuovi();
    initAggiorna();
});
