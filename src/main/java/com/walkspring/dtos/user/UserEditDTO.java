package com.walkspring.dtos.user;

import com.walkspring.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEditDTO {

    private int userId;
    private String username;
    private String email;
    private String password;
    private UserRole role;

    public UserEditDTO(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

}