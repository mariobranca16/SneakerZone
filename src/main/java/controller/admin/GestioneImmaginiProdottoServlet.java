package controller.admin;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.DAO.ImmagineProdottoDAO;
import model.DAO.ProdottoDAO;

import java.io.IOException;

@WebServlet(name = "gestioneImmaginiProdottoAdmin", urlPatterns = "/admin/prodotto/immagine")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class GestioneImmaginiProdottoServlet extends HttpServlet {

    private static final String IMG_DIR = "/images/prodotti/";
    private static final int MAX_DESCRIZIONE_LENGTH = 255;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idProdottoParam = request.getParameter("idProdotto");
        if (idProdottoParam == null || idProdottoParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        long idProdotto;
        try {
            idProdotto = Long.parseLong(idProdottoParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        if (new ProdottoDAO().doRetrieveByKey(idProdotto) == null) {
            request.getSession().setAttribute("flashErrore", "Prodotto non trovato.");
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        String azione = request.getParameter("azione");

        if ("elimina".equals(azione)) {
            String idImgParam = request.getParameter("idImmagine");
            if (idImgParam != null && !idImgParam.isBlank()) {
                try {
                    long idImg = Long.parseLong(idImgParam);
                    new ImmagineProdottoDAO().doDeleteByKeyWithFile(idImg, idProdotto, getServletContext().getRealPath(""));
                } catch (NumberFormatException ignored) {
                }
            }

        } else if ("upload".equals(azione)) {
            Part filePart = request.getPart("fileImmagine");
            if (filePart == null || filePart.getSize() <= 0) {
                request.getSession().setAttribute("flashErrore", "Seleziona un'immagine da caricare.");
            } else {
                String descrizione = ValidatoreInput.normalizzaTesto(request.getParameter("descrizione"));
                if (descrizione != null && descrizione.length() > MAX_DESCRIZIONE_LENGTH) {
                    request.getSession().setAttribute("flashErrore", "La descrizione dell'immagine non puo superare 255 caratteri.");
                } else {
                    String contentType = filePart.getContentType();
                    if (contentType != null) {
                        int separatorIndex = contentType.indexOf(';');
                        if (separatorIndex >= 0) {
                            contentType = contentType.substring(0, separatorIndex);
                        }
                        contentType = contentType.trim().toLowerCase();
                    }

                    boolean contentTypeValido = "image/jpeg".equals(contentType)
                            || "image/png".equals(contentType)
                            || "image/webp".equals(contentType);

                    if (!contentTypeValido) {
                        request.getSession().setAttribute("flashErrore", "Formato immagine non supportato. Usa JPG, PNG o WEBP.");
                    } else {
                        String uploadDir = getServletContext().getRealPath(IMG_DIR);
                        if (uploadDir == null || uploadDir.isBlank()) {
                            request.getSession().setAttribute("flashErrore", "Impossibile determinare la cartella di upload.");
                        } else {
                            String ext = "image/png".equals(contentType) ? ".png" : "image/webp".equals(contentType) ? ".webp" : ".jpg";
                            String nomeFile = idProdotto + "_" + System.currentTimeMillis() + ext;
                            String imgPath = IMG_DIR + nomeFile;
                            byte[] fileContent;
                            try (var inputStream = filePart.getInputStream()) {
                                fileContent = inputStream.readAllBytes();
                            }
                            try {
                                new ImmagineProdottoDAO().doSaveWithFile(
                                        idProdotto,
                                        fileContent,
                                        uploadDir,
                                        nomeFile,
                                        imgPath,
                                        descrizione == null ? "" : descrizione
                                );
                                request.getSession().setAttribute("flashMessaggio", "Immagine caricata correttamente.");
                            } catch (RuntimeException e) {
                                request.getSession().setAttribute("flashErrore", "Impossibile salvare l'immagine caricata.");
                            }
                        }
                    }
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/prodotto?id=" + idProdotto);
    }

}
