package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item createItem(ItemDto item, Integer user);

    Item updateItem(Integer itemId, ItemUpdateRequestDto itemDto, Integer userId);

    Item getItemById(Integer itemId);

    Collection<Item> getItemsOfUser(Integer userId);

    Collection<Item> getItemsSearch(String text);

}
