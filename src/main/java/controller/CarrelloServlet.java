package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;
import model.bean.IndirizzoSpedizione;
import model.bean.Prodotto;
import model.bean.Utente;
import model.dao.IndirizzoSpedizioneDAO;
import model.dao.ProdottoDAO;
import model.dao.ProdottoTagliaDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "carrello", urlPatterns = "/carrello")
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String azione = request.getParameter("azione");
        String origine = request.getParameter("origine");

        long idProdotto;
        try {
            idProdotto = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        String tagliaParam = request.getParameter("taglia");

        if ("rimuovi".equalsIgnoreCase(azione)) {
            if (tagliaParam != null && !tagliaParam.isBlank()) {
                carrello.rimuoviProdotto(idProdotto, Integer.parseInt(tagliaParam));
            }
            if (isAjax) {
                boolean vuoto = carrello.getProdotti().isEmpty();
                double totale = carrello.calcolaTotale();
                sendJson(response, 200, String.format(
                        "{\"success\":true,\"vuoto\":%b,\"totale\":\"%.2f\"}", vuoto, totale));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        if ("aggiorna".equalsIgnoreCase(azione)) {
            if (tagliaParam != null && !tagliaParam.isBlank()) {
                int tagliaAggiorna;
                try {
                    tagliaAggiorna = Integer.parseInt(tagliaParam);
                } catch (NumberFormatException e) {
                    if (isAjax) {
                        sendJson(response, 400, "{\"success\":false}");
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/carrello");
                    return;
                }
                int nuovaQuantita;
                try {
                    nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                } catch (NumberFormatException e) {
                    nuovaQuantita = 0;
                }
                carrello.rimuoviProdotto(idProdotto, tagliaAggiorna);
                if (nuovaQuantita > 0) {
                    Prodotto prodottoAggiorna = new ProdottoDAO().doRetrieveByKey(idProdotto);
                    if (prodottoAggiorna != null) {
                        int disponibilita = new ProdottoTagliaDAO().doRetrieveDisponibilita(idProdotto, tagliaAggiorna);
                        int qty = Math.min(nuovaQuantita, disponibilita);
                        if (qty > 0) {
                            carrello.aggiungiProdotto(prodottoAggiorna, tagliaAggiorna, qty);
                        }
                    }
                }
                if (isAjax) {
                    int qtyEffettiva = 0;
                    double subtotale = 0;
                    for (Carrello.ItemCarrello item : carrello.getProdotti()) {
                        if (item.getProdotto().getId() == idProdotto && item.getTaglia() == tagliaAggiorna) {
                            qtyEffettiva = item.getQuantita();
                            subtotale = item.getQuantita() * item.getProdotto().getCosto();
                            break;
                        }
                    }
                    boolean rimosso = qtyEffettiva == 0;
                    boolean vuoto = carrello.getProdotti().isEmpty();
                    double totale = carrello.calcolaTotale();
                    sendJson(response, 200, String.format(
                            "{\"success\":true,\"nuovaQuantita\":%d,\"subtotale\":\"%.2f\",\"totale\":\"%.2f\",\"rimosso\":%b,\"vuoto\":%b}",
                            qtyEffettiva, subtotale, totale, rimosso, vuoto));
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        if (tagliaParam == null || tagliaParam.isBlank()) {
            if ("prodotto".equalsIgnoreCase(origine)) {
                redirectToProdotto(request, response, idProdotto, "erroreCarrello");
                return;
            }

            if ("wishlist".equalsIgnoreCase(origine)) {
                response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
            } else {
                session.setAttribute("flash_erroreCarrello", "Seleziona una taglia");
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        int tagliaInt;
        try {
            tagliaInt = Integer.parseInt(tagliaParam);
        } catch (NumberFormatException e) {
            if ("prodotto".equalsIgnoreCase(origine)) {
                redirectToProdotto(request, response, idProdotto, "erroreCarrello");
                return;
            }

            if ("wishlist".equalsIgnoreCase(origine)) {
                response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
            } else {
                session.setAttribute("flash_erroreCarrello", "Taglia non valida");
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        int quantita;
        try {
            quantita = Integer.parseInt(request.getParameter("quantita"));
        } catch (NumberFormatException e) {
            quantita = 0;
        }

        if (quantita <= 0) {
            if ("prodotto".equalsIgnoreCase(origine)) {
                redirectToProdotto(request, response, idProdotto, "erroreCarrello");
                return;
            }

            if ("wishlist".equalsIgnoreCase(origine)) {
                response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
            } else {
                session.setAttribute("flash_erroreCarrello", "Quantita non valida");
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            if ("prodotto".equalsIgnoreCase(origine)) {
                redirectToProdotto(request, response, idProdotto, "erroreCarrello");
                return;
            }

            if ("wishlist".equalsIgnoreCase(origine)) {
                response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
            } else {
                session.setAttribute("flash_erroreCarrello", "Prodotto non disponibile");
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
        int disponibilita = ptDAO.doRetrieveDisponibilita(idProdotto, tagliaInt);

        if (quantita > disponibilita) {
            if ("prodotto".equalsIgnoreCase(origine)) {
                redirectToProdotto(request, response, idProdotto, "erroreCarrello");
                return;
            }

            if ("wishlist".equalsIgnoreCase(origine)) {
                response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
            } else {
                session.setAttribute("flash_erroreCarrello", "Quantita richiesta superiore alla disponibilita del prodotto");
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        carrello.aggiungiProdotto(prodotto, tagliaInt, quantita);

        if ("prodotto".equalsIgnoreCase(origine)) {
            redirectToProdotto(request, response, idProdotto, "successoCarrello");
            return;
        }

        if ("wishlist".equalsIgnoreCase(origine)) {
            response.sendRedirect(request.getContextPath() + "/wishlist?successo=1");
        } else {
            session.setAttribute("flash_messaggio", "Prodotto aggiunto al carrello");
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = (Utente) request.getSession().getAttribute("utenteConnesso");
        if (utente != null) {
            IndirizzoSpedizioneDAO indirizzoDAO = new IndirizzoSpedizioneDAO();
            List<IndirizzoSpedizione> indirizzi = indirizzoDAO.doRetrieveByUtente(utente.getId());
            request.setAttribute("indirizziUtente", indirizzi);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(request, response);
    }

    private void sendJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    private void redirectToProdotto(HttpServletRequest request, HttpServletResponse response, long idProdotto, String esito)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto + "&" + esito + "=1");
    }

}
