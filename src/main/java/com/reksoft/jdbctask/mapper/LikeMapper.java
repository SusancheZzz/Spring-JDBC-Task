package com.reksoft.jdbctask.mapper;

import com.reksoft.jdbctask.dto.LikeDto;
import com.reksoft.jdbctask.dto.PostDto;
import com.reksoft.jdbctask.entity.Like;
import com.reksoft.jdbctask.entity.User;
import jakarta.validation.constraints.NotEmpty;

public class LikeMapper {

  private LikeMapper() {}

  public static LikeDto toLikeDto(@NotEmpty Like like,
    @NotEmpty PostDto postDto, @NotEmpty User user) {
    return new LikeDto(
      like.getId(),
      postDto,
      user,
      like.getDateTime()
    );
  }

  public static Like toLike(@NotEmpty LikeDto likeDto,
    @NotEmpty PostDto postDto, @NotEmpty User user) {
    return new Like(
      likeDto.getId(),
      postDto.getId(),
      user.getId(),
      likeDto.getDateTime()
    );
  }
}
