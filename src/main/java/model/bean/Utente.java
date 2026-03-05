package model.bean;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Utente implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String email;
    private String password;
    private boolean isAdmin;
    private String nome;
    private String cognome;
    private String telefono;
    private LocalDate dataDiNascita;
    private LocalDate dataRegistrazione;

    public Utente() {}

    public Utente(long id, String email, String password, boolean isAdmin, String nome, String cognome, String telefono, LocalDate dataDiNascita, LocalDate dataRegistrazione) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.dataDiNascita = dataDiNascita;
        this.dataRegistrazione = dataRegistrazione;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }
    public void setDataDiNascita(LocalDate dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }
    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Utente utente = (Utente) o;
        return id == utente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Utente [ID: " + id +
                ", Email: " + email +
                ", Nome: " + nome +
                ", Cognome: " + cognome +
                ", Telefono: " + telefono +
                ", Admin: " + isAdmin +
                ", Data di nascita: " + dataDiNascita +
                ", Data di registrazione: " + dataRegistrazione + "]";
    }
}
