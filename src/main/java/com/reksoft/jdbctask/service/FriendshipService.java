package com.reksoft.jdbctask.service;

import com.reksoft.jdbctask.config.UserConfig;
import com.reksoft.jdbctask.dao.FriendshipDao;
import com.reksoft.jdbctask.dao.UserDao;
import com.reksoft.jdbctask.dto.FriendshipDto;
import com.reksoft.jdbctask.entity.Friendship;
import com.reksoft.jdbctask.entity.User;
import com.reksoft.jdbctask.mapper.FriendshipMapper;
import com.reksoft.jdbctask.util.DataGenerator;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@DependsOn({"userService", "friendshipDao"})
@Transactional(timeout = 30)
public class FriendshipService implements MyService {

  private final UserDao userDao;
  private final FriendshipDao friendshipDao;
  private final UserConfig userConfig;
  private static final Random RANDOM = new Random();

  @Override
  @Transactional(readOnly = true)
  public List<FriendshipDto> findAll() {
    List<Friendship> friendships = friendshipDao.getAll();

    Map<Long, User> allUsers = userDao.getAll().stream()
      .collect(Collectors.toMap(
          User::getId,
          user -> user
        )
      );

    Map<Long, Map.Entry<User, User>> friends = friendships.stream()
      .collect(Collectors.toMap(
          Friendship::getId,
          friendship -> Map.entry(
            allUsers.get(friendship.getUserId1()),
            allUsers.get(friendship.getUserId2())
          )
        )
      );

    return friendships.stream()
      .map(fr -> {
        Map.Entry<User, User> users = friends.get(fr.getId());
        return FriendshipMapper.toFriendshipDto(
          fr,
          users.getKey(),  // User1
          users.getValue() // User2
        );
      })
      .toList();
  }

  @PostConstruct
  private void init() {

    List<Friendship> friendships = new ArrayList<>();
    List<Long> userId = userDao.getAll().stream()
      .map(User::getId)
      .toList();

    for (int i = 0; i < userConfig.getFriendCount(); i++) {
      long userId1;
      long userId2;
      do {
        userId1 = userId.get(RANDOM.nextInt(userId.size()));
        userId2 = userId.get(RANDOM.nextInt(userId.size()));
      } while (userId1 == userId2);

      friendships.add(new Friendship(
          0,
          userId1,
          userId2,
          DataGenerator.localDateGenerate(
            LocalDate.of(2015, 1, 1),
            LocalDate.now()
          )
        )
      );
    }
    friendshipDao.saveAll(friendships);
  }
}
