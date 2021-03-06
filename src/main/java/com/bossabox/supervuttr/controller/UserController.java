package com.bossabox.supervuttr.controller;

import com.bossabox.supervuttr.controller.dtos.UserDTO;
import com.bossabox.supervuttr.data.AppUser;
import com.bossabox.supervuttr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Signs up a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User was created"),
            @ApiResponse(responseCode = "400", description = "Prerequisites are not fulfilled"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passwords do not match");
        }

        try {
            var addedUser = userService.createUser(dtoToUser(userDTO));
            log.info("Created new user: " + addedUser.getUsername());
            return userToDto(addedUser);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username not available");
        }
    }

    @Operation(summary = "Deletes a user from the database", responses = {
            @ApiResponse(responseCode = "204", description = "The user was deleted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized deletion")
    })
    @SecurityRequirements
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username,
                           Authentication auth) {
        if (auth.getName().equals(username)) {
            userService.deleteUser(username);
            log.info("Deleted user: " + username);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    // Private methods

    private AppUser dtoToUser(UserDTO dto) {
        var user = new AppUser();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    private UserDTO userToDto(AppUser user) {
        var dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
