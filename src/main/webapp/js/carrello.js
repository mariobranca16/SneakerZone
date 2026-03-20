function enforceQtyBounds(input) {
    var val = parseInt(input.value);
    if (isNaN(val) || val < 1) input.value = 1;
    else if (val > 99) input.value = 99;
}
function aggiornaTotale(totale) {
    var el = document.getElementById('cart-totale-valore');
    if (el) el.textContent = parseFloat(totale).toFixed(2).replace('.', ',');
}
function inviaFormAjax(form, body, onSuccess) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.getAttribute('action'), true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        if (xhr.status !== 200) {
            form.submit();
            return;
        }
        var data;
        try {
            data = JSON.parse(xhr.responseText);
        } catch (e) {
            form.submit();
            return;
        }
        if (data.success) onSuccess(data);
    };
    xhr.onerror = function () {
        form.submit();
    };
    xhr.send(body);
}
function inviaRimozione(form) {
    var id = form.querySelector('[name="id"]').value;
    var taglia = form.querySelector('[name="taglia"]').value;
    var body = 'azione=rimuovi&id=' + encodeURIComponent(id) + '&taglia=' + encodeURIComponent(taglia) + '&ajax=1';
    var row = form.closest('tr');
    inviaFormAjax(form, body, function (data) {
        if (row) row.remove();
        if (data.vuoto) window.location.reload();
        else aggiornaTotale(data.totale);
    });
}
function inviaAggiornamento(form) {
    var id = form.querySelector('[name="id"]').value;
    var taglia = form.querySelector('[name="taglia"]').value;
    var quantita = form.querySelector('[name="quantita"]').value;
    var body = 'azione=aggiorna&id=' + encodeURIComponent(id) + '&taglia=' + encodeURIComponent(taglia) + '&quantita=' + encodeURIComponent(quantita) + '&ajax=1';
    var row = form.closest('tr');
    inviaFormAjax(form, body, function (data) {
        if (data.rimosso) {
            if (row) row.remove();
            if (data.vuoto) {
                window.location.reload();
                return;
            }
        } else {
            var priceCell = row && row.querySelector('.cart-price');
            if (priceCell) priceCell.textContent = parseFloat(data.subtotale).toFixed(2).replace('.', ',') + ' \u20ac';
            var qtyInput = form.querySelector('.qty-input');
            if (qtyInput) qtyInput.value = data.nuovaQuantita;
        }
        aggiornaTotale(data.totale);
    });
}
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.qty-input').forEach(function (input) {
        input.addEventListener('change', function () {
            enforceQtyBounds(this);
            var form = this.closest('.qty-form');
            if (form) inviaAggiornamento(form);
        });
    });
    document.querySelectorAll('.qty-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            inviaAggiornamento(form);
        });
    });
    document.querySelectorAll('.inline-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            inviaRimozione(form);
        });
    });
});
