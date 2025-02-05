package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;


import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = ?1 " +
            "ORDER BY b.start_date",
            nativeQuery = true)
    List<Booking> getBookingsByUserWithoutStatus(Long userId);

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start_date",
            nativeQuery = true)
    List<Booking> getBookingsByUserWithStatus(Long userId, String status);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id IN " +
            "(SELECT * FROM items i WHERE i.owner_id = ?1)" +
            "ORDER BY b.startDate",
            nativeQuery = true)
    List<Booking> getBookingsByOwnerWithoutStatus(Long ownerId);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id IN " +
            "(SELECT * FROM items i WHERE i.owner_id = ?1) AND b.status = ?2 " +
            "ORDER BY b.startDate",
            nativeQuery = true)
    List<Booking> getBookingsByOwnerWithStatus(Long ownerId, String status);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id = ?1",
            nativeQuery = true)
    List<Booking> findByItemId(Long itemId);
}
