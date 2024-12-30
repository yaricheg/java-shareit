package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithoutValid;
import ru.practicum.shareit.item.model.ItemModel;

import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Integer, ItemModel> items = new HashMap();

    @Override
    public ItemModel createItem(ItemDto item, Integer user) {
        ItemModel itemModel = new ItemModel();
        itemModel.setId(getNextId());
        itemModel.setName(item.getName());
        itemModel.setDescription(item.getDescription());
        itemModel.setOwner(user);
        itemModel.setAvailable(item.getAvailable());
        itemModel.setRequest(null);
        items.put(itemModel.getId(), itemModel);
        return itemModel;
    }

    @Override
    public ItemModel updateItem(Integer itemId, ItemDtoWithoutValid itemDto, Integer userId) {
        ItemModel itemModel = items.get(itemId);
        if (itemDto.getAvailable() != null) {
            itemModel.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            itemModel.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            itemModel.setName(itemDto.getName());
        }
        items.put(itemModel.getId(), itemModel);
        return itemModel;
    }

    @Override
    public ItemModel getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<ItemModel> getItemsOfUser(Integer userId) {
        return items.values()
                .stream()
                .filter(item -> (item.getOwner() == userId))
                .toList();
    }

    @Override
    public Collection<ItemModel> getItemsSearch(String text) {
        return items.values()
                .stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getName(), text)
                        && item.isAvailable())
                .toList();
    }

    private Integer getNextId() {
        Integer currentMaxId = items.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
