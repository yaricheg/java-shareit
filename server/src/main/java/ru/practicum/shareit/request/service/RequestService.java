package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.Collection;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    Collection<ItemRequestDto> getRequests(long userId);

    Collection<ItemRequestDto> getRequestsUsers();

    ItemRequestDto getRequestById(long requestId);

}
