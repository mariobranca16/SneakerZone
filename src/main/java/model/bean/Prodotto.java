package model.bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Prodotto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String nome;
    private String descrizione;
    private String brand;
    private double costo;
    private String colore;

   // private List<ImmagineProdotto> immagini;
   // private List<ProdottoTaglia> taglie;
    private List<Categoria> categorie;

    private String imgPath;

    public Prodotto() {}

    public Prodotto(long id, String nome, String descrizione, String brand, double costo, String colore) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.brand = brand;
        this.costo = costo;
        this.colore = colore;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getCosto() {
        return costo;
    }
    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getColore() {
        return colore;
    }
    public void setColore(String colore) {
        this.colore = colore;
    }

   /* public List<ImmagineProdotto> getImmagini() {
        return immagini;
    }
    public void setImmagini(List<ImmagineProdotto> immagini) {
        this.immagini = immagini;
    }

    public List<ProdottoTaglia> getTaglie() {
        return taglie;
    }
    public void setTaglie(List<ProdottoTaglia> taglie) {
        this.taglie = taglie;
    }
*/
    public List<Categoria> getCategorie() {
        return categorie;
    }
    public void setCategorie(List<Categoria> categorie) {
        this.categorie = categorie;
    }

    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Prodotto p = (Prodotto) o;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Prodotto [ID: " + id +
                ", Nome: " + nome +
                ", Descrizione: " + descrizione +
                ", Brand: " + brand +
                ", Costo: " + costo +
                ", Colore: " + colore + "]";
    }
}
