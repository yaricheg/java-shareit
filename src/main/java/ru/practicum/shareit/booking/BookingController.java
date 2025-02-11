package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создано бронирование вещи {}");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        //@RequestBody BookingUpdateRequestDto booking
        log.info("Подтверждение или отклонение запроса на бронирование {}");
        return bookingService.confirmationBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer user,
                                     @PathVariable("bookingId") Long bookingId) {
        log.info("Получение бронирование по id {}");
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookings(@RequestParam(required = false) String owner,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех бронирований {}");

        return bookingService.getAllBookings(state, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов к вещам пользовтателей{}");
        return bookingService.getBookingsByOwner(userId, state);
    }

}
