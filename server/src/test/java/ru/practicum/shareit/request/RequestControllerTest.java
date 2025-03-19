package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private ObjectMapper mapper;

    private Long userId;

    private Long requestId;

    private ItemRequestDto requestInputDto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        userId = 1L;
        requestId = 2L;

        requestInputDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Нужна болгарка")
                .items(List.of(
                        ItemDto.builder().id(10L).name("болгарка").available(true).build()
                ))
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();
    }

    @Test
    void createRequestThenReturnRequestDto() throws Exception {
        when(requestService.createRequest(requestInputDto, userId)).thenReturn(requestInputDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestInputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна болгарка"))
                .andExpect(jsonPath("$.items[0].name").value("болгарка"));
        verify(requestService, times(1)).createRequest(requestInputDto, userId);
    }


    @Test
    void getRequestsThenReturnRequestsDto() throws Exception {
        when(requestService.getRequests(userId)).thenReturn(List.of(requestInputDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Нужна болгарка"));
        verify(requestService, times(1)).getRequests(userId);
    }

    @Test
    void getRequestsUsersThenReturnItemsRequestDto() throws Exception {
        when(requestService.getRequestsUsers()).thenReturn(List.of(requestInputDto));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId));
        verify(requestService, times(1)).getRequestsUsers();
    }

    @Test
    void getRequestByIdThenReturnItemRequestDto() throws Exception {
        when(requestService.getRequestById(requestId)).thenReturn(requestInputDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна болгарка"));
        verify(requestService, times(1)).getRequestById(requestId);
    }

}