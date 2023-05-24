package com.vagabond.vagabonduserserver.controller;

import com.vagabond.vagabonduserserver.dto.UserDTO;
import com.vagabond.vagabonduserserver.model.User;
import com.vagabond.vagabonduserserver.service.UserService;
import com.vagabond.vagabonduserserver.util.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vagabond/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info/{email}")
    public ResponseEntity<UserDTO> getUserDTO(@PathVariable String email) {
        UserDTO userDTO = userService.findUserByEmail(email);
        if (userDTO == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> saveUser(@RequestBody UserDTO userDTO) {
        User savedUser = userService.createUser(userDTO);
        UserDTO savedUserDTO = DtoConverter.createUserDTO(savedUser);
        return ResponseEntity.ok(savedUserDTO);
    }

    @PutMapping("/modify")
    public ResponseEntity<UserDTO> modifyUser(@RequestBody UserDTO userDTO) {
        UserDTO modifiedUserDTO = userService.modifyUser(userDTO);
        if (modifiedUserDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(modifiedUserDTO);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }
}