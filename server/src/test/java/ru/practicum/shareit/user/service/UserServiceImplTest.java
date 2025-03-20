package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = null;
        user2 = null;
        user1 = new User(0, "Pavel", "pavel@mail.ru");
        user2 = new User(0, "Misha", "misha@mail.ru");

    }

    @Test
    void createUserThenReturnUserDto() {
        UserDto newUser = UserMapper.toUserDto(user1);
        UserDto createdUser = userService.createUser(newUser);
        assertEquals("Pavel", createdUser.getName());
        assertEquals("pavel@mail.ru", createdUser.getEmail());
    }

    @Test
    void updateUserThenReturnUserDto() {
        UserDto newUser = UserMapper.toUserDto(user1);
        UserDto createdUser = userService.createUser(newUser);
        UserDto update = new UserDto(1, "Pasha", "pasha@mail.ru");
        UserDto updated = userService.updateUser(createdUser.getId(), update);
        assertNotNull(updated);
        assertEquals("Pasha", updated.getName());
        assertEquals("pasha@mail.ru", updated.getEmail());
    }

    @Test
    void getUserByIdThenReturnUserDto() {
        UserDto newUser = UserMapper.toUserDto(user1);
        UserDto createdUser = userService.createUser(newUser);
        UserDto user = userService.getUserById(createdUser.getId());
        assertEquals("Pavel", user.getName());
        assertEquals("pavel@mail.ru", user.getEmail());
    }

    @Test
    void getUsersThenReturnUsersDto() {
        UserDto newUser1 = UserMapper.toUserDto(user1);
        UserDto newUser2 = UserMapper.toUserDto(user2);
        UserDto createdUser1 = userService.createUser(newUser1);
        UserDto createdUser2 = userService.createUser(newUser2);
        List<UserDto> users = userService.getUsers();
        assertEquals(users.get(0).getName(), createdUser1.getName());
        assertEquals(users.get(1).getName(), createdUser2.getName());
    }

    @Test
    void deleteUser() {
        UserDto newUser1 = UserMapper.toUserDto(user1);
        UserDto createdUser1 = userService.createUser(newUser1);
        userService.deleteUser(createdUser1.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUser1.getId()));
    }
}
