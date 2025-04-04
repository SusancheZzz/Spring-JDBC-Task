package com.reksoft.jdbctask.mapper;

import com.reksoft.jdbctask.dto.PostDto;
import com.reksoft.jdbctask.entity.Post;
import com.reksoft.jdbctask.entity.User;
import jakarta.validation.constraints.NotEmpty;

public class PostMapper {

  private PostMapper() {}

  public static PostDto toPostDto(@NotEmpty Post post, @NotEmpty User user) {
    return new PostDto(
      post.getId(),
      user,
      post.getContent(),
      post.getCreateDateTime());
  }

  public static Post toPost(@NotEmpty PostDto postDto, @NotEmpty User user) {
    return new Post(
      postDto.getId(),
      user.getId(),
      postDto.getContent(),
      postDto.getCreateDateTime());
  }
}
