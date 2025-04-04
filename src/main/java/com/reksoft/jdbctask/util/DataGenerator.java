package com.reksoft.jdbctask.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class DataGenerator {

  private static final Random RANDOM = new Random();

  private DataGenerator() {}

  public static LocalDate localDateGenerate(LocalDate start, LocalDate end) {
    long daysBetween = ChronoUnit.DAYS.between(start, end);
    long randomDay = RANDOM.nextLong(daysBetween);
    return start.plusDays(randomDay);
  }

  public static LocalDateTime localDateTimeGenerate(LocalDateTime start, LocalDateTime end) {
    long minutesBetween = ChronoUnit.MINUTES.between(start, end);
    long randomMinute = RANDOM.nextLong(minutesBetween);
    return start.plusMinutes(randomMinute);
  }

  public static String contentGenerate() {
    List<String> content = List.of("Content_1", "Banana", "Content_2", "Dragonfruit", "Content_3",
      "Cherry", "Content_4");
    int randomIndex1 = RANDOM.nextInt(content.size());
    int randomIndex2 = RANDOM.nextInt(content.size());
    return content.get(randomIndex1) + " / " + content.get(randomIndex2);
  }
}
