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
        String sql = "INSERT INTO collectivity (number, name, specialty, city, creation_date, annual_fee) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        List<Collectivity> savedCollectivities = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Collectivity collectivity : collectivities) {
                statement.setString(1, collectivity.getNumber());
                statement.setString(2, collectivity.getName());
                statement.setString(3, collectivity.getSpecialty());
                statement.setString(4, collectivity.getCity());
                statement.setObject(5, collectivity.getCreationDate() != null ? collectivity.getCreationDate() : LocalDate.now());
                statement.setBigDecimal(6, collectivity.getAnnualFee());

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
