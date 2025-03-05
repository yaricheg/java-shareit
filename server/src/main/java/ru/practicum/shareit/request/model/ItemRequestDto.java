package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Поле 'description' обязательно для заполнения")
    private String description;

    private Long requestor;

    private LocalDateTime created;

    private List<ItemDto> items;

}
