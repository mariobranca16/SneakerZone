// gestisce il popolamento delle datalist per provincia e paese

document.addEventListener('DOMContentLoaded', function () {
    // recupera il context path usato per costruire i percorsi dei file json
    var metaCtx = document.querySelector('meta[name="ctx"]');
    var basePath = metaCtx ? metaCtx.content : '';

    // recupera la datalist delle province
    var listaProvince = document.getElementById('listaProvince');

    // carica e inserisce le province solo se la datalist è presente
    if (listaProvince) {
        fetch(basePath + '/data/province.json')
            .then(function (response) {
                // converte la risposta in json
                return response.json();
            })
            .then(function (data) {
                // aggiunge una option per ogni provincia disponibile
                data.forEach(function (provincia) {
                    var opzione = document.createElement('option');
                    opzione.value = provincia.sigla;
                    listaProvince.appendChild(opzione);
                });
            })
            .catch(function () {
                // in caso di errore il campo resta compilabile manualmente
            });
    }

    // recupera la datalist delle nazioni
    var listaNazioni = document.getElementById('listaNazioni');

    // carica e inserisce le nazioni solo se la datalist è presente
    if (listaNazioni) {
        fetch(basePath + '/data/nazioni.json')
            .then(function (response) {
                // converte la risposta in json
                return response.json();
            })
            .then(function (data) {
                // aggiunge una option per ogni nazione disponibile
                data.forEach(function (nazione) {
                    var opzione = document.createElement('option');
                    opzione.value = nazione;
                    listaNazioni.appendChild(opzione);
                });
            })
            .catch(function () {
                // in caso di errore il campo resta compilabile manualmente
            });
    }
});