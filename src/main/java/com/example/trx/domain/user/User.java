package com.example.trx.domain.user;


import jakarta.persistence.*;
import lombok.*;

// 사용자
@Entity
@Table(name = "users")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;
  private String email;
  private String phone;

}
