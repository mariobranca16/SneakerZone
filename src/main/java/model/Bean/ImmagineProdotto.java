package model.Bean;

import java.io.Serial;
import java.io.Serializable;

public class ImmagineProdotto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private long idProdotto;
    private String imgPath;
    private String descrizione;
    private int posizione;

    public ImmagineProdotto() {
    }

    public ImmagineProdotto(long id, long idProdotto, String imgPath, String descrizione, int posizione) {
        this.id = id;
        this.idProdotto = idProdotto;
        this.imgPath = imgPath;
        this.descrizione = descrizione;
        this.posizione = posizione;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getPosizione() {
        return posizione;
    }

    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }

    @Override
    public String toString() {
        return "ImmagineProdotto [ID: " + id +
                ", IdProdotto: " + idProdotto +
                ", ImgPath: " + imgPath +
                ", Descrizione: " + descrizione +
                ", Posizione: " + posizione + "]";
    }
}
