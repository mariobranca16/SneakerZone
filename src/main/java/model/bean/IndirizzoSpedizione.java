package model.bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class IndirizzoSpedizione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private long idUtente;
    private String destinatario;
    private String via;
    private String citta;
    private String provincia;
    private String cap;
    private String paese;

    public IndirizzoSpedizione(){}

    public IndirizzoSpedizione(long id, long idUtente, String destinatario, String via, String citta, String provincia, String cap, String paese ) {
        this.id = id;
        this.idUtente = idUtente;
        this.destinatario = destinatario;
        this.via = via;
        this.citta = citta;
        this.provincia = provincia;
        this.cap = cap;
        this.paese = paese;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getIdUtente() {
        return idUtente;
    }
    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public String getDestinatario() {
        return destinatario;
    }
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getVia() {
        return via;
    }
    public void setVia(String via) {
        this.via = via;
    }

    public String getCitta() {
        return citta;
    }
    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCap() {
        return cap;
    }
    public void setCap(String cap){
        this.cap = cap;
    }

    public String getPaese() {
        return paese;
    }
    public void setPaese(String paese) {
        this.paese = paese;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IndirizzoSpedizione is = (IndirizzoSpedizione) o;
        return id == is.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IndirizzoSpedizione [ID: " + id +
               ", IdUtente: " + idUtente +
               ", Destinatario: " + destinatario +
               ", Via: " + via +
               ", Città: " + citta +
               ", Provincia: " + provincia +
               ", CAP: " + cap +
               ", Paese: " + paese + "]";
    }
}
