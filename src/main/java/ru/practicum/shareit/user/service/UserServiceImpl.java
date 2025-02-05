package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ConflictException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUser() {
        Collection<UserDto> allUsers = userRepository.findAll()
                .stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
        return allUsers;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public UserDto createUser(UserDto user) {
        usersEmailCheck(user);
        User userModel = UserMapper.toUser(user);
        userRepository.save(userModel);
        return UserMapper.toUserDto(userRepository.getById(userModel.getId()));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        usersEmailCheck(user);
        User userModel = userRepository.getById(userId);
        if (user.getName() != null) {
            userModel.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userModel.setEmail(user.getEmail());
        }
        userRepository.save(userModel);
        return UserMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
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
