package com.reksoft.jdbctask.service;

import com.reksoft.jdbctask.config.UserConfig;
import com.reksoft.jdbctask.dao.LikeDao;
import com.reksoft.jdbctask.dao.PostDao;
import com.reksoft.jdbctask.dao.UserDao;
import com.reksoft.jdbctask.dto.LikeDto;
import com.reksoft.jdbctask.dto.PostDto;
import com.reksoft.jdbctask.entity.Like;
import com.reksoft.jdbctask.entity.Post;
import com.reksoft.jdbctask.entity.User;
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
@DependsOn({"userService", "postService"})
@Transactional(timeout = 30)
public class LikeService implements MyService {

  private final UserDao userDao;
  private final PostDao postDao;
  private final LikeDao likeDao;
  private final UserConfig userConfig;
  private static final Random RANDOM = new Random();

  @Override
  @Transactional(readOnly = true)
  public List<LikeDto> findAll() {
    List<Like> likes = likeDao.getAll();
    Set<Long> postIds = likes.stream()
      .map(Like::getPostId)
      .collect(Collectors.toSet());

    Map<Long, User> allUsers = userDao.getAll().stream()
      .collect(Collectors.toMap(
          User::getId,
          user -> user
        )
      );
    Map<Long, Post> likedPostMap = postDao.findAllById(postIds).stream()
      .collect(Collectors.toMap(
          Post::getId,
          post -> post
        )
      );

    return likes.stream()
      .map(like -> new LikeDto(
          like.getId(),
          new PostDto(
            likedPostMap.get(like.getPostId()).getId(),
            allUsers.get(likedPostMap.get(like.getPostId()).getAuthorId()),
            likedPostMap.get(like.getPostId()).getContent(),
            likedPostMap.get(like.getPostId()).getCreateDateTime()
          ),
          new User(
            allUsers.get(like.getUserId()).getId(),
            allUsers.get(like.getUserId()).getName(),
            allUsers.get(like.getUserId()).getSurname(),
            allUsers.get(like.getUserId()).getBirthdate()
          ),
          like.getDateTime()
        )
      )
      .toList();
  }

  @PostConstruct
  private void init() {
    List<Like> likes = new ArrayList<>();
    List<Long> postIds = postDao.getAll().stream()
      .map(Post::getId)
      .toList();
    List<Long> userIds = userDao.getAll().stream()
      .map(User::getId)
      .toList();

    for (int i = 0; i < userConfig.getLikeCount(); i++) {
      likes.add(new Like(
          0,
          postIds.get(RANDOM.nextInt(postIds.size())),
          userIds.get(RANDOM.nextInt(userIds.size())),
          DataGenerator.localDateTimeGenerate(
            LocalDateTime.of(2025, 1, 1, 0, 1),
            LocalDateTime.now()
          )
        )
      );
    }
    likeDao.saveAll(likes);
  }
}
