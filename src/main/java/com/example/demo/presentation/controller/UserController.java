package com.example.demo.presentation.controller;

import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.presentation.presenter.UserPresenter;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario",
            description = "API usado para crear un usuario")
    public User createUser(@Valid @RequestBody UserPresenter userPresenter) {
        User newUser = userService.createUser(userPresenter);
        newUser.setPassword("");
        return newUser;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modificar usuario",
            description = "API usado para modificar un usuario")
    public User updateUser(@Valid @RequestBody UserPresenter userPresenter) {
        User updatedUser = userService.updateUser(userPresenter);
        updatedUser.setPassword("");
        return updatedUser;
    }

    @GetMapping()
    @Operation(summary = "Listar usuarios", description = "API usado para listar usuarios")
    public List<User> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> {
                    user.setPassword("");
                    return user;
                }).collect(Collectors.toList());
    }

    @DeleteMapping(value = "{id}")
    @Operation(summary = "Eliminar usuario",
            description = "API usado para eliminar un usuario por su id")
    public void deleteUser(@PathVariable UUID id) throws CustomException {
        userService.deleteUser(id);
    }
}
