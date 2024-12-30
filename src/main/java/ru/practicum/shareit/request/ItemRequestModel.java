package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestModel {

    private Integer id;

    private String description;

    private String requestor; // пользователь создавший запрос

    private LocalDateTime created;


}
