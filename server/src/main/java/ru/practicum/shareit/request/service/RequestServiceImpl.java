package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.getReferenceById(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto, 0, user);
        itemRequest.setCreated(LocalDateTime.now());
        return RequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getRequests(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId не должно быть пустым");
        }
        List<ItemRequest> itemRequests = requestRepository.findByRequestorId(userId);
        if (itemRequests.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getRequestsUsers() {
        List<ItemRequest> itemRequests = requestRepository.findAll();
        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запрос  с ID: " + requestId + " не найден"));
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemsDto;
        if (items != null) {
            itemsDto = items.stream()
                    .map(item -> {
                        ItemDto itemDto = ItemMapper.toItemDto(item);
                        return itemDto;
                    })
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemsDto);
        }
        return itemRequestDto;
    }

}
