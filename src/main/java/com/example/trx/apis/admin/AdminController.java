package com.example.trx.apis.admin;

import com.example.trx.apis.admin.dto.AdminCreateRequest;
import com.example.trx.apis.admin.dto.AdminLoginRequest;
import com.example.trx.apis.admin.dto.AdminResponse;
import com.example.trx.apis.admin.dto.AdminTokenResponse;
import com.example.trx.apis.dto.ApiResult;
import com.example.trx.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admins", description = "관리자 계정 생성 및 인증 API")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 계정 생성", description = "개발자가 관리자 계정을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<AdminResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ApiResult.succeed(adminService.createAdmin(request));
    }

    @Operation(summary = "관리자 로그인", description = "아이디/비밀번호로 로그인하고 JWT를 발급받습니다.")
    @PostMapping("/login")
    public ApiResult<AdminTokenResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResult.succeed(adminService.login(request.getUsername(), request.getPassword()));
    }
}
