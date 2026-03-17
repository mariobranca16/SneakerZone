var filtriForm = null;
var ctx = '';
var debounceTimer = null;

function caricaProdotti() {
    var params = new URLSearchParams();
    new FormData(filtriForm).forEach(function (val, key) {
        if (val.trim() !== '') params.append(key, val.trim());
    });

    fetch(filtriForm.action + (params.toString() ? '?' + params.toString() : ''), {
        headers: {'X-Requested-With': 'XMLHttpRequest'}
    })
        .then(function (res) {
            return res.json();
        })
        .then(function (data) {
            renderRisultati(data);
        })
        .catch(function () {
        });
}

function renderRisultati(data) {
    var countEl = document.querySelector('.catalogo-count');
    if (countEl) {
        var n = data.count;
        countEl.textContent = n + ' prodott' + (n === 1 ? 'o' : 'i') +
            ' trovat' + (n === 1 ? 'o' : 'i');
    }

    var risultati = document.querySelector('.catalogo-risultati');
    var emptyEl = risultati.querySelector('.catalogo-empty');
    var grid = risultati.querySelector('.catalogo-grid');

    if (data.count === 0) {
        if (grid) {
            grid.remove();
            grid = null;
        }
        if (!emptyEl) {
            emptyEl = document.createElement('div');
            emptyEl.className = 'catalogo-empty';
            emptyEl.innerHTML =
                '<i class="ti ti-package catalogo-empty-icon"></i>' +
                '<p>Nessun prodotto trovato con i filtri selezionati.</p>';
            risultati.appendChild(emptyEl);
        }
    } else {
        if (emptyEl) {
            emptyEl.remove();
        }
        if (!grid) {
            grid = document.createElement('div');
            grid.className = 'catalogo-grid';
            risultati.appendChild(grid);
        }
        grid.innerHTML = data.prodotti.map(cardHTML).join('');
    }
}

function cardHTML(p) {
    var thumb = p.imgPath
        ? '<img src="' + ctx + esc(p.imgPath) + '" alt="' + esc(p.nome) + '" loading="lazy">'
        : '';

    var desc = p.descrizione
        ? '<div class="prodotto-descrizione">' + esc(p.descrizione) + '</div>'
        : '';

    var wishlistForm = (typeof isLoggedIn !== 'undefined' && isLoggedIn)
        ? '<form class="form-wishlist" method="post" action="' + ctx + '/add-to-wishlist">' +
        '<input type="hidden" name="idProdotto" value="' + p.id + '"/>' +
        '<button class="btn-primary" type="submit" aria-label="Aggiungi alla wishlist">' +
        '<i class="ti ti-heart"></i>' +
        '</button>' +
        '</form>'
        : '';

    return '<div class="prodotto-card">' +
        '<a class="prodotto-thumb-link" href="' + ctx + '/prodotto?id=' + p.id + '">' +
        '<div class="prodotto-thumb">' + thumb + '</div>' +
        '</a>' +
        '<div class="prodotto-body">' +
        '<h2 class="prodotto-nome">' +
        '<a class="prodotto-nome-link" href="' + ctx + '/prodotto?id=' + p.id + '">' +
        esc(p.nome) +
        '</a>' +
        '</h2>' +
        '<div class="prodotto-meta">' +
        '<span><b>Brand:</b> ' + esc(p.brand) + '</span>' +
        '<span><b>Colore:</b> ' + esc(p.colore) + '</span>' +
        '</div>' +
        desc +
        '</div>' +
        '<div class="prodotto-footer">' +
        '<div class="prodotto-prezzo">' +
        formatPrezzo(p.costo) +
        '<span class="currency">€</span>' +
        '</div>' +
        '<div class="prodotto-azioni">' +
        '<form method="post" action="' + ctx + '/carrello">' +
        '<input type="hidden" name="azione" value="aggiungi"/>' +
        '<input type="hidden" name="id" value="' + p.id + '"/>' +
        '<input type="hidden" name="origine" value="catalogo"/>' +
        '<input type="hidden" name="taglia" value="' + (p.primaTagliaDisp !== null ? p.primaTagliaDisp : '') + '"/>' +
        '<input type="hidden" name="quantita" value="1"/>' +
        '<button class="btn-primary" type="submit">Aggiungi al carrello</button>' +
        '</form>' +
        wishlistForm +
        '</div>' +
        '</div>' +
        '</div>';
}

function esc(s) {
    if (!s) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function formatPrezzo(costo) {
    return parseFloat(costo).toFixed(2).replace('.', ',');
}

document.addEventListener('DOMContentLoaded', function () {
    filtriForm = document.getElementById('filtriForm');
    if (!filtriForm) return;
    ctx = filtriForm.getAttribute('data-ctx') || '';

    filtriForm.querySelectorAll('input[type="radio"]').forEach(function (r) {
        r.addEventListener('change', caricaProdotti);
    });

    filtriForm.querySelectorAll('input[type="text"], input[type="number"]').forEach(function (inp) {
        inp.addEventListener('input', function () {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(caricaProdotti, 400);
        });
    });

    var btnAzzera = document.getElementById('btnAzzera');
    if (btnAzzera) {
        btnAzzera.addEventListener('click', function (e) {
            e.preventDefault();
            filtriForm.reset();
            caricaProdotti();
        });
    }
});
