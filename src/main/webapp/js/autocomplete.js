var provinceData = null;
var nazioniData = null;

function fetchProvince(callback) {
    if (provinceData) { callback(provinceData); return; }
    var base = (document.querySelector('meta[name="ctx"]') || {}).content || '';
    fetch(base + '/data/province.json')
        .then(function (res) { return res.json(); })
        .then(function (data) { provinceData = data; callback(data); })
        .catch(function () {});
}

function fetchNazioni(callback) {
    if (nazioniData) { callback(nazioniData); return; }
    var base = (document.querySelector('meta[name="ctx"]') || {}).content || '';
    fetch(base + '/data/nazioni.json')
        .then(function (res) { return res.json(); })
        .then(function (data) { nazioniData = data; callback(data); })
        .catch(function () {});
}

function initAutocomplete(input, fetchFn, matchFn, getLabelFn, getValueFn) {
    if (!input || input.dataset.acInit === 'true') return;
    input.dataset.acInit = 'true';

    var dropdown = document.createElement('ul');
    dropdown.className = 'ac-dropdown';
    dropdown.setAttribute('role', 'listbox');
    dropdown.hidden = true;
    input.parentElement.appendChild(dropdown);

    var activeIdx = -1;

    function chiudi() {
        dropdown.innerHTML = '';
        dropdown.hidden = true;
        activeIdx = -1;
    }

    function setAttivo(items, idx) {
        items.forEach(function (li) { li.classList.remove('ac-active'); });
        if (idx >= 0 && idx < items.length) {
            items[idx].classList.add('ac-active');
            items[idx].scrollIntoView({block: 'nearest'});
        }
        activeIdx = idx;
    }

    input.addEventListener('input', function () {
        var q = input.value.trim();
        if (q.length < 1) { chiudi(); return; }

        fetchFn(function (data) {
            var matches = matchFn(data, q);
            if (matches.length === 0) { chiudi(); return; }

            dropdown.innerHTML = '';
            activeIdx = -1;
            matches.forEach(function (item) {
                var li = document.createElement('li');
                li.setAttribute('role', 'option');
                li.textContent = getLabelFn(item);
                li.dataset.valore = getValueFn(item);
                li.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    input.value = li.dataset.valore;
                    chiudi();
                    input.dispatchEvent(new Event('change'));
                });
                dropdown.appendChild(li);
            });
            dropdown.hidden = false;
        });
    });

    input.addEventListener('keydown', function (e) {
        var items = dropdown.querySelectorAll('li');
        if (!items.length) return;
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            setAttivo(items, Math.min(activeIdx + 1, items.length - 1));
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            setAttivo(items, Math.max(activeIdx - 1, 0));
        } else if (e.key === 'Enter' && activeIdx >= 0) {
            e.preventDefault();
            input.value = items[activeIdx].dataset.valore;
            chiudi();
        } else if (e.key === 'Escape') {
            chiudi();
        }
    });

    input.addEventListener('blur', function () {
        setTimeout(chiudi, 150);
    });
}

function initAutocompleteProvincia(input) {
    initAutocomplete(
        input,
        fetchProvince,
        function (data, q) {
            var upper = q.toUpperCase();
            return data.filter(function (p) {
                return p.sigla.startsWith(upper) || p.nome.toLowerCase().startsWith(q.toLowerCase());
            }).slice(0, 8);
        },
        function (p) { return p.sigla + ' - ' + p.nome; },
        function (p) { return p.sigla; }
    );
}

function initAutocompletePaese(input) {
    initAutocomplete(
        input,
        fetchNazioni,
        function (data, q) {
            return data.filter(function (n) {
                return n.toLowerCase().startsWith(q.toLowerCase());
            }).slice(0, 8);
        },
        function (n) { return n; },
        function (n) { return n; }
    );
}

document.addEventListener('DOMContentLoaded', function () {
    ['provincia', 'editProvincia'].forEach(function (id) {
        initAutocompleteProvincia(document.getElementById(id));
    });
    ['paese', 'editPaese'].forEach(function (id) {
        initAutocompletePaese(document.getElementById(id));
    });
});
