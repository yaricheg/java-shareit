package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Collection<UserDto> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        UserDto newUser = userService.createUser(user);
        log.info("Пользователь добавлен {}", user);
        return newUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Integer userId,
                              @RequestBody UserDto user) {
        UserDto updateUser = userService.updateUser(userId, user);
        log.info("Пользователь обновлен {}", updateUser);
        return updateUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUserById(userId);
    }
}
