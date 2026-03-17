var provinceData = null;
var nazioniData = null;

function fetchProvince(callback) {
    if (provinceData) {
        callback(provinceData);
        return;
    }
    var ctx = document.querySelector('meta[name="ctx"]');
    var base = ctx ? ctx.content : '';
    fetch(base + '/data/province.json')
        .then(function (res) {
            return res.json();
        })
        .then(function (data) {
            provinceData = data;
            callback(data);
        });
}

function fetchNazioni(callback) {
    if (nazioniData) {
        callback(nazioniData);
        return;
    }
    var ctx = document.querySelector('meta[name="ctx"]');
    var base = ctx ? ctx.content : '';
    fetch(base + '/data/nazioni.json')
        .then(function (res) {
            return res.json();
        })
        .then(function (data) {
            nazioniData = data;
            callback(data);
        });
}

function initAutocompleteProvincia(input) {
    if (!input || input.dataset.acProvinciaInit === 'true') {
        return;
    }
    input.dataset.acProvinciaInit = 'true';

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
        items.forEach(function (li) {
            li.classList.remove('ac-active');
        });
        if (idx >= 0 && idx < items.length) {
            items[idx].classList.add('ac-active');
            items[idx].scrollIntoView({block: 'nearest'});
        }
        activeIdx = idx;
    }

    input.addEventListener('input', function () {
        var q = input.value.trim();
        if (q.length < 1) {
            chiudi();
            return;
        }

        fetchProvince(function (data) {
            var upper = q.toUpperCase();
            var matches = data.filter(function (p) {
                return p.sigla.startsWith(upper) || p.nome.toLowerCase().startsWith(q.toLowerCase());
            }).slice(0, 8);

            if (matches.length === 0) {
                chiudi();
                return;
            }

            dropdown.innerHTML = '';
            activeIdx = -1;
            matches.forEach(function (p) {
                var li = document.createElement('li');
                li.setAttribute('role', 'option');
                li.textContent = p.sigla + ' - ' + p.nome;
                li.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    input.value = p.sigla;
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
            input.value = items[activeIdx].textContent.split(' - ')[0].trim();
            chiudi();
        } else if (e.key === 'Escape') {
            chiudi();
        }
    });

    input.addEventListener('blur', function () {
        setTimeout(chiudi, 150);
    });
}

function initAutocompletePaese(input) {
    if (!input || input.dataset.acPaeseInit === 'true') {
        return;
    }
    input.dataset.acPaeseInit = 'true';

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
        items.forEach(function (li) {
            li.classList.remove('ac-active');
        });
        if (idx >= 0 && idx < items.length) {
            items[idx].classList.add('ac-active');
            items[idx].scrollIntoView({block: 'nearest'});
        }
        activeIdx = idx;
    }

    input.addEventListener('input', function () {
        var q = input.value.trim();
        if (q.length < 1) {
            chiudi();
            return;
        }

        fetchNazioni(function (data) {
            var matches = data.filter(function (n) {
                return n.toLowerCase().startsWith(q.toLowerCase());
            }).slice(0, 8);

            if (matches.length === 0) {
                chiudi();
                return;
            }

            dropdown.innerHTML = '';
            activeIdx = -1;
            matches.forEach(function (n) {
                var li = document.createElement('li');
                li.setAttribute('role', 'option');
                li.textContent = n;
                li.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    input.value = n;
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
            input.value = items[activeIdx].textContent;
            chiudi();
        } else if (e.key === 'Escape') {
            chiudi();
        }
    });

    input.addEventListener('blur', function () {
        setTimeout(chiudi, 150);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    ['provincia', 'editProvincia'].forEach(function (id) {
        initAutocompleteProvincia(document.getElementById(id));
    });

    ['paese', 'editPaese'].forEach(function (id) {
        initAutocompletePaese(document.getElementById(id));
    });
});
