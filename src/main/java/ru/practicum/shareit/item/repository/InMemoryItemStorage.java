package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Integer, Item> items = new HashMap();

    private final ItemMapper itemMapper;

    @Override
    public Item createItem(ItemDto itemDto, Integer user) {
        Item item = itemMapper.toItem(itemDto, user);
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Integer itemId, ItemUpdateRequestDto itemDto, Integer userId) {
        Item item = items.get(itemId);
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getItemsOfUser(Integer userId) {
        return items.values()
                .stream()
                .filter(item -> (item.getOwner().equals(userId)))
                .toList();
    }

    @Override
    public Collection<Item> getItemsSearch(String text) {
       Collection<Item> itemSearchName =  items.values()
                .stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getName(), text)
                        && item.isAvailable())
                .toList();
       if(itemSearchName.isEmpty()){
           return items.values()
                   .stream()
                   .filter(item -> StringUtils.containsIgnoreCase(item.getDescription(), text)
                           && item.isAvailable())
                   .toList();
       }
        return itemSearchName;
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
