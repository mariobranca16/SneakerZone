(function () {
    var cache = {};
    function fetchData(url, cb) {
        if (cache[url]) { cb(cache[url]); return; }
        fetch(url)
            .then(function (r) { return r.json(); })
            .then(function (d) { cache[url] = d; cb(d); })
            .catch(function () {});
    }
    function removeDropdown(input) {
        var group = input.closest('.form-group');
        if (!group) return;
        var existing = group.querySelector('.ac-dropdown');
        if (existing) existing.remove();
    }
    function showDropdown(input, options, mapFn) {
        var group = input.closest('.form-group');
        if (!group) return;
        removeDropdown(input);
        var val = input.value.trim().toLowerCase();
        if (!val) return;
        var filtered = options.filter(function (o) {
            return mapFn(o).toLowerCase().indexOf(val) === 0;
        }).slice(0, 10);
        if (!filtered.length) return;
        var ul = document.createElement('ul');
        ul.className = 'ac-dropdown';
        filtered.forEach(function (o) {
            var li = document.createElement('li');
            li.textContent = mapFn(o);
            li.addEventListener('mousedown', function (e) {
                e.preventDefault();
                input.value = mapFn(o);
                removeDropdown(input);
            });
            ul.appendChild(li);
        });
        group.appendChild(ul);
    }
    function attachAutocomplete(input, options, mapFn) {
        input.removeAttribute('list');
        input.setAttribute('autocomplete', 'off');
        input.addEventListener('input', function () {
            showDropdown(input, options, mapFn);
        });
        input.addEventListener('focus', function () {
            if (input.value.trim()) showDropdown(input, options, mapFn);
        });
        input.addEventListener('blur', function () {
            setTimeout(function () { removeDropdown(input); }, 150);
        });
    }
    document.addEventListener('DOMContentLoaded', function () {
        var base = (document.querySelector('meta[name="ctx"]') || {}).content || '';
        var provinceInputs = document.querySelectorAll('[data-ac="province"]');
        var nazioniInputs  = document.querySelectorAll('[data-ac="nazioni"]');
        if (provinceInputs.length) {
            fetchData(base + '/data/province.json', function (data) {
                provinceInputs.forEach(function (input) {
                    attachAutocomplete(input, data, function (p) { return p.sigla; });
                });
            });
        }
        if (nazioniInputs.length) {
            fetchData(base + '/data/nazioni.json', function (data) {
                nazioniInputs.forEach(function (input) {
                    attachAutocomplete(input, data, function (n) { return n; });
                });
            });
        }
    });
})();
