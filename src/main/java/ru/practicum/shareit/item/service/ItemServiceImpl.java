package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto item, Integer userId) {
        User user =  userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return itemMapper.toItemDto(itemStorage.createItem(item, userId));
    }

    @Override
    public ItemDto updateItem(Integer itemId, ItemUpdateRequestDto itemDto, Integer userId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Данная вещь пользователю с  " +
                    "id " + userId + " не принадлежит");
        }
        return itemMapper.toItemDto(itemStorage.updateItem(itemId, itemDto, userId));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsOfUser(Integer userId) {
        Collection<ItemDto> itemsDto = itemStorage.getItemsOfUser(userId).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> getItemsSearch(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemsDto = itemStorage.getItemsSearch(text).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return itemsDto;
    }


}
