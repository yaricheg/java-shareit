package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingDto booking, Long userId) {
        Optional<Item> item = itemRepository.findById(booking.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BadRequestException("Время начала или конца " +
                    "бронирования не может быть равным null");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Введите " +
                    "правильное время бронирования");
        }

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("Время начала бронирования " +
                    "не может быть позже конца бронирования");
        }
        if (item.get().isAvailable() == false) {
            throw new BadRequestException("Вещь занята");
        }
        Optional<User> user = userRepository.findById(userId);
        Booking bookingModel = BookingMapper.toBooking(booking, user.get(), item.get());
        bookingModel.setStatus(WAITING);
        bookingRepository.save(bookingModel);
        return getBookingById(bookingModel.getId());
    }

    @Override
    public BookingDto confirmationBooking(Long bookingId, Boolean approved, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<User> user = userRepository.findById(userId);
        Item item = booking.get().getItem();

        if (booking.isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        if (user.isEmpty()) {
            throw new BadRequestException("Пользователь не найден");
        }
        if (approved == null) {
            throw new BadRequestException("Ошибка подтверждения");
        }
        if (approved == true) {
            booking.get().setStatus(APPROVED);
            item.setLastBooking(booking.get().getStart());
            item.setNextBooking(booking.get().getEnd());
            booking.get().setItem(item);
            itemRepository.save(item);
        }
        if (approved == false) {
            booking.get().setStatus(REJECTED);
        }
        bookingRepository.save(booking.get());
        return BookingMapper.toBookingDto(bookingRepository.getById(bookingId));
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
    }

    @Override
    public Collection<BookingDto> getAllBookings(String state, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BadRequestException("Пользователь не найден");
        }
        List<Booking> bookings = new ArrayList<>();
        if (state.equals("ALL")) {
            bookings = bookingRepository.getBookingsByUserWithoutStatus(userId);
        }
        if (BookingStatus.values().equals(state)) {
            bookings = bookingRepository.getBookingsByUserWithStatus(userId, state);
        }
        if (state.equals("CURRENT")) {
            return null;
        }
        if (state.equals("PAST")) {
            return null;
        }
        if (state.equals("FUTURE")) {
            return null;
        }
        return bookingsDto(bookings);
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long ownerId, String state) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Booking> bookings = new ArrayList<>();
        if (state.equals("ALL")) {
            bookings = bookingRepository.getBookingsByOwnerWithoutStatus(ownerId);
        }
        if (BookingStatus.values().equals(state)) {
            bookings = bookingRepository.getBookingsByOwnerWithStatus(ownerId, state);
        }
        if (state.equals("CURRENT")) {
            return null;
        }
        if (state.equals("PAST")) {
            return null;
        }
        if (state.equals("FUTURE")) {
            return null;
        }
        return bookingsDto(bookings);
    }

    private List<BookingDto> bookingsDto(Collection<Booking> bookings) {
        List<BookingDto> bookingsDto = bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking))
                .collect(Collectors.toList());
        return bookingsDto;
    }
}
