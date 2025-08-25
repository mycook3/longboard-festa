package com.example.trx.dto.user;


import lombok.*;

import java.time.LocalDateTime;

// 사용자
// 사용자 생성/조회 DTO
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDto {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private LocalDateTime createdAt;
}