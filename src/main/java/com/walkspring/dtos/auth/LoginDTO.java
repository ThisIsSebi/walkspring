package com.walkspring.dtos.auth;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDTO {

    //Könnte Username sein oder Email heißt jetzt hier einfach nur username
    private String username;
    private String password;
}

