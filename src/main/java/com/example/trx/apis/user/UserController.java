package com.example.trx.apis.user;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.user.dto.ParticipantCreateRequest;
import com.example.trx.domain.user.Participant;
import com.example.trx.service.user.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations for creating, retrieving and updating users")
public class UserController {
    private final ParticipantService participantService;

    @GetMapping("/upload")
    public ApiResult<?> uploadExcel() {
        try {
            return ApiResult.succeed(participantService.readExcel());
        } catch (Exception e) {
            return ApiResult.failed("엑셀 파일 읽기 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "List users", description = "Retrieve all registered users")
    @GetMapping()
    public ApiResult<List<ParticipantCreateRequest>> getUsers() {
        return ApiResult.succeed(participantService.getAllUsers());
    }
//
//  @Operation(summary = "Get user", description = "Retrieve detailed information for a single user")
//  @GetMapping("/{userId}")
//  public ApiResult<UserDto> getUser(@PathVariable Long userId) {
//    return ApiResult.succeed(userService.getUser(userId));
//  }
//
//  @Operation(summary = "Create user", description = "Register a new user")
//  @PostMapping
//  public ApiResult<UserDto> createUser(@RequestBody UserDto dto) {
//    return ApiResult.succeed(userService.createUser(dto));
//  }
//
//  @Operation(summary = "Update user", description = "Update information of an existing user")
//  @PutMapping("/{userId}")
//  public ApiResult<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto dto) {
//    return ApiResult.succeed(userService.updateUser(userId, dto));
//  }
}

