package com.example.trx.apis.user.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ParticipantCreateRequest {
    private String nameKr;
    private LocalDate birth;
    private String phone;
    private String emergencyContact;
    private String email;
    private String gender;
    private String residence;
    private String division;
    private List<String> eventToParticipate;
    private String oneLiner;
    private String memo;
}
