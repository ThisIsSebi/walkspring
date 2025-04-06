package com.walkspring.dtos.auth;

import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthDTO {
    private final int userId;
    private final String username;
    private final String email;
    private final String role;
    private String jwt;
}
