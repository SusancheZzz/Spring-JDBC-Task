package com.reksoft.jdbctask.mapper;

import com.reksoft.jdbctask.dto.FriendshipDto;
import com.reksoft.jdbctask.entity.Friendship;
import com.reksoft.jdbctask.entity.User;
import jakarta.validation.constraints.NotEmpty;

public class FriendshipMapper {

  private FriendshipMapper() {}

  public static FriendshipDto toFriendshipDto(@NotEmpty Friendship friendship,
    @NotEmpty User friend1, @NotEmpty User friend2) {
    return new FriendshipDto(
      friendship.getId(),
      friend1,
      friend2,
      friendship.getDate()
    );
  }

  public static Friendship toFriendship(@NotEmpty FriendshipDto friendshipDto,
    @NotEmpty User friend1, @NotEmpty User friend2) {
    return new Friendship(
      friendshipDto.getId(),
      friend1.getId(),
      friend2.getId(),
      friendshipDto.getDate()
    );
  }
}
