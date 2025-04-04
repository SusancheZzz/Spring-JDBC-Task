package com.reksoft.jdbctask.service;

import com.reksoft.jdbctask.config.UserConfig;
import com.reksoft.jdbctask.dao.UserDao;
import com.reksoft.jdbctask.entity.User;
import com.reksoft.jdbctask.util.DataGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@DependsOn("userDao")
@Validated
@Transactional(timeout = 30)
public class UserService implements MyService {

  private final UserDao userDao;
  private final UserConfig userConfig;
  private static final Random RANDOM = new Random();

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userDao.getAll();
  }

  @Transactional(readOnly = true)
  public List<User> getPopularUsersForDate(@NotNull LocalDate start, @NotNull LocalDate end,
    @Positive int minFriend, @Positive int minLike) {
    return userDao.findActiveUsersWithLikesAndFriends(start, end, minFriend, minLike);
  }

  @PostConstruct
  private void init() {

    List<User> randomUsers = new ArrayList<>();
    for (int i = 0; i < userConfig.getUserCount(); i++) {
      int randNameIdx = RANDOM.nextInt(userConfig.getNames().size());
      int randSurnameIdx = RANDOM.nextInt(userConfig.getSurnames().size());
      randomUsers.add(new User(
        0,
        userConfig.getNames().get(randNameIdx),
        userConfig.getSurnames().get(randSurnameIdx),
        DataGenerator.localDateGenerate(
          LocalDate.of(1970, 1, 1),
          LocalDate.now().minusYears(userConfig.getMinUserAge()))
      ));
    }
    userDao.saveAll(randomUsers);
  }
}
