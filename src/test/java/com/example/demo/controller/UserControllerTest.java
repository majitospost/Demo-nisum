package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.presentation.controller.UserController;
import com.example.demo.presentation.presenter.UserPresenter;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Create User")
    public void createUserTest() {
        when(userService.createUser(any())).thenReturn(User.builder().password("123").build());
        User user = userController.createUser(UserPresenter.builder().build());
        assertNotNull(user);
        assertEquals(user.getPassword(), "");
    }

    @Test
    @DisplayName("Update User")
    public void updateUserTest() {
        when(userService.updateUser(any())).thenReturn(User.builder().password("123").build());
        User user = userController.updateUser(UserPresenter.builder().build());
        assertNotNull(user);
        assertEquals(user.getPassword(), "");
    }

    @Test
    @DisplayName("List Users")
    public void listUsersTest() {
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());
        List<User> users = userController.getAllUsers();
        assertNotNull(users);
        users.forEach(user -> {
            assertEquals(user.getPassword(), "");
        });
    }

    @Test
    @DisplayName("Delete User")
    public void deleteUserTest() throws CustomException {
        userController.deleteUser(UUID.randomUUID());
        verify(userService, atLeastOnce()).deleteUser(any());
    }
}
