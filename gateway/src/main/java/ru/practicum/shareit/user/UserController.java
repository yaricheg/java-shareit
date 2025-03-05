package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    public ResponseEntity<Object> updateUser(@PathVariable("userId") long userId,
                                             @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    public ResponseEntity<Object> getUserById(@PathVariable("userId") long userId) {
        return userClient.getUserById(userId);
    }

    public ResponseEntity<Object> deleteUser(@PathVariable("userId") long userId) {
        return userClient.deleteUser(userId);
    }
}
