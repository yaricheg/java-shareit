package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public class BookingModel {

    @NotNull
    private Integer id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Integer itemId;

    @NotNull
    private Integer booker;

    private String status;
}
