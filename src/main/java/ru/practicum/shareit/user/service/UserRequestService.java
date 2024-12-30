package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserModel;

import java.util.Collection;

public interface UserRequestService {
    Collection<UserModel> getAllUser();

    UserModel getUserById(Integer userId);

    UserModel createUser(UserDto user);

    UserModel updateUser(Integer userId, UserDto user);

    void deleteUserById(Integer userId);

}
