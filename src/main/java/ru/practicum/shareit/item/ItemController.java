package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer user) {
        ItemDto newItem = itemService.createItem(item, user);
        log.info("Вещь добавлена {}", newItem);
        return newItem;
    }

    @PatchMapping("/{itemId}") // изменить можно название, описание, статус
    public ItemDto updateItem(@PathVariable("itemId") Integer itemId,
                              @RequestBody ItemUpdateRequestDto item,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Integer itemId) {
        log.info("Просмотр информации по конкретной вещи");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Просмотр вещей пользователя ");
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search") // только доступные для аренды вещи
    public Collection<ItemDto> getItemsSearch(@RequestParam("text") String text) {
        log.info("Поиск вещей по тексту в названии ");
        return itemService.getItemsSearch(text);
    }

}
