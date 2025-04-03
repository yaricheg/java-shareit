package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private UserDto user;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new UserDto(userId, "Pavel", "pavel@mail.ru");
    }

    @Test
    void createUser() throws Exception {

        when(userService.createUser(user)).thenReturn(user);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).createUser(user);
    }

    @Test
    void updateUser() throws Exception {

        UserDto updateUser = new UserDto(userId, "Misha", "Misha@mail.ru");

        when(userService.updateUser(userId, updateUser)).thenReturn(updateUser);

        mockMvc.perform(
                        patch("/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).updateUser(userId, updateUser);
    }

    @Test
    void getUserById() throws Exception {

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(
                        get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).getUserById(userId);
    }


    @Test
    void deleteUser() throws Exception {

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }
}