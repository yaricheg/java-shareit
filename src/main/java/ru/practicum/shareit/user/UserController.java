package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserModel;
import ru.practicum.shareit.user.service.UserRequestService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserRequestService userService;

    @GetMapping()
    public Collection<UserModel> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{userId}")
    public UserModel getUserById(@PathVariable("userId") Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserModel createUser(@Valid @RequestBody UserDto user) {
        UserModel newUser = userService.createUser(user);
        log.info("Пользователь добавлен {}", user);
        return newUser;
    }

    @PatchMapping("/{userId}")
    public UserModel updateUser(@PathVariable("userId") Integer userId,
                                @RequestBody UserDto user) {
        UserModel updateUser = userService.updateUser(userId, user);
        log.info("Пользователь обновлен {}", updateUser);
        return updateUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUserById(userId);
    }
}
