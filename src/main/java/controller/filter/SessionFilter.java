package controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Carrello;

import java.io.IOException;

/*
 * Filtro che si occupa di inizializzare il carrello in sessione.
 * Viene eseguito su tutte le richieste, tranne quelle per le risorse statiche.
 */
@WebFilter("/*")
public class SessionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // ricava il path della richiesta togliendo il context path dell'applicazione
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // se la richiesta non è per una risorsa statica, controlla la sessione e inizializza il carrello
        if (!isStaticResource(path)) {
            HttpSession session = request.getSession();
            // crea il carrello solo la prima volta, così se è già presente non viene sovrascritto
            if (session.getAttribute("carrello") == null) {
                session.setAttribute("carrello", new Carrello());
            }
        }
        // dopo il controllo lascia proseguire normalmente la richiesta
        chain.doFilter(request, response);
    }

    // Helper privato per riconoscere risorse statiche ed evitare di creare sessioni inutili
    private boolean isStaticResource(String path) {
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/data/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".webp")
                || path.endsWith(".svg")
                || path.endsWith(".ico");
    }
}
