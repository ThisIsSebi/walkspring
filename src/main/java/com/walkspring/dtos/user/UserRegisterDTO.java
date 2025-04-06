package com.walkspring.dtos.user;

import com.walkspring.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {

    private String username;
    private String email;
    private String password;
    private UserRole role;

}
