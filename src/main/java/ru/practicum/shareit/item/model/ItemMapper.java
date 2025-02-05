package ru.practicum.shareit.item.model;
import ru.practicum.shareit.user.model.User;


public class ItemMapper {


    public static ItemDto toItemDto(Item item ) {
        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                item.getLastBooking() != null ? null : null,
                item.getNextBooking() != null ? null : null,
                item.getComments() != null ? item.getComments() : null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(user);
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());
        return item;
    }

    public static Item toItemWithoutRequest(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(user);
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(Long.valueOf(0));
        return item;
    }

}
