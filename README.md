# SneakerZone 👟 

> E-commerce per la vendita online di sneakers, sviluppato per il corso di **Tecnologie Software per il Web** presso l'Università degli Studi di Salerno.
>
> Progetto realizzato da:
> - **Mario Branca**
> - **Gaetano Pascarella**

---

## Stack tecnologico

| Layer      | Tecnologie                                    |
|------------|-----------------------------------------------|
| Backend    | Java 21, Jakarta Servlet API 6.1, JDBC        |
| Frontend   | JSP + JSTL, HTML/CSS, JavaScript              |
| Database   | MySQL (`db_progetto`)                         |
| Server     | Apache Tomcat 11, Tomcat JDBC Connection Pool |
| Build      | Maven (packaging WAR)                         |
| Dinamismo  | AJAX (fetch JSON)                             |

Il progetto segue il pattern **MVC (Model–View–Controller)** senza l'uso di framework esterni.

---

## Funzionalità

### Utente non registrato
- Navigazione del catalogo con filtri (brand, categoria, genere, taglia, prezzo)
- Carrello gestito in sessione
- Ricerca prodotti con autocomplete

### Utente registrato
- Registrazione, login e logout
- Gestione profilo, password e indirizzi di spedizione
- Checkout con selezione indirizzo
- Storico ordini con dettaglio
- Wishlist
- Recensioni sui prodotti

### Amministratore
- Pannello admin dedicato
- Gestione prodotti (creazione, modifica, eliminazione con taglie e immagini)
- Gestione ordini (visualizzazione e aggiornamento stato)
- Gestione utenti
- Moderazione recensioni

> **Nota:** il prezzo viene salvato al momento dell'ordine (`DettaglioOrdine.prezzoUnitario`) per garantire la coerenza dei dati nel tempo.

---

## Schema del database

Il database `db_progetto` include le seguenti tabelle principali:

| Tabella               | Descrizione                                      |
|-----------------------|--------------------------------------------------|
| `Utente`              | Dati anagrafici e credenziali (password in hash) |
| `Prodotto`            | Nome, brand, costo, colore, genere               |
| `Prodotto_Taglia`     | Disponibilità per taglia                         |
| `Immagine_Prodotto`   | Path immagine associata al prodotto              |
| `Categoria`           | Categorie (es. Running, Basketball, Lifestyle)   |
| `Prodotto_Categoria`  | Relazione N:M prodotto–categoria                 |
| `IndirizzoSpedizione` | Indirizzi salvati per utente                     |
| `Ordine`              | Testata ordine con stato e snapshot indirizzo    |
| `DettaglioOrdine`     | Righe ordine con snapshot prodotti|
| `Recensione`          | Recensioni con voto e testo                      |
| `Wishlist`            | Prodotti salvati dall'utente                     |

---

## Installazione

### Prerequisiti

- JDK 21+
- Apache Tomcat 11+
- MySQL 8+
- Maven 3.8+

### 1. Clona il repository

```bash
git clone https://github.com/mariobranca16/SneakerZone.git
```

### 2. Configura il database

Importa lo schema ed i dati iniziali:

```sql
source src/main/resources/db_progetto.sql
```

Apri la classe `src/main/java/model/ConPool.java` e sostituisci i segnaposto con le credenziali della tua installazione MySQL:

```java
p.setUrl("jdbc:mysql://localhost:3306/db_progetto?serverTimezone=" + TimeZone.getDefault().getID()); // MY_URL
p.setUsername("MY_USERNAME"); // es. root
p.setPassword("MY_PASSWORD"); // la tua password MySQL
```

> I valori `MY_URL`, `MY_USERNAME` e `MY_PASSWORD` sono segnaposto e vanno obbligatoriamente sostituiti prima di avviare l'applicazione.

### 3. Build e deploy

```bash
mvn clean package
```

Copia il file `.war` generato in `target/` nella cartella `webapps/` di Tomcat, oppure configura il progetto direttamente nell'IDE (es. IntelliJ IDEA).

### 4. Avvia l'applicazione

```
http://localhost:8080/SneakerZone
```

---

## Struttura del progetto

```
src/main/
├── java/
│   ├── controller/          # Servlet — gestione delle richieste HTTP
│   │   ├── admin/           # Servlet area amministratore
│   │   ├── filter/          # Filtri (autenticazione, ruoli, sessione)
│   │   └── util/            # ValidatoreInput — validazione server-side
│   └── model/
│       ├── Bean/            # Entità di dominio (Prodotto, Utente, Ordine…)
│       ├── DAO/             # Accesso al database via JDBC
│       └── ConPool.java     # Connection pool (Tomcat JDBC)
├── resources/
│   └── db_progetto.sql      # Schema e dati iniziali
└── webapp/
    ├── WEB-INF/jsp/         # Viste JSP (utente e admin)
    ├── css/                 # Fogli di stile per pagina
    ├── js/                  # Script per pagina + validazione client
    ├── images/              # Immagini prodotti e categorie
    └── data/                # JSON statici (province, nazioni) per autocomplete
```

---

## Sicurezza

- Password cifrate con hash SHA-256 nel database
- Validazione degli input lato client (`validazione.js`) e lato server (`ValidatoreInput.java`)
- Filtri servlet per il controllo degli accessi basato sui ruoli (`AdminFilter`, `LoginFilter`, `SessionFilter`)
- Trasporto HTTPS configurato tramite `CONFIDENTIAL` in `web.xml`
- Gestione centralizzata degli errori (pagine 404, 500)

---

## Design

Il design è volutamente semplice e minimale.

| Colore    | Utilizzo             |
|-----------|----------------------|
| `#FFFFFF` | Sfondo               |
| `#000000` | Elementi principali  |
| `#FF4500` | Elementi interattivi |
