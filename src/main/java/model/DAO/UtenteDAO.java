package model.DAO;

import model.Bean.Utente;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella Utente.
 * Gestisce registrazione, login, aggiornamento dati e cancellazione degli utenti.
 */
public class UtenteDAO {
    // Salva un nuovo utente nel db
    // La sua password viene hashata direttamente nella query
    public void doSave(Utente utente) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Utente (email, passwordHash, isAdmin, nome, cognome, telefono, data_di_nascita, data_registrazione) " +
                             "VALUES (?, SHA2(?, 256), ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, utente.getEmail());
            ps.setString(2, utente.getPassword());
            ps.setBoolean(3, utente.isAdmin());
            ps.setString(4, utente.getNome());
            ps.setString(5, utente.getCognome());
            ps.setString(6, utente.getTelefono());
            ps.setDate(7, Date.valueOf(utente.getDataDiNascita()));
            ps.setDate(8, Date.valueOf(utente.getDataRegistrazione()));
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'inserimento dell'utente");
            // salva l'id generato direttamente nel bean
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    utente.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'inserimento dell'utente", e);
        }
    }

    // Recupera un utente tramite il suo id
    public Utente doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Utente WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return buildUtente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'utente con ID: " + id, e);
        }
        return null;
    }

    /*
     * Metodo responsabile del login.
     * Recupera un utente solo se la sua email e password sono corrette.
     */
    public Utente doRetrieveByEmailAndPassword(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank())
            return null;
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Utente WHERE email = ? AND passwordHash = SHA2(?, 256)")) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return buildUtente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel login", e);
        }
        return null;
    }

    // Controlla se l'email è già registrata
    public boolean doExistsByEmail(String email) {
        if (email == null || email.isBlank())
            return false;
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM Utente WHERE email = ? LIMIT 1")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel controllo esistenza email: " + email, e);
        }
    }

    /*
     * Controlla se esiste un utente con la stessa email, escludendo l'utente con l'ID fornito.
     * Serve in fase di modifica profilo per verificare che la nuova email non sia già presa
     * da un altro account (senza escludersi da solo).
     */
    public boolean doExistsByEmailExcludingId(String email, long excludedId) {
        if (email == null || email.isBlank())
            return false;
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT 1 FROM Utente WHERE email = ? AND id <> ? LIMIT 1")) {
            ps.setString(1, email);
            ps.setLong(2, excludedId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel controllo esistenza email: " + email, e);
        }
    }

    // Recupera tutti gli utenti, ordinandoli dal più recente
    public List<Utente> doRetrieveAll() {
        List<Utente> utenti = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Utente ORDER BY id DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    utenti.add(buildUtente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero degli utenti", e);
        }
        return utenti;
    }

    // Aggiorna i dati principali dell'utente, escludendo la password
    public void doUpdate(Utente utente) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Utente SET nome = ?, cognome = ?, email = ?, telefono = ?, data_di_nascita = ? WHERE id = ?"
             )) {
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getTelefono());
            ps.setDate(5, Date.valueOf(utente.getDataDiNascita()));
            ps.setLong(6, utente.getId());
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nella modifica dell'utente");
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella modifica dell'utente con ID: " + utente.getId(), e);
        }
    }

    // Aggiorna solo la password dell'utente
    public void doUpdatePassword(long id, String nuovaPassword) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Utente SET passwordHash = SHA2(?, 256) WHERE id = ?")) {
            ps.setString(1, nuovaPassword);
            ps.setLong(2, id);
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nella modifica della password");
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella modifica della password", e);
        }
    }

    // Cancella un utente dal db
    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM Utente WHERE id = ?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Nessun utente trovato da eliminare");
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione dell'utente con ID: " + id, e);
        }
    }

    // Helper per costruire l'oggetto partendo dal ResultSet
    protected Utente buildUtente(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setAdmin(rs.getBoolean("isAdmin"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setTelefono(rs.getString("telefono"));
        Date dataNascita = rs.getDate("data_di_nascita");
        u.setDataDiNascita(dataNascita != null ? dataNascita.toLocalDate() : null);
        Date dataReg = rs.getDate("data_registrazione");
        u.setDataRegistrazione(dataReg != null ? dataReg.toLocalDate() : null);
        return u;
    }
}
