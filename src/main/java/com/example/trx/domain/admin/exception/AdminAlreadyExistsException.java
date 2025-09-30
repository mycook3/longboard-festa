package com.example.trx.domain.admin.exception;

public class AdminAlreadyExistsException extends RuntimeException {
    public AdminAlreadyExistsException(String username) {
        super("이미 존재하는 관리자 계정입니다. username=" + username);
    }
}
