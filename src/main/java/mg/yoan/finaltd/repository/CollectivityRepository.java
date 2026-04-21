package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Collectivity;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class CollectivityRepository {

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
