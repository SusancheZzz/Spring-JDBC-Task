package com.reksoft.jdbctask.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {

  private long id;
  private String name;
  private String surname;
  private LocalDate birthdate;
}
