package com.example.trx.apis.user;

import com.example.trx.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations for creating, retrieving and updating users")
public class UserController {

  private final UserService userService;

//  @Operation(summary = "List users", description = "Retrieve all registered users")
//  @GetMapping
//  public ApiResult<List<UserDto>> getUsers() {
//    return ApiResult.succeed(userService.getAllUsers());
//  }
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

