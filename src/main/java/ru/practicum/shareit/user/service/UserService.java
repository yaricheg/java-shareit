package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUser();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUserById(Long userId);

}
