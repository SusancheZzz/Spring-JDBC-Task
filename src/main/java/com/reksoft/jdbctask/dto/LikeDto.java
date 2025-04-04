package com.reksoft.jdbctask.dto;

import com.reksoft.jdbctask.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LikeDto {

  private long id;
  private PostDto post;
  private User user;
  private LocalDateTime dateTime;
}
