package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Member;
import mg.yoan.finaltd.entity.Gender;
import mg.yoan.finaltd.entity.MemberOccupation;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    public Optional<Member> findById(String id, Connection conn) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching member by id", e);
        }
        return Optional.empty();
    }

    public String save(Member member, Connection conn) {
        String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, profession, phone, email) " +
                     "VALUES (?, ?, ?, ?::gender, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setObject(3, member.getBirthDate());
            pstmt.setString(4, member.getGender().name());
            pstmt.setString(5, member.getAddress());
            pstmt.setString(6, member.getProfession());
            pstmt.setString(7, member.getPhoneNumber());
            pstmt.setString(8, member.getEmail());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
        return null;
    }

    public List<Member> findByCollectivityId(String collectivityId, Connection conn) {
        String sql = "SELECT m.* FROM member m " +
                     "JOIN collectivity cl ON m.collectivity_id = cl.id " +
                     "WHERE cl.id = ?";
        List<Member> members = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, collectivityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching members by collectivity id", e);
        }
        return members;
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return Member.builder()
                .id(rs.getString("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .birthDate(rs.getObject("birth_date", LocalDate.class))
                .gender(Gender.valueOf(rs.getString("gender")))
                .address(rs.getString("address"))
                .profession(rs.getString("profession"))
                .phoneNumber(rs.getString("phone"))
                .email(rs.getString("email"))
                .occupation(rs.getString("occupation") != null ? MemberOccupation.valueOf(rs.getString("occupation")) : null)
                .build();
    }

    public List<String> findRefereesByMemberId(String memberId, Connection conn) {
        String sql = "SELECT sponsor_id FROM sponsorship WHERE candidate_id = ?";
        List<String> referees = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    referees.add(rs.getString("sponsor_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching referees", e);
        }
        return referees;
    }
}
