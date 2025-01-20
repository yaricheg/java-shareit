package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.exception.ConflictException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAllUser() {
        Collection<UserDto> allUsers = userStorage.getAllUser()
                .stream()
                .map(user -> userMapper.toUserDto(user))
                .collect(Collectors.toList());
        return allUsers;
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return userMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto user) {
        usersEmailCheck(user);
        return userMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto user) {
        usersEmailCheck(user);
        return userMapper.toUserDto(userStorage.updateUser(userId, user));
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
