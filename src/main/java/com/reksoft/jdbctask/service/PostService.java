package com.reksoft.jdbctask.service;

import com.reksoft.jdbctask.config.UserConfig;
import com.reksoft.jdbctask.dao.PostDao;
import com.reksoft.jdbctask.dao.UserDao;
import com.reksoft.jdbctask.dto.PostDto;
import com.reksoft.jdbctask.entity.Post;
import com.reksoft.jdbctask.entity.User;
import com.reksoft.jdbctask.mapper.PostMapper;
import com.reksoft.jdbctask.util.DataGenerator;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@DependsOn({"userService", "postDao"})
@Transactional(timeout = 30)
public class PostService implements MyService {

  private final UserDao userDao;
  private final PostDao postDao;
  private final UserConfig userConfig;
  private static final Random RANDOM = new Random();

  @Override
  @Transactional(readOnly = true)
  public List<PostDto> findAll() {

    List<Post> posts = postDao.getAll();

    Set<Long> userIds = posts.stream()
      .map(Post::getAuthorId)
      .collect(Collectors.toSet());

    Map<Long, User> usersMap = userDao.findAllById(userIds).stream()
      .collect(Collectors.toMap(
          User::getId,
          user -> user
        )
      );

    return posts.stream()
      .map(post -> PostMapper.toPostDto(
        post,
        usersMap.get(post.getAuthorId()))
      )
      .toList();
  }

  @PostConstruct
  private void init() {

    List<Post> randomPosts = new ArrayList<>();
    List<Long> userIds = userDao.getAll().stream()
      .map(User::getId)
      .toList();

    for (int i = 0; i < userConfig.getPostCount(); i++) {
      randomPosts.add(new Post(
        0,
        userIds.get(RANDOM.nextInt(userIds.size())),
        DataGenerator.contentGenerate(),
        DataGenerator.localDateTimeGenerate(
          LocalDateTime.of(2024, 1, 1, 0, 1),
          LocalDateTime.now()
        )
      ));
    }
    postDao.saveAll(randomPosts);
  }

}
