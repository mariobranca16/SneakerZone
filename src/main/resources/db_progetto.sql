DROP DATABASE IF EXISTS db_progetto;
CREATE DATABASE IF NOT EXISTS db_progetto;

USE db_progetto;

CREATE TABLE Utente
(
    id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email              VARCHAR(255) NOT NULL UNIQUE,
    passwordHash       VARCHAR(64)  NOT NULL,
    isAdmin            BOOLEAN      NOT NULL DEFAULT FALSE,
    nome               VARCHAR(255) NOT NULL,
    cognome            VARCHAR(255) NOT NULL,
    telefono           VARCHAR(13)  NOT NULL,
    data_di_nascita    DATE         NOT NULL,
    data_registrazione DATE         NOT NULL
);

CREATE TABLE Categoria
(
    id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Prodotto
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(255)   NOT NULL,
    descrizione TEXT,
    brand       VARCHAR(100)   NOT NULL,
    costo       DECIMAL(10, 2) NOT NULL,
    colore      VARCHAR(50)
);

CREATE TABLE Prodotto_Taglia
(
    prodotto_id BIGINT UNSIGNED,
    taglia      INT NOT NULL,
    quantita    INT NOT NULL CHECK (quantita >= 0),
    PRIMARY KEY (prodotto_id, taglia),
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE
);

CREATE TABLE Immagine_Prodotto
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    prodotto_id BIGINT UNSIGNED NOT NULL,
    imgPath     VARCHAR(255)    NOT NULL,
    descrizione VARCHAR(255),
    posizione   INT DEFAULT 0,
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE
);

CREATE TABLE Prodotto_Categoria
(
    prodotto_id  BIGINT UNSIGNED,
    categoria_id BIGINT UNSIGNED,
    PRIMARY KEY (prodotto_id, categoria_id),
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES Categoria (id) ON DELETE CASCADE
);

CREATE TABLE IndirizzoSpedizione
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id    BIGINT UNSIGNED NOT NULL,
    destinatario VARCHAR(255)    NOT NULL,
    via          VARCHAR(255)    NOT NULL,
    citta        VARCHAR(100)    NOT NULL,
    provincia    VARCHAR(100)    NOT NULL,
    cap          VARCHAR(10)     NOT NULL,
    paese        VARCHAR(100)    NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES Utente (id) ON DELETE CASCADE
);

CREATE TABLE Ordine
(
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id               BIGINT UNSIGNED                                                NOT NULL,
    indirizzo_spedizione_id BIGINT UNSIGNED                                                NOT NULL,
    data_ordine             DATE                                                           NOT NULL,
    stato_ordine            ENUM ('IN_ELABORAZIONE', 'SPEDITO', 'CONSEGNATO', 'ANNULLATO') NOT NULL DEFAULT 'IN_ELABORAZIONE',
    data_consegna           DATE,
    FOREIGN KEY (utente_id) REFERENCES Utente (id) ON DELETE CASCADE,
    FOREIGN KEY (indirizzo_spedizione_id) REFERENCES IndirizzoSpedizione (id)
);

CREATE TABLE Dettaglio_Ordine
(
    ordine_id   BIGINT UNSIGNED,
    prodotto_id BIGINT UNSIGNED,
    taglia      INT            NOT NULL,
    quantita    INT            NOT NULL CHECK (quantita > 0),
    costo       DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (ordine_id, prodotto_id, taglia),
    FOREIGN KEY (ordine_id) REFERENCES Ordine (id) ON DELETE CASCADE,
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE
);

CREATE TABLE MetodoPagamento
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id    BIGINT UNSIGNED NOT NULL UNIQUE,
    nome_carta   VARCHAR(255)    NOT NULL,
    numero_carta VARCHAR(16)     NOT NULL,
    scadenza     VARCHAR(5)      NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES Utente (id) ON DELETE CASCADE
);

CREATE TABLE Wishlist
(
    utente_id   BIGINT UNSIGNED,
    prodotto_id BIGINT UNSIGNED,
    PRIMARY KEY (utente_id, prodotto_id),
    FOREIGN KEY (utente_id) REFERENCES Utente (id) ON DELETE CASCADE,
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE
);

CREATE TABLE Recensione
(
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id       BIGINT UNSIGNED,
    prodotto_id     BIGINT UNSIGNED,
    titolo          VARCHAR(255) NOT NULL,
    valutazione     INT          NOT NULL CHECK (valutazione BETWEEN 1 AND 5),
    commento        TEXT,
    data_recensione DATE         NOT NULL,
    UNIQUE (utente_id, prodotto_id),
    FOREIGN KEY (utente_id) REFERENCES Utente (id) ON DELETE CASCADE,
    FOREIGN KEY (prodotto_id) REFERENCES Prodotto (id) ON DELETE CASCADE
);

