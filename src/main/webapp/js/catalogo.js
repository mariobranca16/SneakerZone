var filtriForm = null;
var debounceTimer = null;

function caricaProdotti() {
    var params = new URLSearchParams();
    new FormData(filtriForm).forEach(function(val, key) {
        if (val.trim() !== '') params.append(key, val.trim());
    });

    fetch(filtriForm.action + (params.toString() ? '?' + params.toString() : ''), {
        headers: {'X-Requested-With': 'XMLHttpRequest'}
    })
    .then(function(res) {
        if (!res.ok) throw new Error();
        return res.text();
    })
    .then(function(html) {
        document.getElementById('risultatiProdotti').innerHTML = html;
    })
    .catch(function() {});
}

document.addEventListener('DOMContentLoaded', function() {
    filtriForm = document.getElementById('filtriForm');
    if (!filtriForm) return;

    filtriForm.querySelectorAll('input[type="radio"]').forEach(function(r) {
        r.addEventListener('change', caricaProdotti);
    });

    filtriForm.querySelectorAll('input[type="text"], input[type="number"]').forEach(function(inp) {
        inp.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(caricaProdotti, 400);
        });
    });

    var btnAzzera = document.getElementById('btnAzzera');
    if (btnAzzera) {
        btnAzzera.addEventListener('click', function(e) {
            e.preventDefault();
            filtriForm.querySelectorAll('input[type="text"], input[type="number"]').forEach(function(inp) {
                inp.value = '';
            });
            filtriForm.querySelectorAll('input[type="radio"]').forEach(function(r) {
                r.checked = r.value === '';
            });
            caricaProdotti();
        });
    }
});
