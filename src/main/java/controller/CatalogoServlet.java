package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.DAO.CategoriaDAO;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "catalogo", urlPatterns = "/catalogo")
public class CatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoria = request.getParameter("categoria");
        String q = request.getParameter("q");
        String prezzoMinP = request.getParameter("prezzoMin");
        String prezzoMaxP = request.getParameter("prezzoMax");
        String genere = request.getParameter("genere");

        Double prezzoMin = null;
        if (prezzoMinP != null && !prezzoMinP.isBlank()) {
            try { double v = Double.parseDouble(prezzoMinP.trim()); if (v >= 0) prezzoMin = v; } catch (NumberFormatException ignored) {}
        }
        Double prezzoMax = null;
        if (prezzoMaxP != null && !prezzoMaxP.isBlank()) {
            try { double v = Double.parseDouble(prezzoMaxP.trim()); if (v >= 0) prezzoMax = v; } catch (NumberFormatException ignored) {}
        }

        boolean anyFilter = (categoria != null && !categoria.isBlank())
                || (q != null && !q.isBlank())
                || prezzoMin != null || prezzoMax != null
                || (genere != null && !genere.isBlank());

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        List<Prodotto> prodotti = anyFilter
                ? prodottoDAO.doRetrieveByFiltriRandom(categoria, q, prezzoMin, prezzoMax, genere)
                : prodottoDAO.doRetrieveAllRandom();
        ordinaRandom(prodotti);

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("tutteCategorie", new CategoriaDAO().doRetrieveAllUsed());

        request.setAttribute("filtroCategoria", categoria);
        request.setAttribute("filtroQ", q);
        request.setAttribute("filtroPrezzoMin", prezzoMinP);
        request.setAttribute("filtroPrezzoMax", prezzoMaxP);
        request.setAttribute("filtroGenere", genere);

        HttpSession session = request.getSession(false);
        if (session != null) {
            String flashErrore = (String) session.getAttribute("flash_erroreCarrello");
            if (flashErrore != null) {
                request.setAttribute("erroreCarrello", flashErrore);
                session.removeAttribute("flash_erroreCarrello");
            }
            String flashMessaggio = (String) session.getAttribute("flash_messaggio");
            if (flashMessaggio != null) {
                request.setAttribute("messaggio", flashMessaggio);
                session.removeAttribute("flash_messaggio");
            }
        }

        if ("1".equals(request.getParameter("successoWishlist")))
            request.setAttribute("messaggio", "Prodotto aggiunto alla wishlist");

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            request.getRequestDispatcher("/WEB-INF/jsp/catalogo_risultati.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(request, response);
    }

    private static final String ORDINE_KEY = "catalogoOrdine";

    private List<Long> getOrCreateOrdine() {
        @SuppressWarnings("unchecked")
        List<Long> ordine = (List<Long>) getServletContext().getAttribute(ORDINE_KEY);
        if (ordine == null) {
            List<Prodotto> tutti = new ProdottoDAO().doRetrieveAll();
            List<Long> ids = new ArrayList<>();
            for (Prodotto p : tutti) ids.add(p.getId());
            Collections.shuffle(ids);
            getServletContext().setAttribute(ORDINE_KEY, ids);
            return ids;
        }
        return ordine;
    }

    private void ordinaRandom(List<Prodotto> prodotti) {
        List<Long> ordine = getOrCreateOrdine();
        Map<Long, Integer> pos = new HashMap<>();
        for (int i = 0; i < ordine.size(); i++) pos.put(ordine.get(i), i);
        prodotti.sort(Comparator.comparingInt(p -> pos.getOrDefault(p.getId(), Integer.MAX_VALUE)));
    }

}
