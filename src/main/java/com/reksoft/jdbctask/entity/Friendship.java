package com.reksoft.jdbctask.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Friendship {

  private long id;
  private long userId1;
  private long userId2;
  private LocalDate date;
}
