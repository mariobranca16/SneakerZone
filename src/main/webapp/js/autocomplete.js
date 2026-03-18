function popolaDatalist(datalistId, opzioni) {
    var dl = document.getElementById(datalistId);
    if (!dl) return;
    opzioni.forEach(function(val) {
        var opt = document.createElement('option');
        opt.value = val;
        dl.appendChild(opt);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    var base = (document.querySelector('meta[name="ctx"]') || {}).content || '';

    fetch(base + '/data/province.json')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            popolaDatalist('list-province', data.map(function(p) { return p.sigla; }));
        })
        .catch(function() {});

    fetch(base + '/data/nazioni.json')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            popolaDatalist('list-nazioni', data);
        })
        .catch(function() {});
});
