package com.reksoft.jdbctask;

import com.reksoft.jdbctask.entity.User;
import com.reksoft.jdbctask.service.UserService;
import java.time.LocalDate;
import java.time.Month;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class JdbcApplication implements CommandLineRunner {

  private final UserService userService;

  public static void main(String[] args) {
    SpringApplication.run(JdbcApplication.class, args);
  }

  @Override
  public void run(String... args) {

    int minFriend = 50;
    int minLike = 50;
    System.out.printf(
      "Уникальные имена пользователей, с более чем %s друзьями и %s лайками в марте 2025 года:%n",
      minFriend, minLike);

    userService.getPopularUsersForDate(
        LocalDate.of(2025, Month.MARCH, 1),
        LocalDate.of(2025, Month.APRIL, 1),
        minFriend,
        minLike
      )
      .stream()
      .map(User::getName)
      .distinct()
      .forEach(System.out::println);
  }
}
