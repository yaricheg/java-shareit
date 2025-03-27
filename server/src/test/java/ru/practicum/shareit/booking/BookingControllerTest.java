package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper mapper;

    private Long userId = 1L;
    private Long bookingId = 1L;
    private BookingDto bookingRequestDto;
    private BookingDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        mapper = mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        bookingRequestDto = BookingDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2024, 1, 10, 10, 0))
                .end(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        bookingResponseDto = BookingDto.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(ItemDto.builder()
                        .id(10L)
                        .name("Test Item")
                        .description("Test Description")
                        .available(true)
                        .build())
                .booker(UserDto.builder()
                        .id(userId)
                        .name("UserName")
                        .email("user@email")
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), eq(userId)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).createBooking(any(BookingDto.class), eq(userId));
    }

    @Test
    void updateBookingStatus() throws Exception {
        BookingDto approvedResponse = BookingDto.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(bookingResponseDto.getItem())
                .booker(bookingResponseDto.getBooker())
                .status(BookingStatus.APPROVED)
                .build();

        Boolean approved = true;
        when(bookingService.updateBookingStatus(bookingId, userId, approved))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
        verify(bookingService, times(1)).updateBookingStatus(bookingId, userId, approved);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(bookingId, userId))
                .thenReturn(bookingResponseDto);
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void getUserBookings() throws Exception {
        BookingDto bookingResponse2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 2, 10, 10, 0))
                .end(LocalDateTime.of(2023, 2, 12, 10, 0))
                .item(ItemDto.builder().id(5L).name("Item2").build())
                .booker(bookingResponseDto.getBooker())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getUserBookings(userId, "ALL"))
                .thenReturn(List.of(bookingResponseDto, bookingResponse2));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[1].id").value(2L));
        verify(bookingService, times(1)).getUserBookings(userId, "ALL");
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(userId, "ALL"))
                .thenReturn(List.of(bookingResponseDto));
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].item.id").value(10L));
        verify(bookingService, times(1)).getOwnerBookings(userId, "ALL");
    }
}