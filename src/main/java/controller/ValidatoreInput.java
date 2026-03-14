package controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

public final class ValidatoreInput {
    public static final int LUNGHEZZA_MAX_NOME = 50;
    public static final int LUNGHEZZA_MAX_EMAIL = 100;
    public static final int LUNGHEZZA_MIN_PASSWORD = 8;
    public static final int LUNGHEZZA_MAX_PASSWORD = 64;
    public static final int ETA_MINIMA = 18;
    public static final int LUNGHEZZA_MAX_VIA = 100;
    public static final int LUNGHEZZA_MAX_LOCALITA = 100;
    public static final int LUNGHEZZA_MAX_NOME_CARTA = 26;
    public static final int ANNI_MAX_SCADENZA_CARTA = 15;
    public static final int LUNGHEZZA_MAX_NOME_PRODOTTO = 150;
    public static final int LUNGHEZZA_MAX_BRAND = 100;
    public static final int LUNGHEZZA_MAX_COLORE_PRODOTTO = 50;
    public static final int LUNGHEZZA_MAX_DESCRIZIONE_PRODOTTO = 2000;
    public static final int LUNGHEZZA_MIN_TITOLO_RECENSIONE = 3;
    public static final int LUNGHEZZA_MAX_TITOLO_RECENSIONE = 255;
    public static final int LUNGHEZZA_MAX_COMMENTO_RECENSIONE = 2000;

    private static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[A-Za-z0-9](?:[A-Za-z0-9._%+\\-]{0,62}[A-Za-z0-9])?@"
                    + "(?:[A-Za-z0-9](?:[A-Za-z0-9\\-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z]{2,63}$");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^\\+?\\d{8,13}$");
    private static final Pattern PATTERN_MAIUSCOLA = Pattern.compile("[A-Z]");
    private static final Pattern PATTERN_MINUSCOLA = Pattern.compile("[a-z]");
    private static final Pattern PATTERN_CIFRA = Pattern.compile("\\d");
    private static final Pattern PATTERN_CARATTERE_SPECIALE = Pattern.compile("[^A-Za-z0-9]");
    private static final Pattern PATTERN_SPAZIO = Pattern.compile("\\s");
    private static final Pattern PATTERN_NOME =
            Pattern.compile("^(?=.{2,50}$)\\p{L}+(?:[ '\\-]\\p{L}+)*$");
    private static final Pattern PATTERN_VIA =
            Pattern.compile("^(?=.{5,100}$)(?=.*\\p{L})(?=.*\\d)[\\p{L}\\d'.,/\\- ]+$");
    private static final Pattern PATTERN_CAP = Pattern.compile("^\\d{5}$");
    private static final Pattern PATTERN_PROVINCIA = Pattern.compile("^\\p{L}{2,5}$");
    private static final Pattern PATTERN_LOCALITA =
            Pattern.compile("^(?=.{2,100}$)\\p{L}+(?:[ '\\-]\\p{L}+)*$");
    private static final Pattern PATTERN_NOME_COGNOME =
            Pattern.compile("^(?=.{4,100}$)\\p{L}+(?:['\\-]\\p{L}+)*(?:\\s+\\p{L}+(?:['\\-]\\p{L}+)*)+$");
    private static final Pattern PATTERN_NUMERO_CARTA = Pattern.compile("^\\d{16}$");
    private static final Pattern PATTERN_SCADENZA_CARTA = Pattern.compile("^(0[1-9]|1[0-2])/\\d{2}$");
    private static final Pattern PATTERN_CVV = Pattern.compile("^\\d{3,4}$");

    private ValidatoreInput() {
    }

    public static String normalizzaTesto(String valore) {
        return valore == null ? null : valore.trim();
    }

    public static String normalizzaTelefono(String telefono) {
        if (telefono == null) {
            return null;
        }
        return telefono.trim().replaceAll("[\\s-]+", "");
    }

    public static String normalizzaNumeroCarta(String numeroCarta) {
        if (numeroCarta == null) {
            return null;
        }
        return numeroCarta.trim().replaceAll("[\\s-]+", "");
    }

    public static boolean contieneTesto(String valore) {
        return valore != null && !valore.trim().isEmpty();
    }

    public static boolean isEmailValida(String email) {
        String t = normalizzaTesto(email);
        return contieneTesto(t)
                && t.length() <= LUNGHEZZA_MAX_EMAIL
                && !t.contains("..")
                && PATTERN_EMAIL.matcher(t).matches();
    }

    public static boolean isTelefonoValido(String telefono) {
        String t = normalizzaTelefono(telefono);
        return contieneTesto(t)
                && t.length() <= 13
                && PATTERN_TELEFONO.matcher(t).matches();
    }

    public static boolean isPasswordForte(String password) {
        return password != null
                && password.length() >= LUNGHEZZA_MIN_PASSWORD
                && password.length() <= LUNGHEZZA_MAX_PASSWORD
                && !PATTERN_SPAZIO.matcher(password).find()
                && PATTERN_MAIUSCOLA.matcher(password).find()
                && PATTERN_MINUSCOLA.matcher(password).find()
                && PATTERN_CIFRA.matcher(password).find()
                && PATTERN_CARATTERE_SPECIALE.matcher(password).find();
    }

    public static boolean isMinorenne(LocalDate dataNascita) {
        return dataNascita == null || dataNascita.isAfter(LocalDate.now().minusYears(ETA_MINIMA));
    }

    public static boolean isNomeValido(String nome) {
        String t = normalizzaTesto(nome);
        return contieneTesto(t) && PATTERN_NOME.matcher(t).matches();
    }

    public static boolean isViaValida(String via) {
        String t = normalizzaTesto(via);
        return contieneTesto(t)
                && t.length() <= LUNGHEZZA_MAX_VIA
                && PATTERN_VIA.matcher(t).matches();
    }

    public static boolean isCapValido(String cap) {
        String t = normalizzaTesto(cap);
        return contieneTesto(t) && PATTERN_CAP.matcher(t).matches();
    }

    public static boolean isProvinciaValida(String provincia) {
        String t = normalizzaTesto(provincia);
        return contieneTesto(t) && PATTERN_PROVINCIA.matcher(t).matches();
    }

    public static boolean isLocalitaValida(String localita) {
        String t = normalizzaTesto(localita);
        return contieneTesto(t)
                && t.length() <= LUNGHEZZA_MAX_LOCALITA
                && PATTERN_LOCALITA.matcher(t).matches();
    }

    public static boolean isNomeCartaValido(String nome) {
        String t = normalizzaTesto(nome);
        return contieneTesto(t)
                && t.length() >= 3
                && t.length() <= LUNGHEZZA_MAX_NOME_CARTA
                && PATTERN_NOME_COGNOME.matcher(t).matches();
    }

    public static boolean isDestinatarioValido(String dest) {
        String t = normalizzaTesto(dest);
        return contieneTesto(t)
                && t.length() >= 4
                && t.length() <= LUNGHEZZA_MAX_LOCALITA
                && PATTERN_NOME_COGNOME.matcher(t).matches();
    }

    public static boolean isNumeroCartaValido(String numeroCarta) {
        String t = normalizzaNumeroCarta(numeroCarta);
        return contieneTesto(t) && PATTERN_NUMERO_CARTA.matcher(t).matches();
    }

    public static boolean isCvvValido(String cvv) {
        String t = normalizzaTesto(cvv);
        return contieneTesto(t) && PATTERN_CVV.matcher(t).matches();
    }

    public static boolean isTitoloRecensioneValido(String titolo) {
        String t = normalizzaTesto(titolo);
        return contieneTesto(t)
                && t.length() >= LUNGHEZZA_MIN_TITOLO_RECENSIONE
                && t.length() <= LUNGHEZZA_MAX_TITOLO_RECENSIONE;
    }

    public static boolean isCommentoRecensioneValido(String commento) {
        String t = normalizzaTesto(commento);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_COMMENTO_RECENSIONE;
    }

    public static boolean isNomeProdottoValido(String nome) {
        String t = normalizzaTesto(nome);
        return contieneTesto(t) && t.length() <= LUNGHEZZA_MAX_NOME_PRODOTTO;
    }

    public static boolean isBrandProdottoValido(String brand) {
        String t = normalizzaTesto(brand);
        return contieneTesto(t) && t.length() <= LUNGHEZZA_MAX_BRAND;
    }

    public static boolean isColoreProdottoValido(String colore) {
        String t = normalizzaTesto(colore);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_COLORE_PRODOTTO;
    }

    public static boolean isDescrizioneProdottoValida(String descrizione) {
        String t = normalizzaTesto(descrizione);
        return t == null || t.isEmpty() || t.length() <= LUNGHEZZA_MAX_DESCRIZIONE_PRODOTTO;
    }

    public static boolean isGenereProdottoValido(String genere) {
        return "Uomo".equals(genere) || "Donna".equals(genere) || "Unisex".equals(genere);
    }

    public static String getErroreScadenzaCarta(String scadenza) {
        String t = normalizzaTesto(scadenza);
        if (!contieneTesto(t) || !PATTERN_SCADENZA_CARTA.matcher(t).matches()) {
            return "Formato non valido. Usa MM/AA.";
        }

        String[] parti = t.split("/");
        YearMonth scad = YearMonth.of(2000 + Integer.parseInt(parti[1]), Integer.parseInt(parti[0]));
        YearMonth oggi = YearMonth.now();
        if (scad.isBefore(oggi)) {
            return "La carta è scaduta.";
        }
        if (scad.isAfter(oggi.plusYears(ANNI_MAX_SCADENZA_CARTA))) {
            return "Data di scadenza non realistica.";
        }
        return null;
    }
}
