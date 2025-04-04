package com.reksoft.jdbctask.dao;

import com.reksoft.jdbctask.entity.Post;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@DependsOn("userDao")
public class PostDao implements Dao {

  @Value("${spring.profiles.active}")
  private String activeProfile;
  private final JdbcTemplate jdbcTemplate;
  private static final String CREATE_SCRIPT = """
    CREATE TABLE IF NOT EXISTS posts (
        id SERIAL PRIMARY KEY,
        userId INTEGER,
        text VARCHAR(255),
        dateTime TIMESTAMP  NOT NULL,
        FOREIGN KEY (userId) REFERENCES users(id)
    )
    """;
  private static final String DROP_SCRIPT = """
    DROP TABLE IF EXISTS posts
    """;
  private static final String INSERT_SCRIPT = """
    INSERT INTO posts (userId, text, dateTime)
    VALUES (?, ?, ?)
    """;
  private static final String SELECT_ALL = """
    SELECT * FROM posts
    """;
  public static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
    new Post(
      Long.parseLong(rs.getString("id")),
      Long.parseLong(rs.getString("userid")),
      rs.getString("text"),
      rs.getTimestamp("datetime").toLocalDateTime()
    );

  @Override
  public List<Post> getAll() {
    return jdbcTemplate.query(SELECT_ALL, POST_ROW_MAPPER);
  }

  public void saveAll(List<Post> posts) {
    var batchArgs = posts.stream()
      .map(post -> new Object[]{post.getAuthorId(), post.getContent(), post.getCreateDateTime()})
      .toList();

    jdbcTemplate.batchUpdate(INSERT_SCRIPT, batchArgs);
  }

  public List<Post> findAllById(Set<Long> postIds) {
    if (postIds.isEmpty()) {
      return Collections.emptyList();
    }

    String query = String.format(
      SELECT_ALL + """
        WHERE id IN (%s)
        """,
      postIds.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","))
    );

    return jdbcTemplate.query(query, POST_ROW_MAPPER);
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
