package com.shakemate.model;

import org.springframework.stereotype.Repository;

import com.shakemate.vo.UserProfileVO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserProfileDAOImpl implements UserProfileDAO {

    private final DataSource dataSource;

    public UserProfileDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserProfileVO findById(int userId) throws SQLException {
        String sql = "SELECT username, birthday, personality, interests, intro, img1, img2, img3, img4, img5 FROM users WHERE user_id = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    Date birthday = rs.getDate("birthday");
                    String personality = rs.getString("personality");
                    String interests = rs.getString("interests");
                    String intro = rs.getString("intro");

                    int age = Period.between(birthday.toLocalDate(), LocalDate.now()).getYears();
                    String zodiac = getZodiac(birthday.toLocalDate());

                    List<String> avatarList = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        String imgUrl = rs.getString("img" + i);
                        if (imgUrl != null && !imgUrl.isBlank()) {
                            avatarList.add(imgUrl);
                        }
                    }

                    return new UserProfileVO(userId, username, age, zodiac, avatarList, personality, interests, intro);
                }
            }
        }
        return null;
    }

    @Override
    public UserProfileVO getRandomUnmatchedUser(int currentUserId) throws SQLException {
        String sql = """
            SELECT user_id, username, birthday, personality, interests, intro,
                   img1, img2, img3, img4, img5
            FROM users
            WHERE user_id != ?
              AND user_id NOT IN (
                  SELECT TARGET_USER_ID FROM user_matches WHERE ACTION_USER_ID = ?
              )
            ORDER BY RAND()
            LIMIT 1
        """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    Date birthday = rs.getDate("birthday");
                    int age = Period.between(birthday.toLocalDate(), LocalDate.now()).getYears();
                    String zodiac = getZodiac(birthday.toLocalDate());
                    String personality = rs.getString("personality");
                    String interests = rs.getString("interests");
                    String intro = rs.getString("intro");

                    List<String> avatarList = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        String url = rs.getString("img" + i);
                        if (url != null && !url.isBlank()) {
                            avatarList.add(url);
                        }
                    }

                    return new UserProfileVO(userId, username, age, zodiac, avatarList, personality, interests, intro);
                }
            }
        }
        return null;
    }

    @Override
    public List<UserProfileVO> prefer_matched(int currentUserId, List<String> interests, List<String> personality, Integer gender) {
        List<UserProfileVO> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT
                  user_id,
                  username,
                  birthday,
                  personality,
                  interests,
                  intro,
                  img1, img2, img3, img4, img5,
                  (
        """);

        List<String> matchExpressions = new ArrayList<>();
        for (String interest : interests) {
            matchExpressions.add("IF(FIND_IN_SET('" + interest + "', interests) > 0, 1, 0)");
        }
        for (String p : personality) {
            matchExpressions.add("IF(FIND_IN_SET('" + p + "', personality) > 0, 1, 0)");
        }

        sql.append(String.join(" + ", matchExpressions));
        sql.append(") AS match_count\nFROM users\n");

        List<String> optionalConds = new ArrayList<>();
        for (String interest : interests) {
            optionalConds.add("FIND_IN_SET('" + interest + "', interests) > 0");
        }
        for (String p : personality) {
            optionalConds.add("FIND_IN_SET('" + p + "', personality) > 0");
        }

        sql.append("WHERE user_id != ").append(currentUserId).append("\n")
           .append("AND user_id NOT IN (\n")
           .append("    SELECT TARGET_USER_ID FROM user_matches WHERE ACTION_USER_ID = ").append(currentUserId).append("\n")
           .append(")\n");

        if (gender != null) {
            sql.append("AND gender = ").append(gender).append("\n");
        }

        if (!optionalConds.isEmpty()) {
            sql.append("AND (\n").append(String.join(" OR\n", optionalConds)).append(")\n");
        }

        sql.append("ORDER BY match_count DESC");

        System.out.println("🔍 組出 SQL：\n" + sql);

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserProfileVO vo = new UserProfileVO();
                vo.setUserId(rs.getInt("user_id"));
                vo.setUsername(rs.getString("username"));
                Date birthday = rs.getDate("birthday");

                vo.setAge(Period.between(birthday.toLocalDate(), LocalDate.now()).getYears());
                vo.setZodiac(getZodiac(birthday.toLocalDate()));

                List<String> avatarList = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String imgUrl = rs.getString("img" + i);
                    if (imgUrl != null && !imgUrl.isBlank()) {
                        avatarList.add(imgUrl);
                    }
                }
                vo.setAvatarList(avatarList);
                vo.setPersonality(rs.getString("personality"));
                vo.setInterests(rs.getString("interests"));
                vo.setIntro(rs.getString("intro"));

                result.add(vo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getZodiac(LocalDate birthday) {
        int month = birthday.getMonthValue();
        int day = birthday.getDayOfMonth();
        return switch (month) {
            case 1 -> (day < 20) ? "摩羯座" : "水瓶座";
            case 2 -> (day < 19) ? "水瓶座" : "雙魚座";
            case 3 -> (day < 21) ? "雙魚座" : "牡羊座";
            case 4 -> (day < 20) ? "牡羊座" : "金牛座";
            case 5 -> (day < 21) ? "金牛座" : "雙子座";
            case 6 -> (day < 21) ? "雙子座" : "巨蟹座";
            case 7 -> (day < 23) ? "巨蟹座" : "獅子座";
            case 8 -> (day < 23) ? "獅子座" : "處女座";
            case 9 -> (day < 23) ? "處女座" : "天秤座";
            case 10 -> (day < 23) ? "天秤座" : "天蠍座";
            case 11 -> (day < 22) ? "天蠍座" : "射手座";
            case 12 -> (day < 22) ? "射手座" : "摩羯座";
            default -> "未知";
        };
    }
}
