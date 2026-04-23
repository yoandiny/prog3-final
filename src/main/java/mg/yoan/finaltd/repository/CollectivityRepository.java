package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CollectivityRepository {

    public List<Collectivity> saveAll(List<Collectivity> collectivities, Connection connection) {
        String sql = "INSERT INTO collectivity (id, name, number, location) VALUES (?, ?, ?, ?)";
        List<Collectivity> savedCollectivities = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Collectivity collectivity : collectivities) {
                if (collectivity.getId() == null) {
                    collectivity.setId(UUID.randomUUID().toString());
                }
                statement.setString(1, collectivity.getId());
                statement.setString(2, collectivity.getName());
                statement.setObject(3, collectivity.getNumber());
                statement.setString(4, collectivity.getLocation());

                statement.executeUpdate();

                savedCollectivities.add(collectivity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving collectivities", e);
        }
        return savedCollectivities;
    }

    public Optional<Collectivity> findById(String id, Connection conn) {
        String sql = "SELECT * FROM collectivity WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
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

    public Optional<Collectivity> findByNumber(Integer number, Connection conn) {
        String sql = "SELECT * FROM collectivity WHERE number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, number);
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

    public void updateInformations(String id, Integer number, String name, Connection conn) {
        String sql = "UPDATE collectivity SET number = ?, name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, number);
            pstmt.setString(2, name);
            pstmt.setString(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating collectivity informations", e);
        }
    }

    private Collectivity mapResultSetToCollectivity(ResultSet rs) throws SQLException {
        return Collectivity.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .number(rs.getObject("number", Integer.class))
                .location(rs.getString("location"))
                .build();
    }
}
