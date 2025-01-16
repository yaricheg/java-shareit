package ru.practicum.shareit.item.model;

public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto, Integer user);
}
