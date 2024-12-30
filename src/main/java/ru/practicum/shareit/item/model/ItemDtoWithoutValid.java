package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDtoWithoutValid {

    private String name;

    private String description;

    private Boolean available;

}
