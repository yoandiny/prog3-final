package mg.yoan.finaltd.service;

import lombok.RequiredArgsConstructor;
import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.Activity;
import mg.yoan.finaltd.repository.ActivityRepository;
import mg.yoan.finaltd.repository.CollectivityRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CollectivityRepository collectivityRepository;

    public List<Activity> addActivities(String collectivityId, List<Activity> activities) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Check if collectivity exists
                collectivityRepository.findById(collectivityId, conn)
                        .orElseThrow(() -> new RuntimeException("Collectivity not found"));

                for (Activity activity : activities) {
                    activity.setCollectivityId(collectivityId);
                }

                activityRepository.saveAll(activities, conn);
                conn.commit();
                return activities;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error adding activities: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<Activity> getActivitiesByCollectivity(String collectivityId) {
        try (Connection conn = DBConnection.getConnection()) {
            return activityRepository.findByCollectivityId(collectivityId, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
