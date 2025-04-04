package com.reksoft.jdbctask.dto;

import com.reksoft.jdbctask.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostDto {

  private long id;
  private User author;
  private String content;
  private LocalDateTime createDateTime;
}
