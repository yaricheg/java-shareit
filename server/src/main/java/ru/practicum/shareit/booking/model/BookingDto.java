package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    @Future(message = "Указанная дата и время start должны быть в будущем.")
    @NotNull(message = "Поле 'start' обязательно для заполнения")
    private LocalDateTime start;

    @Future(message = "Указанная дата и время end должны быть в будущем.")
    @NotNull(message = "Поле 'end' обязательно для заполнения")
    private LocalDateTime end;

    @NotNull(message = "Поле 'itemId' обязательно для заполнения")
    private Long itemId;

    private BookingStatus status;

    private UserDto booker;

    private ItemDto item;
}
