package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.*;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.OrdineDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * Gestisce il flusso di acquisto.
 * Accessibile solo agli utenti registrati grazie a LoginFilter.
 */
@WebServlet(name = "checkout", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet {
    // Mostra la pagina di checkout. Se il carrello è vuoto, rimanda l'utente al carrello con un errore.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione esistente e il carrello salvato
        HttpSession session = request.getSession(false);
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        // Se il carrello è vuoto non ha senso procedere con il checkout
        if (carrello == null || carrello.getProdotti().isEmpty()) {
            request.setAttribute("erroreCarrello", "Il tuo carrello e vuoto. Aggiungi almeno un prodotto per procedere.");
            request.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(request, response); // quindi rimanda alla pagina
            return;
        }

        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        // Recupera gli indirizzi salvati e precompila il form con il primo disponibile
        List<IndirizzoSpedizione> indirizziSalvati = new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId());
        if (!indirizziSalvati.isEmpty()) {
            request.setAttribute("indirizzoPrecompilato", indirizziSalvati.get(0));
        }
        request.setAttribute("indirizzi", indirizziSalvati);
        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
    }

    // Elabora il form di checkout: valida indirizzo e carta, crea l'ordine, svuota il carrello.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione, il carrello e l'utente autenticato
        HttpSession session = request.getSession(false);
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        // verifica di sicurezza, il carrello non deve essere vuoto al momento del checkout
        if (carrello == null || carrello.getProdotti().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        boolean hasError = false;

        // recupera e normalizza i campi dell'indirizzo di spedizione
        String destinatario = ValidatoreInput.normalizzaTesto(request.getParameter("destinatario"));
        String via = ValidatoreInput.normalizzaTesto(request.getParameter("via"));
        String cap = ValidatoreInput.normalizzaTesto(request.getParameter("cap"));
        String citta = ValidatoreInput.normalizzaTesto(request.getParameter("citta"));
        String provincia = ValidatoreInput.normalizzaTesto(request.getParameter("provincia"));
        String paese = ValidatoreInput.normalizzaTesto(request.getParameter("paese"));

        // validazione di tutti i campi dell'indirizzo di spedizione
        if (!ValidatoreInput.isDestinatarioValido(destinatario)) {
            request.setAttribute("erroreDestinatario", "Inserisci nome e cognome del destinatario.");
            hasError = true;
        }
        if (!ValidatoreInput.isViaValida(via)) {
            request.setAttribute("erroreVia", "Inserisci un indirizzo completo di numero civico (es. Via Roma 1).");
            hasError = true;
        }
        if (!ValidatoreInput.isCapValido(cap)) {
            request.setAttribute("erroreCap", "Il CAP deve essere di esattamente 5 cifre.");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(citta)) {
            request.setAttribute("erroreCitta", "La citta deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }
        if (!ValidatoreInput.isProvinciaValida(provincia)) {
            request.setAttribute("erroreProvincia", "La provincia deve avere 2-5 lettere (es. RM).");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(paese)) {
            request.setAttribute("errorePaese", "Il paese deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }

        // normalizzazione campi della carta per il pagamento
        String nomeCarta = ValidatoreInput.normalizzaTesto(request.getParameter("nomeCarta"));
        // numeroCarta non usa normalizzaTesto perché isNumeroCartaValido chiama internamente normalizzaNumeroCarta
        String numeroCarta = request.getParameter("numeroCarta");
        String scadenza = ValidatoreInput.normalizzaTesto(request.getParameter("scadenza"));

        // validazione dei campi della carta
        if (!ValidatoreInput.isNomeCartaValido(nomeCarta)) {
            request.setAttribute("erroreNomeCarta", "Inserisci nome e cognome come appaiono sulla carta (es. Mario Rossi).");
            hasError = true;
        }
        if (!ValidatoreInput.isNumeroCartaValido(numeroCarta)) {
            request.setAttribute("erroreNumeroCarta", "Inserisci un numero di carta valido (16 cifre).");
            hasError = true;
        }
        if (!ValidatoreInput.isScadenzaCartaValida(scadenza)) {
            request.setAttribute("erroreScadenza", "Inserisci una data di scadenza valida (MM/AA).");
            hasError = true;
        }
        String cvv = ValidatoreInput.normalizzaTesto(request.getParameter("cvv"));
        if (!ValidatoreInput.isCvvValido(cvv)) {
            request.setAttribute("erroreCvv", "CVV non valido (3 o 4 cifre).");
            hasError = true;
        }

        // se ci sono errori ricarica il checkout mantenendo il carrello e gli indirizzi salvati
        if (hasError) {
            List<IndirizzoSpedizione> indirizziErr = new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId());
            if (!indirizziErr.isEmpty()) {
                // se presente, usa il primo indirizzo per la precompilazione dei campi
                request.setAttribute("indirizzoPrecompilato", indirizziErr.get(0));
            }
            request.setAttribute("indirizzi", indirizziErr);
            request.setAttribute("carrello", carrello);
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        // se l'indirizzo inserito esiste già tra quelli salvati, lo riusa invece di duplicarlo
        IndirizzoSpedizioneDAO indirizzoDAO = new IndirizzoSpedizioneDAO();
        IndirizzoSpedizione indirizzo = null;
        for (IndirizzoSpedizione i : indirizzoDAO.doRetrieveByUtente(utente.getId())) {
            if (i.getDestinatario().equalsIgnoreCase(destinatario)
                    && i.getVia().equalsIgnoreCase(via)
                    && i.getCap().equalsIgnoreCase(cap)
                    && i.getCitta().equalsIgnoreCase(citta)
                    && i.getProvincia().equalsIgnoreCase(provincia)
                    && i.getPaese().equalsIgnoreCase(paese)) {
                indirizzo = i;
                break;
            }
        }
        // se invece non esiste ancora, lo crea e lo salva nel db
        if (indirizzo == null) {
            indirizzo = new IndirizzoSpedizione();
            indirizzo.setIdUtente(utente.getId());
            indirizzo.setDestinatario(destinatario);
            indirizzo.setVia(via);
            indirizzo.setCap(cap);
            indirizzo.setCitta(citta);
            indirizzo.setProvincia(provincia);
            indirizzo.setPaese(paese);
            indirizzoDAO.doSave(indirizzo);
        }

        // costruisce l'ordine partendo dal contenuto attuale del carrello
        Ordine ordine = new Ordine();
        ordine.setIdUtente(utente.getId());
        ordine.setIdIndirizzoSpedizione(indirizzo.getId());
        ordine.setDataOrdine(LocalDate.now());
        ordine.setStato(StatoOrdine.IN_ELABORAZIONE);

        // converte ogni elemento del carrello in un dettaglio ordine
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        for (Carrello.ItemCarrello item : carrello.getProdotti()) {
            DettaglioOrdine d = new DettaglioOrdine();
            d.setIdProdotto(item.getProdotto().getId());
            d.setProdotto(item.getProdotto());
            d.setTaglia(item.getTaglia());
            d.setQuantita(item.getQuantita());
            d.setCosto(item.getProdotto().getCosto()); // salva il prezzo del prodotto al momento dell'acquisto
            dettagli.add(d);
        }
        ordine.setDettagliOrdine(dettagli);

        // OrdineDAO.doSave() fa tutto in una sola transazione: inserisce l'ordine, i dettagli
        // e decrementa lo stock. Se la disponibilità è esaurita lancia RuntimeException.
        try {
            new OrdineDAO().doSave(ordine);
        } catch (RuntimeException e) {
            request.setAttribute("erroreStock", "Uno o piu prodotti non sono piu disponibili nella quantita richiesta. Aggiorna il carrello e riprova.");
            request.setAttribute("carrello", carrello);
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        // se l'ordina viene confermato, allora svuota il carrello e mostra la conferma
        carrello.svuotaCarrello();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        request.setAttribute("dataOrdine", ordine.getDataOrdine().format(dtf));
        request.setAttribute("ordine", ordine);
        request.getRequestDispatcher("/WEB-INF/jsp/conferma_ordine.jsp").forward(request, response);
    }
}
