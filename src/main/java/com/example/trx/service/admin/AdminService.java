package com.example.trx.service.admin;

import com.example.trx.apis.admin.dto.AdminCreateRequest;
import com.example.trx.apis.admin.dto.AdminResponse;
import com.example.trx.apis.admin.dto.AdminTokenResponse;
import com.example.trx.apis.admin.dto.AdminTokenResponse.TokenType;
import com.example.trx.domain.admin.Admin;
import com.example.trx.domain.admin.exception.AdminAlreadyExistsException;
import com.example.trx.domain.admin.exception.InvalidAdminCredentialsException;
import com.example.trx.domain.auth.Role;
import com.example.trx.repository.admin.AdminRepository;
import com.example.trx.support.security.JwtTokenProvider;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new AdminAlreadyExistsException(request.getUsername());
        }
        Admin admin = Admin.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(Role.ADMIN))
            .build();
        Admin saved = adminRepository.save(admin);
        return AdminResponse.builder()
            .id(saved.getId())
            .username(saved.getUsername())
            .build();
    }

    @Transactional(readOnly = true)
    public AdminTokenResponse login(String username, String rawPassword) {
        Admin admin = adminRepository.findByUsername(username)
            .orElseThrow(InvalidAdminCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new InvalidAdminCredentialsException();
        }
        String token = jwtTokenProvider.generateToken(admin.getUsername(),
            admin.getRoles().stream().map(role -> "ROLE_" + role.name()).toList());
        return AdminTokenResponse.builder()
            .token(token)
            .tokenType(TokenType.BEARER)
            .build();
    }
}
