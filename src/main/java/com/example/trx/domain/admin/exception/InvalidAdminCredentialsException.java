package com.example.trx.domain.admin.exception;

public class InvalidAdminCredentialsException extends RuntimeException {
    public InvalidAdminCredentialsException() {
        super("잘못된 관리자 인증 정보입니다.");
    }
}
