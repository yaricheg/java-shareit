package ru.practicum.shareit.item.model;

public class ItemMapper {

    public static ItemDto toItemDto(ItemModel item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

}
