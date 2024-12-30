package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserModel;

import java.util.Collection;
import java.util.HashMap;


@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, UserModel> users = new HashMap();

    @Override
    public Collection<UserModel> getAllUser() {
        return users.values();
    }

    @Override
    public UserModel getUserById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public UserModel createUser(UserDto userDto) {
        UserModel userModel = new UserModel();
        userModel.setId(getNextId());
        userModel.setName(userDto.getName());
        userModel.setEmail(userDto.getEmail());
        users.put(userModel.getId(), userModel);
        return userModel;
    }

    @Override
    public UserModel updateUser(Integer userId, UserDto user) {
        UserModel userModel = users.get(userId);
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
