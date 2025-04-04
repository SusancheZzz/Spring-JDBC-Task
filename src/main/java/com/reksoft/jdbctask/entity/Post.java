package com.reksoft.jdbctask.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Post {

  private long id;
  private long authorId;
  private String content;
  private LocalDateTime createDateTime;
}
