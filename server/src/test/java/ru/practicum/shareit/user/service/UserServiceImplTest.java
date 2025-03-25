package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
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

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        UserDto newUser1 = UserMapper.toUserDto(new User(0, "Pavel", "pavel@mail.ru"));
        UserDto newUser2 = UserMapper.toUserDto(new User(0, "Misha", "misha@mail.ru"));
        user1 = userService.createUser(newUser1);
        user2 = userService.createUser(newUser2);
    }

    @Test
    void createUserThenReturnUserDto() {
        assertEquals("Pavel", userService.getUserById(1).getName());
        assertEquals("pavel@mail.ru", userService.getUserById(1).getEmail());
    }

    @Test
    void сreateUserWithDuplicateEmailThenConflictException() {
        UserDto newUser = UserDto.builder()
                .name("Another Pavel")
                .email(user1.getEmail())
                .build();
        assertThrows(ConflictException.class, () -> userService.createUser(newUser));
    }


    @Test
    void updateUserThenReturnUserDto() {
        UserDto update = new UserDto(1, "Pasha", "pasha@mail.ru");
        UserDto updated = userService.updateUser(user1.getId(), update);
        assertNotNull(updated);
        assertEquals("Pasha", updated.getName());
        assertEquals("pasha@mail.ru", updated.getEmail());
    }

    @Test
    void updateByIdWithDuplicateEmailThenConflictException() {
        UserDto update = UserDto.builder()
                .email(user2.getEmail())
                .build();
        assertThrows(ConflictException.class, () -> userService.updateUser(user1.getId(), update));
    }

    @Test
    void UpdateByIdWithWrongUserThenNotFoundException() {
        UserDto update = UserDto.builder()
                .email(user2.getEmail())
                .build();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, update));
    }

    @Test
    void getUserByIdThenReturnUserDto() {
        UserDto user = userService.getUserById(user1.getId());
        assertEquals("Pavel", user.getName());
        assertEquals("pavel@mail.ru", user.getEmail());
    }

    @Test
    void getByIdForNonExistingUserThenNotFoundException() {
        long nonExistingId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(nonExistingId));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingId)));
    }

    @Test
    void getUsersThenReturnUsersDto() {
        List<UserDto> users = userService.getUsers();
        assertEquals(users.get(0).getName(), user1.getName());
        assertEquals(users.get(1).getName(), user2.getName());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user1.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(user1.getId()));
    }

}
