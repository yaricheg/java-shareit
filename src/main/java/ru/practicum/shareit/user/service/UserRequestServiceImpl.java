package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserModel;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.exception.ConflictException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRequestServiceImpl implements UserRequestService {

    private final UserStorage userStorage;

    @Override
    public Collection<UserModel> getAllUser() {
        return userStorage.getAllUser();
    }

    @Override
    public UserModel getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public UserModel createUser(UserDto user) {
        usersEmailCheck(user);
        return userStorage.createUser(user);
    }

    @Override
    public UserModel updateUser(Integer userId, UserDto user) {
        usersEmailCheck(user);
        return userStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userStorage.deleteUserById(userId);
    }

    private void usersEmailCheck(UserDto user) {
        Set<String> emailUsers = getAllUser()
                .stream().map(userObject -> userObject.getEmail())
                .collect(Collectors.toSet());
        if (emailUsers.contains(user.getEmail())) {
            throw new ConflictException("Пользователь с данным email уже существует");
        }

    }

}
