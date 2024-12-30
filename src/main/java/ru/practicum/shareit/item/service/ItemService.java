package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithoutValid;
import ru.practicum.shareit.item.model.ItemModel;

import java.util.Collection;

public interface ItemService {

    ItemModel createItem(ItemDto item, Integer user);

    ItemModel updateItem(Integer itemId, ItemDtoWithoutValid itemDto, Integer userId);

    ItemModel getItemById(Integer itemId);

    Collection<ItemModel> getItemsOfUser(Integer userId);

    Collection<ItemModel> getItemsSearch(String text);

}
