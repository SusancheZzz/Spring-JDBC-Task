package com.reksoft.jdbctask.dao;

import com.reksoft.jdbctask.entity.Like;
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
@DependsOn({"userDao", "postDao"})
public class LikeDao implements Dao {

  @Value("${spring.profiles.active}")
  private String activeProfile;
  private final JdbcTemplate jdbcTemplate;
  private static final String CREATE_SCRIPT = """
    CREATE TABLE IF NOT EXISTS likes (
    id SERIAL PRIMARY KEY,
    postId INTEGER,
    userId INTEGER,
    dateTime TIMESTAMP NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (postId) REFERENCES posts(id)
    )
    """;
  private static final String DROP_SCRIPT = """
    DROP TABLE IF EXISTS likes
    """;
  private static final String INSERT_SCRIPT = """
    INSERT INTO likes (postid, userid, datetime)
    VALUES (?, ?, ?)
    """;
  private static final String SELECT_ALL = """
    SELECT * FROM likes
    """;
  public static final RowMapper<Like> LIKE_ROW_MAPPER = (rs, rowNum) ->
    new Like(
      rs.getLong("id"),
      rs.getLong("postId"),
      rs.getLong("userId"),
      rs.getTimestamp("datetime").toLocalDateTime()
    );

  @Override
  public List<Like> getAll() {
    return jdbcTemplate.query(SELECT_ALL, LIKE_ROW_MAPPER);
  }

  public void saveAll(List<Like> likes) {
    var batchArgs = likes.stream()
      .map(post -> new Object[]{post.getPostId(), post.getUserId(), post.getDateTime()})
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
