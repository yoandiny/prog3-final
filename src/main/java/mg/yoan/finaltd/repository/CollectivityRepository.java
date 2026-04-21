package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CollectivityRepository {

    public List<Collectivity> saveAll(List<Collectivity> collectivities) {
        String sql = "INSERT INTO collectivity (number, name, specialty, city, annual_fee) VALUES (?, ?, ?, ?, ?) RETURNING id";
        List<Collectivity> savedCollectivities = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Collectivity collectivity : collectivities) {
                statement.setString(1, collectivity.getNumber());
                statement.setString(2, collectivity.getName());
                statement.setString(3, collectivity.getSpecialty());
                statement.setString(4, collectivity.getCity());
                statement.setDouble(5, collectivity.getAnnualFee());

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
}
