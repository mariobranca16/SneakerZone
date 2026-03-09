package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Prodotto;
import model.bean.ProdottoTaglia;
import model.dao.CategoriaDAO;
import model.dao.ProdottoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "catalogo", urlPatterns = "/catalogo")
public class CatalogoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoria  = request.getParameter("categoria");
        String q          = request.getParameter("q");
        String prezzoMinP = request.getParameter("prezzoMin");
        String prezzoMaxP = request.getParameter("prezzoMax");
        String genere     = request.getParameter("genere");

        Double prezzoMin = parsePrezzo(prezzoMinP);
        Double prezzoMax = parsePrezzo(prezzoMaxP);

        boolean anyFilter = (categoria != null && !categoria.isBlank())
                         || (q != null && !q.isBlank())
                         || prezzoMin != null || prezzoMax != null
                         || (genere != null && !genere.isBlank());

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        List<Prodotto> prodotti = anyFilter
                ? prodottoDAO.doRetrieveByFiltri(categoria, q, prezzoMin, prezzoMax, genere)
                : prodottoDAO.doRetrieveAll();

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("tutteCategorie", new CategoriaDAO().doRetrieveAll());

        // Valori correnti dei filtri (per ripopolare la sidebar)
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
            sendJsonProdotti(response, prodotti);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(request, response);
    }

    private void sendJsonProdotti(HttpServletResponse response, List<Prodotto> prodotti) throws IOException {
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
              .append("\"primaTagliaDisp\":").append(primaTagliaDisp(p))
              .append("}");
            if (i < prodotti.size() - 1) sb.append(",");
        }
        sb.append("]}");

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(sb.toString());
    }

    private static String primaTagliaDisp(Prodotto p) {
        if (p.getTaglie() != null) {
            for (ProdottoTaglia pt : p.getTaglie()) {
                if (pt.getQuantita() > 0) return String.valueOf(pt.getTaglia());
            }
        }
        return "null";
    }

    private static String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Double parsePrezzo(String param) {
        if (param == null || param.isBlank()) return null;
        try {
            double v = Double.parseDouble(param.trim());
            return v >= 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
