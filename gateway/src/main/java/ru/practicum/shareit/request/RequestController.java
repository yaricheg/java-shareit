package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // список своих запросов вместе с данными об ответах на них
    public ResponseEntity<Object> getRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK) // список своих запросов вместе с данными об ответах на них
    public ResponseEntity<Object> getRequestsUsers() {
        return itemRequestClient.getRequestsUsers();
    }


    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") long requestId) {
        return itemRequestClient.getRequestById(requestId);
    }

}


