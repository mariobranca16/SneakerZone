package controller.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

public class ValidatoreInput {
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
        if (!contieneTesto(t) || t.length() > LUNGHEZZA_MAX_EMAIL)
            return false;
        if (t.contains(".."))
            return false;
        return Pattern.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$", t);
    }

    public static boolean isTelefonoValido(String telefono) {
        String t = normalizzaTelefono(telefono);
        if (!contieneTesto(t) || t.length() > 13)
            return false;
        return Pattern.matches("^\\+?\\d{8,13}$", t);
    }

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

    public static boolean isMinorenne(LocalDate dataNascita) {
        return dataNascita == null || dataNascita.isAfter(LocalDate.now().minusYears(ETA_MINIMA));
    }

    public static boolean isNomeValido(String nome) {
        String t = normalizzaTesto(nome);
        if (!contieneTesto(t) || t.length() < 2 || t.length() > 50)
            return false;
        return Pattern.matches("^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$", t);
    }

    public static boolean isViaValida(String via) {
        String t = normalizzaTesto(via);
        if (!contieneTesto(t) || t.length() < 5 || t.length() > LUNGHEZZA_MAX_VIA)
            return false;
        if (!Pattern.matches("^[A-Za-zÀ-ÿ0-9 .,'/\\-]+$", t))
            return false;
        boolean hasLettera = false;
        boolean hasNumero = false;
        for (char c : t.toCharArray()) {
            if (Character.isLetter(c)) hasLettera = true;
            if (Character.isDigit(c)) hasNumero = true;
        }
        return hasLettera && hasNumero;
    }

    public static boolean isCapValido(String cap) {
        String t = normalizzaTesto(cap);
        return contieneTesto(t) && Pattern.matches("^\\d{5}$", t);
    }

    public static boolean isProvinciaValida(String provincia) {
        String t = normalizzaTesto(provincia);
        return contieneTesto(t) && Pattern.matches("^[A-Za-z]{2,5}$", t);
    }

    public static boolean isLocalitaValida(String localita) {
        String t = normalizzaTesto(localita);
        if (!contieneTesto(t) || t.length() < 2 || t.length() > LUNGHEZZA_MAX_LOCALITA)
            return false;
        return Pattern.matches("^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$", t);
    }

    public static boolean isNomeCartaValido(String nome) {
        String t = normalizzaTesto(nome);
        if (!contieneTesto(t) || t.length() < 3 || t.length() > LUNGHEZZA_MAX_NOME_CARTA)
            return false;
        return Pattern.matches("^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*\\s+[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$", t);
    }

    public static boolean isDestinatarioValido(String dest) {
        String t = normalizzaTesto(dest);
        if (!contieneTesto(t) || t.length() < 4 || t.length() > LUNGHEZZA_MAX_LOCALITA)
            return false;
        return Pattern.matches("^[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*\\s+[A-Za-zÀ-ÿ]+([ '-][A-Za-zÀ-ÿ]+)*$", t);
    }

    public static boolean isNumeroCartaValido(String numeroCarta) {
        String t = normalizzaNumeroCarta(numeroCarta);
        return contieneTesto(t) && Pattern.matches("^\\d{16}$", t);
    }

    public static boolean isCvvValido(String cvv) {
        String t = normalizzaTesto(cvv);
        return contieneTesto(t) && Pattern.matches("^\\d{3,4}$", t);
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
        if (!contieneTesto(t) || !Pattern.matches("^\\d{2}/\\d{2}$", t)) {
            return "Formato non valido. Usa MM/AA.";
        }

        String[] parti = t.split("/");
        YearMonth scad;
        try {
            scad = YearMonth.of(2000 + Integer.parseInt(parti[1]), Integer.parseInt(parti[0]));
        } catch (Exception e) {
            return "Formato non valido. Usa MM/AA.";
        }
        YearMonth oggi = YearMonth.now();
        if (scad.isBefore(oggi)) {
            return "La carta è scaduta.";
        }
        if (scad.isAfter(oggi.plusYears(ANNI_MAX_SCADENZA_CARTA))) {
            return "Data di scadenza non realistica.";
        }
        return null;
    }

    public static void sendJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
