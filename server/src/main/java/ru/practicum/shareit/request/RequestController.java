package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // список своих запросов вместе с данными об ответах на них
    public Collection<ItemRequestDto> getRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK) // список своих запросов вместе с данными об ответах на них
    public Collection<ItemRequestDto> getRequestsUsers() {
        return requestService.getRequestsUsers();
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(@PathVariable("requestId") long requestId) {
        return requestService.getRequestById(requestId);
    }

}

