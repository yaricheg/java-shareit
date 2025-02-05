package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemUpdateRequestDto;
import java.util.Collection;

public interface ItemService {

    ItemDto createItem(ItemDto item, Long user);

    ItemDto updateItem(Long itemId, ItemUpdateRequestDto itemDto, Long userId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getItemsOfUser(Long userId);

    Collection<ItemDto> getItemsSearch(String text);

    CommentDto createItemComment(Long itemId, CommentDto commentDto, Long userId);

}
