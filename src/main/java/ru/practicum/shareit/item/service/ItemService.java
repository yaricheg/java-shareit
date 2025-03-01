package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItems(long userId);

    Collection<ItemDto> searchItems(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
