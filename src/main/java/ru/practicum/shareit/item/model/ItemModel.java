package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.ItemRequestModel;


@Data
@Validated
public class ItemModel {

    @NotNull
    private Integer id;

    @NotBlank
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private boolean available;

    @NotNull
    private Integer owner;

    private ItemRequestModel request;
}
