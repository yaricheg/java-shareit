package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithoutValid;
import ru.practicum.shareit.item.model.ItemModel;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemModel createItem(ItemDto item, Integer user) {
        Set<Integer> users = checkUserid(user);
        if (!users.contains(user)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return itemStorage.createItem(item, user);
    }

    @Override
    public ItemModel updateItem(Integer itemId, ItemDtoWithoutValid itemDto, Integer userId) {
        ItemModel itemModel = getItemById(itemId);
        if (!itemModel.getOwner().equals(userId)) {
            throw new NotFoundException("Данная вещь пользователю с  " +
                    "id " + userId + " не принадлежит");
        }
        return itemStorage.updateItem(itemId, itemDto, userId);
    }

    @Override
    public ItemModel getItemById(Integer itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public Collection<ItemModel> getItemsOfUser(Integer userId) {
        return itemStorage.getItemsOfUser(userId);
    }

    @Override
    public Collection<ItemModel> getItemsSearch(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsSearch(text);
    }

    private Set<Integer> checkUserid(Integer user) {
        return userStorage.getAllUser()
                .stream()
                .map(userObject -> userObject.getId())
                .collect(Collectors.toSet());
    }
}
