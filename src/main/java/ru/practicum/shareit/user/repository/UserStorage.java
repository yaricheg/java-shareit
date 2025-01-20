package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAllUser();

    User getUserById(Integer userId);

    User createUser(UserDto user);

    User updateUser(Integer userId, UserDto user);

    void deleteUserById(Integer userId);

}
