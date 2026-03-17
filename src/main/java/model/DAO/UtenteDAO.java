package model.DAO;

import model.Bean.Utente;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

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

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    utente.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'inserimento dell'utente", e);
        }
    }

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

    public Utente doRetrieveByEmail(String email) {
        if (email == null || email.isBlank())
            return null;

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Utente WHERE email = ?")) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return buildUtente(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'utente con email: " + email, e);
        }

        return null;
    }

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

    public void doUpdateAdmin(long id, boolean admin) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE Utente SET isAdmin = ? WHERE id = ?")) {

            ps.setBoolean(1, admin);
            ps.setLong(2, id);

            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nella modifica del ruolo dell'utente");

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella modifica del ruolo dell'utente con ID: " + id, e);
        }
    }

    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Recensione WHERE utente_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Wishlist WHERE utente_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE d FROM Dettaglio_Ordine d JOIN Ordine o ON d.ordine_id = o.id WHERE o.utente_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Ordine WHERE utente_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM IndirizzoSpedizione WHERE utente_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Utente WHERE id = ?")) {
                ps.setLong(1, id);
                rows = ps.executeUpdate();
            }

            if (rows != 1) {
                connection.rollback();
                throw new RuntimeException("Nessun utente trovato da eliminare");
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione dell'utente con ID: " + id, e);
        }
    }

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
