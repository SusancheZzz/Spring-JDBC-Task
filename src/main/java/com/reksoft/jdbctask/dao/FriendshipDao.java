package com.reksoft.jdbctask.dao;

import com.reksoft.jdbctask.entity.Friendship;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@DependsOn("userDao")
public class FriendshipDao implements Dao {

  @Value("${spring.profiles.active}")
  private String activeProfile;
  private final JdbcTemplate jdbcTemplate;
  private static final String CREATE_SCRIPT = """
    CREATE TABLE IF NOT EXISTS friendships (
    id SERIAL PRIMARY KEY,
    userId1 INTEGER,
    userId2 INTEGER,
    datetime DATE NOT NULL,
    FOREIGN KEY(userId1) REFERENCES users(id),
    FOREIGN KEY(userId2) REFERENCES users(id),
    CHECK (userId1 != userId2)
    )
    """;
  private static final String DROP_SCRIPT = """
    DROP TABLE IF EXISTS friendships
    """;
  private static final String INSERT_SCRIPT = """
    INSERT INTO friendships (userId1, userId2, datetime)
    VALUES (?, ?, ?)
    """;
  private static final String SELECT_ALL = """
    SELECT * FROM friendships
    """;
  public static final RowMapper<Friendship> FRIENDSHIPS_ROW_MAPPER = (rs, rowNum) ->
    new Friendship(
      rs.getLong("id"),
      rs.getLong("userid1"),
      rs.getLong("userid2"),
      rs.getDate("datetime").toLocalDate()
    );

  @Override
  public List<Friendship> getAll() {
    return jdbcTemplate.query(SELECT_ALL, FRIENDSHIPS_ROW_MAPPER);
  }

  public void saveAll(List<Friendship> friendships) {
    var batchArgs = friendships.stream()
      .map(friend -> new Object[]{
        friend.getUserId1(),
        friend.getUserId2(),
        friend.getDate()})
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
