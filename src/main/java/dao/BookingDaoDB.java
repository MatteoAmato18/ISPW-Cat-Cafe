package dao;

import entity.Booking;
import entity.BookingStatus;
import exception.DataAccessException;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/** DAO JDBC per la tabella <code>bookings</code> (senza SELECT *). */
public class BookingDaoDB implements GenericDao<Booking> {

    private static final String BASE_SELECT = """
        SELECT  id,
                nomePrenotazione,
                email,
                data,
                ora,
                numeroPartecipanti,
                status,
                activities
          FROM  bookings
        """;

    private final Connection conn;
    public BookingDaoDB(Connection c) { this.conn = c; }

    /* ------------------------------------------------ CREATE ---- */
    @Override
    public void create(Booking b) throws SQLException {
        final String sql = """
            INSERT INTO bookings
              (nomePrenotazione, email, data, ora,
               numeroPartecipanti, status, activities)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps =
                 conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, b.getTitle());
            ps.setString(2, b.getConfirmationEmail());
            ps.setDate  (3, Date.valueOf(b.getDate()));
            ps.setTime  (4, Time.valueOf(b.getTime()));
            ps.setInt   (5, b.getSeats());
            ps.setString(6, b.getStatus().name());
            ps.setString(7, String.join(",", b.getFreeActivities()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setId(rs.getInt(1));
            }
        }
    }

    /* ------------------------------------------------ READ by id */
    @Override
    public Booking read(Object... keys) throws SQLException {
        if (keys.length != 1 || !(keys[0] instanceof Integer id))
            throw new IllegalArgumentException("Key must be Integer id");

        final String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /* ------------------------------------------------ UPDATE ---- */
    @Override
    public void update(Booking b) throws SQLException {
        final String sql = """
            UPDATE bookings
               SET nomePrenotazione   = ?,
                   email             = ?,
                   data              = ?,
                   ora               = ?,
                   numeroPartecipanti = ?,
                   status            = ?,
                   activities        = ?
             WHERE id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getConfirmationEmail());
            ps.setDate  (3, Date.valueOf(b.getDate()));
            ps.setTime  (4, Time.valueOf(b.getTime()));
            ps.setInt   (5, b.getSeats());
            ps.setString(6, b.getStatus().name());
            ps.setString(7, String.join(",", b.getFreeActivities()));
            ps.setInt   (8, b.getId());
            ps.executeUpdate();
        }
    }

    /* ------------------------------------------------ DELETE ---- */
    @Override
    public void delete(Object... keys) throws SQLException {
        if (keys.length != 1 || !(keys[0] instanceof Integer id))
            throw new IllegalArgumentException("Key must be Integer id");

        try (PreparedStatement ps =
                 conn.prepareStatement("DELETE FROM bookings WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /* ------------------------------------------------ READ ALL -- */
    @Override
    public List<Booking> readAll() {
        List<Booking> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero di tutte le prenotazioni", e);
        }
        return list;
    }

    /* -------------------------------- exists / find ------------- */
    public boolean existsByUserAndCheckIn(String email, LocalDate in) {
        final String sql = "SELECT 1 FROM bookings WHERE email = ? AND data = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setDate  (2, Date.valueOf(in));
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new DataAccessException("Errore durante la verifica di esistenza prenotazione", e);
        }
    }

    public Optional<Booking> findByUserAndCheckIn(String email, LocalDate in) {
        final String sql = BASE_SELECT + " WHERE email = ? AND data = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setDate  (2, Date.valueOf(in));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore durante il recupero prenotazione", e);
        }
    }

    /* --------------------- ResultSet → Booking ------------------ */
    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId               (rs.getInt   ("id"));
        b.setTitle            (rs.getString("nomePrenotazione"));
        b.setConfirmationEmail(rs.getString("email"));
        b.setDate             (rs.getDate  ("data").toLocalDate());
        b.setTime             (rs.getTime  ("ora").toLocalTime());
        b.setSeats            (rs.getInt   ("numeroPartecipanti"));
        b.setStatus           (BookingStatus.valueOf(rs.getString("status")));

        String csv = rs.getString("activities");
        b.setFreeActivities(csv == null || csv.isBlank()
                            ? List.of()
                            : List.of(csv.split("\\s*,\\s*")));
        return b;
    }
}
