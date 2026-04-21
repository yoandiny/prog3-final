package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Member;
import mg.yoan.finaltd.entity.Gender;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class MemberRepository {

    public Optional<Member> findById(Integer id, Connection conn) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
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

    public Integer save(Member member, Connection conn) {
        String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, profession, phone, email, admission_date) " +
                     "VALUES (?, ?, ?, ?::gender, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setObject(3, member.getBirthDate());
            pstmt.setString(4, member.getGender().name());
            pstmt.setString(5, member.getAddress());
            pstmt.setString(6, member.getProfession());
            pstmt.setString(7, member.getPhone());
            pstmt.setString(8, member.getEmail());
            pstmt.setObject(9, member.getAdmissionDate() != null ? member.getAdmissionDate() : LocalDate.now());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
        return null;
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return Member.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .birthDate(rs.getObject("birth_date", LocalDate.class))
                .gender(Gender.valueOf(rs.getString("gender")))
                .address(rs.getString("address"))
                .profession(rs.getString("profession"))
                .phone(rs.getString("phone"))
                .email(rs.getString("email"))
                .admissionDate(rs.getObject("admission_date", LocalDate.class))
                .build();
    }
}
