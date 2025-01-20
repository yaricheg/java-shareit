package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.Collection;
import java.util.HashMap;


@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap();

    private final UserMapper userMapper;

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User getUserById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Integer userId, UserDto user) {
        User userModel = users.get(userId);
        if (user.getName() != null) {
            userModel.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userModel.setEmail(user.getEmail());
        }
        users.put(userModel.getId(), userModel);
        return userModel;
    }

    @Override
    public void deleteUserById(Integer userId) {
        users.remove(userId);
    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
