package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User user;
    private User booker;
    private Item item1;
    private Item item2;
    private LocalDateTime now;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        itemRequest1 = requestRepository.save(ItemRequest.builder()
                .requestor(booker)
                .created(LocalDateTime.now())
                .description("Description1")
                .build());

        itemRequest2 = requestRepository.save(ItemRequest.builder()
                .requestor(booker)
                .created(LocalDateTime.now())
                .description("Description2")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user.getId())
                .name("item1")
                .description("description1")
                .available(true)
                .request(itemRequest1)
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user.getId())
                .name("Item2")
                .description("Desc2")
                .available(true)
                .request(itemRequest2)
                .build());

        Booking pastBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.minusDays(5))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .item(item2)
                .booker(booker)
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking futureBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        Booking rejectedBooking = Booking.builder()
                .item(item2)
                .booker(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .status(BookingStatus.REJECTED)
                .build();
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));
    }


    @Test
    void createBookingThenReturnBookingDto() {
        BookingDto requestDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(2).plusSeconds(1))
                .end(now.plusDays(3))
                .build();
        BookingDto response = bookingService.createBooking(requestDto, booker.getId());
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(requestDto.getStart(), response.getStart());
        assertEquals(requestDto.getEnd(), response.getEnd());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(item1.getId(), response.getItem().getId());
        assertEquals(booker.getId(), response.getBooker().getId());
    }

    @Test
    void createBookingWithNonExistingItemThenNotFoundException() {
        BookingDto requestDto = BookingDto.builder()
                .itemId(9999L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(requestDto, booker.getId())
        );
    }

    @Test
    void createBookingNonExistingUserThenNotFoundException() {
        BookingDto requestDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        Long nonExistingUserId = 9999L;

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(requestDto, nonExistingUserId)
        );
    }

    @Test
    void createBookingForUnavailableItemThenValidationException() {
        item1.setAvailable(false);
        itemRepository.save(item1);

        BookingDto requestDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(requestDto, booker.getId())
        );
    }

    @Test
    void createBookingWithStartAfterEndThenBadRequestException() {
        BookingDto requestDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(2))
                .end(now.plusDays(1))
                .build();
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(requestDto, booker.getId())
        );
    }

    @Test
    void createBookingOverlappingWithExistingBookingThenValidationException() {
        var existingBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(existingBooking);

        BookingDto requestDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(3).plusHours(1))
                .end(now.plusDays(4).plusHours(1))
                .build();

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(requestDto, booker.getId())
        );
    }

    @Test
    void updateBookingStatusThenReturnUpdatedBookingDto() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());
        BookingDto response = bookingService.updateBookingStatus(user.getId(),
                booking.getId(), true);
        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void ownerApprovesBookingThenStatusChangesToApproved() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        BookingDto response = bookingService.updateBookingStatus(
                user.getId(), booking.getId(), true);
        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void nonOwnerTriesToApproveBookingThenValidationException() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        assertThrows(ValidationException.class, () ->
                bookingService.updateBookingStatus(booker.getId(),
                        booking.getId(), true)
        );
    }

    @Test
    void tryingToApproveNonWaitingBookingThenValidationException() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());
        assertThrows(ValidationException.class, () ->
                bookingService.updateBookingStatus(user.getId(), booking.getId(), true)
        );
    }

    @Test
    void tryingToApproveNonExistingBookingThenNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(
                        user.getId(), 999L, true)
        );
    }

    @Test
    void ownerRejectsBookingThenStatusRejected() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());
        BookingDto response = bookingService.updateBookingStatus(user.getId(),
                booking.getId(), false);
        assertNotNull(response);
        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void updateNonExistingBookingThenNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(user.getId(),
                        999L, true) // 999L — ID несуществующего бронирования
        );
    }

    @Test
    void getUserBookingsThenReturnBookingDto() {
        now = now.minusMinutes(20);
        bookingRepository.deleteAll();
        Booking booking = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusMinutes(5))
                .end(now.plusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item2)
                .start(now.plusSeconds(30))
                .end(now.plusSeconds(40))
                .build();
        bookingRepository.saveAll(List.of(booking, booking2));
        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), "ALL");
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item1.getName())));
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item2.getName())));
    }

    @Test
    void getOwnerBookings() {

    }

    @Test
    void getBookingsWithStatusCurrentThenReturnCurrentBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(user.getId(), "CURRENT");
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDto current = bookings.get(0);
        LocalDateTime nowTime = LocalDateTime.now();
        assertTrue(current.getStart().isBefore(nowTime) || current.getStart().isEqual(nowTime));
        assertTrue(current.getEnd().isAfter(nowTime) || current.getEnd().isEqual(nowTime));
        assertEquals(item2.getId(), current.getItem().getId());
    }

    @Test
    void getBookingsWithStatusFutureThenReturnFutureBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(user.getId(), "FUTURE");
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        LocalDateTime currentTime = LocalDateTime.now();
        for (BookingDto dto : bookings) {
            assertTrue(dto.getStart().isAfter(currentTime));
        }
    }

    @Test
    void getBookingsWithStatusPastThenReturnPastBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(user.getId(), "PAST");
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDto past = bookings.get(0);
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
        assertEquals(item1.getId(), past.getItem().getId());
    }

    @Test
    void getBookingsWithStatusWaitingThenReturnWaitingBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(user.getId(), "WAITING");
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDto waiting = bookings.get(0);
        assertEquals(BookingStatus.WAITING, waiting.getStatus());
    }

    @Test
    void getBookingsWithStatusRejectedThenReturnRejectedBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(user.getId(), "REJECTED");
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDto rejected = bookings.get(0);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }
}