package com.walkspring.services;

import com.walkspring.dtos.auth.AuthDTO;
import com.walkspring.dtos.auth.LoginDTO;
import com.walkspring.dtos.user.UserRegisterDTO;
import com.walkspring.dtos.user.UserResponseDTO;
import com.walkspring.dtos.user.UserEditDTO;
import com.walkspring.entities.User;
import com.walkspring.enums.UserRole;
import com.walkspring.exceptions.EmptyOptionalException;
import com.walkspring.exceptions.UserAlreadyExistsException;
import com.walkspring.repositories.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserCrudRepository userCrudRepository;

    @Autowired
    ConversionService1 conversionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthDTO login(LoginDTO loginDTO){

        User user = getUserByUsernameOrEmail(loginDTO);
        String username = user.getUsername().toLowerCase();

        String temp = loginDTO.getPassword();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword()));

        String jwt = tokenService.generateTokenWithClaims(user);

        return new AuthDTO(
                user.getUserId(),
                user.getUsername().toLowerCase(),
                user.getEmail(),
                user.getUserRole().getLabel(),
                jwt);
    }

    public AuthDTO register(UserRegisterDTO registerDTO) throws UserAlreadyExistsException{

        UserRole userRole = (registerDTO.getRole() != null) ? registerDTO.getRole() : UserRole.USER;

        User user = new User(
                registerDTO.getUsername().toLowerCase(),
                registerDTO.getEmail(),
                passwordEncoder.encode(registerDTO.getPassword()),
                userRole
        );
        userCrudRepository.save(user);

        String jwt = tokenService.generateTokenWithClaims(user);



        return new AuthDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().getLabel(),
                jwt
        );
    }

    public User getUserByUsernameOrEmail(LoginDTO loginDTO){
        return getUserFromOptional(
                userCrudRepository.findByUsernameOrEmail(loginDTO.getUsername().toLowerCase(), null)
        );
    }


    public User getUserFromOptional(Optional<User> userOptional){
        User user;
        try {
            user = conversionService.getEntityFromOptional(userOptional);
        }catch (EmptyOptionalException e){
            throw new UsernameNotFoundException("Kein entsprechender User in der Datenbank gefunden!");
        }
        return user;
    }

    public List<UserEditDTO> getAllUsers(){
        List <User> userList = userCrudRepository.findAll();
        List<UserEditDTO> dtoList = new ArrayList<>();
        for (User user : userList) {
            dtoList.add(new UserEditDTO(user.getUserId(),user.getUsername(), user.getEmail()));
        }
        return dtoList;
    }

    public UserEditDTO deleteUser(int userId){
        Optional<User> userOptional = userCrudRepository.findById(userId);
        User user = getUserFromOptional(userOptional);
        UserEditDTO userEditDTO = new UserEditDTO(user.getUserId(), user.getUsername(), user.getEmail());
        userCrudRepository.delete(user);
        return userEditDTO;
    }

    public UserEditDTO editUser(int userId, UserEditDTO dto){
        UserRole userRole = (dto.getRole() != null) ? dto.getRole() : UserRole.USER;

        Optional<User> userOptional = userCrudRepository.findById(userId);
        User user = getUserFromOptional(userOptional);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRole(userRole);

        userCrudRepository.save(user);
        return new UserEditDTO(user.getUserId(), user.getUsername(), user.getEmail(), null, user.getUserRole());
    }

    public UserResponseDTO userToDTO(User user) {
        return new UserResponseDTO(user.getUsername(), user.getUserImage() != null ? user.getUserImage().getImageId() : 0);
    }

}

