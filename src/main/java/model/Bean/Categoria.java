package model.Bean;

import java.io.Serial;
import java.io.Serializable;

/*
 * Rappresenta la categoria di un prodotto.
 */
public class Categoria implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long id;
    private String nome;

    public Categoria() {
    }

    public Categoria(long id, String nome) {
        this.id = id;
        this.nome = nome;
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

    @Override
    public String toString() {
        return "Categoria [ID: " + id +
                ", Nome: " + nome + "]";
    }
}
