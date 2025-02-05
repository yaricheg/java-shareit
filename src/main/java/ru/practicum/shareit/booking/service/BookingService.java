package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(BookingDto booking, Long userId);

    BookingDto confirmationBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId);

    Collection<BookingDto> getAllBookings(String state, Long userId);

    Collection<BookingDto> getBookingsByOwner(Long ownerId, String state);
}
