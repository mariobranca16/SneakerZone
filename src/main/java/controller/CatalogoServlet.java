package controller;

import controller.util.ValidatoreInput;
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
import java.util.List;

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
            StringBuilder sb = new StringBuilder();
            sb.append("{\"count\":").append(prodotti.size()).append(",\"prodotti\":[");
            for (int i = 0; i < prodotti.size(); i++) {
                Prodotto p = prodotti.get(i);
                sb.append("{")
                        .append("\"id\":").append(p.getId()).append(",")
                        .append("\"nome\":\"").append(escJson(p.getNome())).append("\",")
                        .append("\"brand\":\"").append(escJson(p.getBrand())).append("\",")
                        .append("\"colore\":\"").append(escJson(p.getColore())).append("\",")
                        .append("\"costo\":").append(p.getCosto()).append(",")
                        .append("\"descrizione\":\"").append(escJson(p.getDescrizione())).append("\",")
                        .append("\"imgPath\":\"").append(escJson(p.getImgPath())).append("\",")
                        .append("\"primaTagliaDisp\":").append(p.primaTagliaDisp())
                        .append("}");
                if (i < prodotti.size() - 1) sb.append(",");
            }
            sb.append("]}");
            ValidatoreInput.sendJson(response, 200, sb.toString());
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(request, response);
    }

    private static String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
