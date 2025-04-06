package com.walkspring.controller;

import com.walkspring.dtos.auth.AuthDTO;
import com.walkspring.dtos.auth.LoginDTO;
import com.walkspring.dtos.user.UserRegisterDTO;
import com.walkspring.dtos.user.UserEditDTO;
import com.walkspring.entities.User;
import com.walkspring.exceptions.UserAlreadyExistsException;
import com.walkspring.services.AuthenticatedUserService;
import com.walkspring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticatedUserService authenticatedUserService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {

        AuthDTO authDTO;

        try {
            authDTO = userService.login(loginDTO);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>(authDTO, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return handleUserRegistration(userRegisterDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return handleUserRegistration(userRegisterDTO);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        try {
            UserEditDTO userEditDTO = userService.deleteUser(userId);
            return ResponseEntity.ok(userEditDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/editUser/{userId}")
    public ResponseEntity<UserEditDTO> editUser(@PathVariable int userId, @RequestBody UserEditDTO userEditDTO) {
        UserEditDTO updatedUser = userService.editUser(userId, userEditDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDTO> getCurrentUser(Authentication authentication) {
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        AuthDTO authDTO = new AuthDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().toString()
        );
        return new ResponseEntity<>(authDTO, HttpStatus.OK);
    }


    private ResponseEntity<?> handleUserRegistration(UserRegisterDTO userRegisterDTO) {
        try {
            AuthDTO authDTO = userService.register(userRegisterDTO);
            return new ResponseEntity<>(authDTO, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
