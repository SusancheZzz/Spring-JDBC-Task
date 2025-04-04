package com.reksoft.jdbctask.dto;

import com.reksoft.jdbctask.entity.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendshipDto {

  private long id;
  private User user1;
  private User user2;
  private LocalDate date;
}
