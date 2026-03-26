package controller.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

/*
 * Classe di supporto per normalizzare e validare gli input inseriti dall'utente.
 */
public class ValidatoreInput {

    // costanti usate nelle varie validazioni di lunghezza e formato
    public static final int LUNGHEZZA_MAX_NOME = 50;
    public static final int LUNGHEZZA_MAX_EMAIL = 100;
    public static final int LUNGHEZZA_MIN_PASSWORD = 8;
    public static final int LUNGHEZZA_MAX_PASSWORD = 64;
    public static final int ETA_MINIMA = 18;
    public static final int LUNGHEZZA_MAX_VIA = 100;
    public static final int LUNGHEZZA_MAX_LOCALITA = 100;
    public static final int LUNGHEZZA_MAX_NOME_CARTA = 26;
    public static final int LUNGHEZZA_MAX_NOME_PRODOTTO = 150;
    public static final int LUNGHEZZA_MAX_BRAND = 100;
    public static final int LUNGHEZZA_MAX_COLORE_PRODOTTO = 50;
    public static final int LUNGHEZZA_MAX_DESCRIZIONE_PRODOTTO = 2000;
    public static final int LUNGHEZZA_MIN_TITOLO_RECENSIONE = 3;
    public static final int LUNGHEZZA_MAX_TITOLO_RECENSIONE = 255;
    public static final int LUNGHEZZA_MAX_COMMENTO_RECENSIONE = 2000;

    // rimuove gli spazi iniziali e finali dal testo
    public static String normalizzaTesto(String valore) {
        return valore == null ? null : valore.trim();
    }

    // rimuove spazi e trattini dal numero di telefono, per validare formati scritti in maniera diversa
    public static String normalizzaTelefono(String telefono) {
        if (telefono == null) {
            return null;
        }
        return telefono.trim().replaceAll("[\\s-]+", "");
    }

    // rimuove spazi e trattini dal numero della carta, così che il controllo avviene sullo stesso formato
    public static String normalizzaNumeroCarta(String numeroCarta) {
        if (numeroCarta == null) {
            return null;
        }
        return numeroCarta.trim().replaceAll("[\\s-]+", "");
    }

    // per verificare che una stringa abbia effettivamente testo e non solo spazi vuoti
    public static boolean contieneTesto(String valore) {
        return valore != null && !valore.trim().isEmpty();
    }


    // valida l'email, controllando che sia presente, la lunghezza e il formato previsto dalla regex
    public static boolean isEmailValida(String email) {
        String t = normalizzaTesto(email);
        if (!contieneTesto(t) || t.length() > LUNGHEZZA_MAX_EMAIL)
            return false;
        if (t.contains("..")) // blocca il caso in cui sono presenti due punti consecutivi
            return false;
        return Pattern.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$", t);
    }

    // valida il telefono, accettando solo cifre e un eventuale prefisso con "+"
    public static boolean isTelefonoValido(String telefono) {
        String t = normalizzaTelefono(telefono);
        if (!contieneTesto(t) || t.length() > 13)
            return false;
        return Pattern.matches("^\\+?\\d{8,13}$", t);
    }

    // valida la password controllando che:
    // abbia una lungheza valida, almeno una minuscola, una maiuscola, una cifra e un simbolo
    public static boolean isPasswordForte(String password) {
        if (password == null || password.length() < LUNGHEZZA_MIN_PASSWORD || password.length() > LUNGHEZZA_MAX_PASSWORD)
            return false;
        if (password.contains(" "))
            return false;
        boolean hasMaiuscola = false;
        boolean hasMinuscola = false;
        boolean hasCifra = false;
        boolean hasSpeciale = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasMaiuscola = true;
            else if (Character.isLowerCase(c)) hasMinuscola = true;
            else if (Character.isDigit(c)) hasCifra = true;
            else hasSpeciale = true;
        }
        return hasMaiuscola && hasMinuscola && hasCifra && hasSpeciale;
    }

    // controlla se la data di nascita inserita è di un utente minorenne
    public static boolean isMinorenne(LocalDate dataNascita) {
        return dataNascita == null || dataNascita.isAfter(LocalDate.now().minusYears(ETA_MINIMA));
    }

    // valida il nome dell'utente, controllando che sia presente, la lunghezza e i caratteri ammessi dalla regex
    public static boolean isNomeValido(String nome) {
        String t = normalizzaTesto(nome);
        if (!contieneTesto(t) || t.length() < 2 || t.length() > 50)
            return false;
        return Pattern.matches("^[A-Za-z]+([ '-][A-Za-z]+)*$", t);
    }

    // valida la via, controllando la lunghezza, i caratteri ammessi e la presenza sia di lettere che numeri
    public static boolean isViaValida(String via) {
        String t = normalizzaTesto(via);
        if (!contieneTesto(t) || t.length() < 5 || t.length() > LUNGHEZZA_MAX_VIA)
            return false;
        if (!Pattern.matches("^[A-Za-z0-9 .,'/\\-]+$", t))
            return false;
        boolean hasLettera = false;
        boolean hasNumero = false;
        // verifica che sia presente anche il numero civico oltre al nome della via
        for (char c : t.toCharArray()) {
            if (Character.isLetter(c)) hasLettera = true;
            if (Character.isDigit(c)) hasNumero = true;
        }
        return hasLettera && hasNumero;
    }

    // valida il cap accettando solo 5 cifre
    public static boolean isCapValido(String cap) {
        String t = normalizzaTesto(cap);
        return contieneTesto(t) && Pattern.matches("^\\d{5}$", t);
    }

    // valida la provincia con un formato breve di sole lettere
    public static boolean isProvinciaValida(String provincia) {
        String t = normalizzaTesto(provincia);
        return contieneTesto(t) && Pattern.matches("^[A-Za-z]{2,5}$", t);
    }

    // valida la località, controllando la lunghezza i caratteri ammessi
    public static boolean isLocalitaValida(String localita) {
        String t = normalizzaTesto(localita);
        if (!contieneTesto(t) || t.length() < 2 || t.length() > LUNGHEZZA_MAX_LOCALITA)
            return false;
        return Pattern.matches("^[A-Za-z]+([ '-][A-Za-z]+)*$", t);
    }

    // valida il nome della carta per il pagamento controllando che la stringa contenga sia nome che cognome
    public static boolean isNomeCartaValido(String nome) {
        String t = normalizzaTesto(nome);
        if (!contieneTesto(t) || t.length() < 3 || t.length() > LUNGHEZZA_MAX_NOME_CARTA)
            return false;
        return Pattern.matches("^[A-Za-z]+([ '-][A-Za-z]+)*\\s+[A-Za-z]+([ '-][A-Za-z]+)*$", t);
    }

    // stessa validazione del nome della carta applicato al destinatario della spedizione
    public static boolean isDestinatarioValido(String dest) {
        String t = normalizzaTesto(dest);
        if (!contieneTesto(t) || t.length() < 4 || t.length() > LUNGHEZZA_MAX_LOCALITA)
            return false;
        return Pattern.matches("^[A-Za-z]+([ '-][A-Za-z]+)*\\s+[A-Za-z]+([ '-][A-Za-z]+)*$", t);
    }

    // valida il numero della carta per il pagamento, controllando che abbia 16 cifre
    public static boolean isNumeroCartaValido(String numeroCarta) {
        String t = normalizzaNumeroCarta(numeroCarta);
        return contieneTesto(t) && Pattern.matches("^\\d{16}$", t);
    }

    // valida il cvv accettando solo 3 o 4 cifre
    public static boolean isCvvValido(String cvv) {
        String t = normalizzaTesto(cvv);
        return contieneTesto(t) && Pattern.matches("^\\d{3,4}$", t);
    }

    // controlla che il titolo della recensione sia presente e rientri nei limiti di lunghezza
    public static boolean isTitoloRecensioneValido(String titolo) {
        String t = normalizzaTesto(titolo);
        return contieneTesto(t)
                && t.length() >= LUNGHEZZA_MIN_TITOLO_RECENSIONE
                && t.length() <= LUNGHEZZA_MAX_TITOLO_RECENSIONE;
    }

    // il commento della recensione è facoltativo, però se presente deve rientrare nella lunghezza massima
    public static boolean isCommentoRecensioneValido(String commento) {
        String t = normalizzaTesto(commento);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_COMMENTO_RECENSIONE;
    }

    // valida il nome del prodotto, controllando che sia presente e rientri nella lunghezza massima
    public static boolean isNomeProdottoValido(String nome) {
        String t = normalizzaTesto(nome);
        return contieneTesto(t) && t.length() <= LUNGHEZZA_MAX_NOME_PRODOTTO;
    }

    // valida il brand del prodotto, controllando che sia presente e rientri nella lunghezza massima
    public static boolean isBrandProdottoValido(String brand) {
        String t = normalizzaTesto(brand);
        return contieneTesto(t) && t.length() <= LUNGHEZZA_MAX_BRAND;
    }

    // il colore del prodotto è facoltativo, però se presente deve rientrare nella lunghezza massima
    public static boolean isColoreProdottoValido(String colore) {
        String t = normalizzaTesto(colore);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_COLORE_PRODOTTO;
    }

    // anche la descrizione del prodotto è facoltativa, però anch'essa deve rispettare il limite di lunghezza
    public static boolean isDescrizioneProdottoValida(String descrizione) {
        String t = normalizzaTesto(descrizione);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_DESCRIZIONE_PRODOTTO;
    }

    // controlla che il genere associato al prodotto sia uno dei tre previsti
    public static boolean isGenereProdottoValido(String genere) {
        return "Uomo".equals(genere) || "Donna".equals(genere) || "Unisex".equals(genere);
    }

    // valida la scadenza della carta di pagamento nel formato MM/AA
    public static boolean isScadenzaCartaValida(String scadenza) {
        String t = normalizzaTesto(scadenza);
        // controllo del formato per evitare parsing inutili
        if (!contieneTesto(t) || !Pattern.matches("^\\d{2}/\\d{2}$", t)) {
            return false;
        }
        // conversione della data
        String[] parti = t.split("/");
        YearMonth scad;
        try {
            scad = YearMonth.of(2000 + Integer.parseInt(parti[1]), Integer.parseInt(parti[0]));
        } catch (Exception e) {
            return false;
        }
        // blocca le carte scadute
        YearMonth oggi = YearMonth.now();
        return !scad.isBefore(oggi);
    }

    // serve per inviare una risposta JSON impostando lo stato HTTP e il content type corretto
    // viene usato dalle richieste con AJAX
    public static void sendJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
