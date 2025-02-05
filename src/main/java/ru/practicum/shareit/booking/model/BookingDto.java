package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    private Long id; //— уникальный идентификатор бронирования;

    private LocalDateTime start; // — дата и время начала бронирования;

    private LocalDateTime end;//— дата и время конца бронирования;

    @ReadOnlyProperty
    private Long itemId;

    private Item item; //— вещь, которую пользователь бронирует;

    private User booker; //— пользователь, который осуществляет бронирование


    private BookingStatus status;

}
