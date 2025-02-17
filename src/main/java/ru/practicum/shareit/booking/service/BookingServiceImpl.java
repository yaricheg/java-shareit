package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;



    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!item.isAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }
        List<Booking> bookings = bookingRepository.findNextBookingsForItem(item);
        bookings.stream()
                .forEach(b -> {
                    if (bookingDto.getStart().isAfter(b.getStart()) &
                            bookingDto.getStart().isBefore(b.getEnd())) {
                        throw new BadRequestException("Введите другое время бронирования");
                    }
                    if (bookingDto.getEnd().isAfter(b.getStart()) &
                            bookingDto.getEnd().isBefore(b.getEnd())) {
                        throw new BadRequestException("Введите другое время бронирования");
                    }
                });
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }


    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getItem().getOwner() != userId) {
            throw new ValidationException("Только владелец вещи может подтверждать или отклонять бронирование");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }


    @Override
    @Transactional
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Доступ запрещен: " +
                    "Только автор бронирования или владелец вещи могут просматривать бронирование");
        }

        return BookingMapper.toBookingDto(booking);
    }


    @Override
    @Transactional
    public List<BookingDto> getUserBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        List<Booking> bookings;

        switch (State.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        List<Booking> bookings;

        switch (State.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerOrderByStartDesc(ownerId);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
