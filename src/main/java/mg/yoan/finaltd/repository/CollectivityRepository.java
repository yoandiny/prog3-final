package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CollectivityRepository {

    public List<Collectivity> saveAll(List<Collectivity> collectivities) {
        String sql = "INSERT INTO collectivity (specialty, city, creation_date, annual_fee) VALUES (?, ?, ?, ?) RETURNING id";
        List<Collectivity> savedCollectivities = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Collectivity collectivity : collectivities) {
                statement.setString(1, collectivity.getSpecialty());
                statement.setString(2, collectivity.getCity());
                statement.setObject(3, collectivity.getCreationDate() != null ? collectivity.getCreationDate() : LocalDate.now());
                statement.setBigDecimal(4, collectivity.getAnnualFee());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        collectivity.setId(resultSet.getInt("id"));
                        savedCollectivities.add(collectivity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving collectivities", e);
        }
        return savedCollectivities;
    }

    public Optional<Collectivity> findById(Integer id, Connection conn) {
        String sql = "SELECT * FROM collectivity WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCollectivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching collectivity by id", e);
        }
        return Optional.empty();
    }

    public Optional<Collectivity> findByName(String name, Connection conn) {
        String sql = "SELECT * FROM collectivity WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCollectivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching collectivity by name", e);
        }
        return Optional.empty();
    }

    public Optional<Collectivity> findByNumber(String number, Connection conn) {
        String sql = "SELECT * FROM collectivity WHERE number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCollectivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching collectivity by number", e);
        }
        return Optional.empty();
    }

    public void updateIdentity(Integer id, String number, String name, Connection conn) {
        String sql = "UPDATE collectivity SET number = ?, name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, name);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating collectivity identity", e);
        }
    }

    private Collectivity mapResultSetToCollectivity(ResultSet rs) throws SQLException {
        return Collectivity.builder()
                .id(rs.getInt("id"))
                .number(rs.getString("number"))
                .name(rs.getString("name"))
                .specialty(rs.getString("specialty"))
                .city(rs.getString("city"))
                .creationDate(rs.getObject("creation_date", LocalDate.class))
                .annualFee(rs.getBigDecimal("annual_fee"))
                .build();
    }
}
