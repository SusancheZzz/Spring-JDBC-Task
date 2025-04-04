package com.reksoft.jdbctask.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "users")
public class UserConfig {

  @Positive
  private Integer userCount;

  @NotEmpty
  private List<String> names;

  @NotEmpty
  private List<String> surnames;

  @Positive
  private Integer postCount;

  @Positive
  private Integer friendCount;

  @Positive
  private Integer likeCount;

  @Positive
  private Integer minUserAge;
}
