package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(ItemDto item, Integer user);

    ItemDto updateItem(Integer itemId, ItemUpdateRequestDto itemDto, Integer userId);

    ItemDto getItemById(Integer itemId);

    Collection<ItemDto> getItemsOfUser(Integer userId);

    Collection<ItemDto> getItemsSearch(String text);

}
