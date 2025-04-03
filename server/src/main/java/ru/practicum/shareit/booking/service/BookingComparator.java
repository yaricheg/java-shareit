package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Comparator;

@Component
public class BookingComparator implements Comparator<Booking> {
    @Override
    public int compare(Booking booking1, Booking booking2) {
        return booking1.getStart().compareTo(booking2.getStart());
    }
}
