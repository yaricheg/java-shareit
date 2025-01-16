package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Data
@Validated
public class Item {

    private Integer id;

    private String name;

    private String description;

    private boolean available;

    private Integer owner;

    private ItemRequestModel request;
}
