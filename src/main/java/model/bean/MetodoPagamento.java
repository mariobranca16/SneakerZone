package model.bean;

import java.io.Serial;
import java.io.Serializable;

public class MetodoPagamento implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private long idUtente;
    private String nomeCarta;
    private String numeroCarta; // 16 cifre senza spazi
    private String scadenza;   // formato MM/AA

    public MetodoPagamento() {}

    public MetodoPagamento(long id, long idUtente, String nomeCarta, String numeroCarta, String scadenza) {
        this.id = id;
        this.idUtente = idUtente;
        this.nomeCarta = nomeCarta;
        this.numeroCarta = numeroCarta;
        this.scadenza = scadenza;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getIdUtente() { return idUtente; }
    public void setIdUtente(long idUtente) { this.idUtente = idUtente; }

    public String getNomeCarta() { return nomeCarta; }
    public void setNomeCarta(String nomeCarta) { this.nomeCarta = nomeCarta; }

    public String getNumeroCarta() { return numeroCarta; }
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }

    public String getScadenza() { return scadenza; }
    public void setScadenza(String scadenza) { this.scadenza = scadenza; }

    /** "**** **** **** XXXX" – ultime 4 cifre visibili */
    public String getNumeroMascherato() {
        if (numeroCarta == null || numeroCarta.length() < 4) return "**** **** **** ****";
        String cifre = numeroCarta.replaceAll("\\s", "");
        return "**** **** **** " + cifre.substring(Math.max(0, cifre.length() - 4));
    }
}
