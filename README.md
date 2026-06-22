# SneakerZone

E-commerce per la vendita di sneakers online, realizzato per l'esame di Tecnologie Software per il Web presso l'Università degli Studi di Salerno.

Progetto di gruppo, sviluppato insieme a Gaetano Pascarella.

L'applicazione gestisce un negozio di scarpe completo, dalla navigazione del catalogo fino al checkout, con un'area riservata all'amministratore per la gestione di prodotti, ordini e utenti. È costruita seguendo il pattern MVC (Model-View-Controller) senza l'uso di framework esterni.

## Tecnologie

- Java 21 con Jakarta Servlet API e JDBC per il backend
- JSP, JSTL, HTML, CSS e JavaScript per il frontend
- MySQL per il database (`db_progetto`)
- Apache Tomcat 11 come server, con connection pool Tomcat JDBC
- Maven per la build (packaging WAR)
- Chiamate AJAX per le parti dinamiche

## Funzionalità

Utente non registrato:

- navigazione del catalogo con filtri per brand, categoria, genere, taglia e prezzo
- carrello gestito in sessione
- ricerca dei prodotti con autocomplete

Utente registrato:

- registrazione, login e logout
- gestione del profilo, della password e degli indirizzi di spedizione
- checkout con scelta dell'indirizzo
- storico degli ordini con dettaglio
- wishlist e recensioni sui prodotti

Amministratore:

- pannello dedicato
- gestione dei prodotti (creazione, modifica ed eliminazione, con taglie e immagini)
- gestione degli ordini e aggiornamento del loro stato
- gestione degli utenti e moderazione delle recensioni

## Schema del database

Il database `db_progetto` è composto dalle seguenti tabelle principali:

- `Utente`: dati anagrafici e credenziali (password in hash)
- `Prodotto`: nome, brand, costo, colore, genere
- `Prodotto_Taglia`: disponibilità per taglia
- `Immagine_Prodotto`: immagini associate al prodotto
- `Categoria` e `Prodotto_Categoria`: categorie e relazione N:M con i prodotti
- `IndirizzoSpedizione`: indirizzi salvati per ogni utente
- `Ordine` e `DettaglioOrdine`: testata e righe degli ordini, con i dati salvati al momento dell'acquisto
- `Recensione`: recensioni con voto e testo
- `Wishlist`: prodotti salvati dall'utente

## Struttura del progetto

```
src/main/
├── java/
│   ├── controller/          servlet per la gestione delle richieste HTTP
│   │   ├── admin/           servlet dell'area amministratore
│   │   ├── filter/          filtri per autenticazione, ruoli e sessione
│   │   └── util/            validazione lato server
│   └── model/
│       ├── Bean/            entità di dominio (Prodotto, Utente, Ordine...)
│       ├── DAO/             accesso al database via JDBC
│       └── ConPool.java     connection pool (Tomcat JDBC)
├── resources/
│   └── db_progetto.sql      schema e dati iniziali
└── webapp/
    ├── WEB-INF/jsp/         viste JSP (utente e admin)
    ├── css/                 fogli di stile
    ├── js/                  script client e validazione
    ├── images/              immagini di prodotti e categorie
    └── data/                JSON statici (province, nazioni) per l'autocomplete
```

## Come avviarlo

Prerequisiti: JDK 21+, Apache Tomcat 11+, MySQL 8+, Maven 3.8+.

1. Clonare il repository:

   ```bash
   git clone https://github.com/mariobranca16/SneakerZone.git
   ```

2. Importare lo schema e i dati iniziali:

   ```sql
   source src/main/resources/db_progetto.sql
   ```

3. Aprire `src/main/java/model/ConPool.java` e inserire le proprie credenziali MySQL al posto dei segnaposto `MY_USERNAME` e `MY_PASSWORD`.

4. Generare il pacchetto e portarlo su Tomcat:

   ```bash
   mvn clean package
   ```

   Copiare il file `.war` da `target/` nella cartella `webapps/` di Tomcat, oppure configurare il progetto direttamente dall'IDE.

5. Aprire l'applicazione su `http://localhost:8080/SneakerZone`.

## Sicurezza

- password cifrate con hash SHA-256 nel database
- validazione degli input sia lato client sia lato server
- filtri servlet per il controllo degli accessi in base al ruolo
- trasporto HTTPS configurato in `web.xml`
- gestione centralizzata degli errori (pagine 404 e 500)
