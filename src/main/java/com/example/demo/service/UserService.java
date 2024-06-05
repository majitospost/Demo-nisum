package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.presentation.presenter.UserPresenter;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(UserPresenter userPresenter) throws CustomException;

    List<User> getAllUsers();

    void deleteUser(UUID id) throws CustomException;

    User updateUser(UserPresenter userPresenter) throws CustomException;
}
