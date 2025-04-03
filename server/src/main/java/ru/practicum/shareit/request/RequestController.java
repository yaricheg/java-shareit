package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getRequestsUsers() {
        return requestService.getRequestsUsers();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable("requestId") long requestId) {
        return requestService.getRequestById(requestId);
    }

}

