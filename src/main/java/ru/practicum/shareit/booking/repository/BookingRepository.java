package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                           LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId,
                                                           BookingStatus status, LocalDateTime end);


    @Query("SELECT b FROM Booking b WHERE b.item IN :items AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC")
    List<Booking> findLastBookings(@Param("items") List<Item> items);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookings(@Param("items") List<Item> items);
}
