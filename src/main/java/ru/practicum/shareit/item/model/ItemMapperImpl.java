package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    @Override
    public Item toItem(ItemDto itemDto, Integer user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(user);
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(null);
        return item;
    }

}
