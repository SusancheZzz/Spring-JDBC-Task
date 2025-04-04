package com.reksoft.jdbctask.dao;

import com.reksoft.jdbctask.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDao implements Dao {

  @Value("${spring.profiles.active}")
  private String activeProfile;
  private final JdbcTemplate jdbcTemplate;
  private static final String CREATE_SCRIPT = """
    CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    birthDate DATE NOT NULL
    )
    """;
  private static final String INSERT_SCRIPT = """
    INSERT INTO users (name, surname, birthDate)
    VALUES (?, ?, ?)
    """;
  private static final String DROP_SCRIPT = """
    DROP TABLE IF EXISTS users
    """;
  private static final String SELECT_ALL = """
    SELECT * FROM users
    """;
  private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
    new User(
      rs.getLong("id"),
      rs.getString("name"),
      rs.getString("surname"),
      rs.getDate("birthdate").toLocalDate()
    );

  public List<User> findActiveUsersWithLikesAndFriends(LocalDate startDate, LocalDate endDate,
    int minFriend, int minLike) {

    String sql = """
        SELECT u.id,
               u.name,
               u.surname,
               u.birthdate
        FROM users u
                 JOIN friendships f ON u.id = f.userid1
                 JOIN posts p ON u.id = p.userid
                 JOIN likes l ON l.postid = p.id
        WHERE l.datetime BETWEEN ? AND ?
        GROUP BY u.id, u.name, u.surname, u.birthdate
        HAVING COUNT(DISTINCT f.userid2) > ? AND COUNT(l.id) > ?
        """;

    return jdbcTemplate.query(
      sql,
      ps -> {
        ps.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
        ps.setTimestamp(2, Timestamp.valueOf(endDate.atStartOfDay()));
        ps.setInt(3, minFriend);
        ps.setInt(4, minLike);
      },
      USER_ROW_MAPPER
    );
  }

  public List<User> findAllById(Set<Long> ids) {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    String query = String.format(
      SELECT_ALL + """
        WHERE id IN (%s)
        """,
      ids.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","))
    );

    return jdbcTemplate.query(query, USER_ROW_MAPPER);
  }

  @Override
  public List<User> getAll() {

    return jdbcTemplate.query(SELECT_ALL, USER_ROW_MAPPER);
  }

  public void saveAll(Collection<User> users) {
    var batchArgs = users.stream()
      .map(user -> new Object[]{user.getName(), user.getSurname(), user.getBirthdate()})
      .toList();

    jdbcTemplate.batchUpdate(INSERT_SCRIPT, batchArgs);
  }

  @PostConstruct
  private void init() {
    jdbcTemplate.execute(CREATE_SCRIPT);
  }

  @PreDestroy
  private void destroy() {
    if (!"dev".equals(activeProfile)) {
      jdbcTemplate.execute(DROP_SCRIPT);
    }
  }
}
