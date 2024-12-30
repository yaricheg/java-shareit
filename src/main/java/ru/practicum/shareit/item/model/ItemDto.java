package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {

    @NotBlank
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private Boolean available;

    private Integer request;

}
