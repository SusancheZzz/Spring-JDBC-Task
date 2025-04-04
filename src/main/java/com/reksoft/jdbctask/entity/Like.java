package com.reksoft.jdbctask.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Like {

  private long id;
  private long postId;
  private long userId;
  private LocalDateTime dateTime;
}
