package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class RequestServiceImplTest {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User anotherUser;

    @BeforeEach
    void setup() {

        itemRepository.deleteAll();

        requestRepository.deleteAll();

        userRepository.deleteAll();

        requester = userRepository.save(new User(0, "Requester", "requester@mail.com"));

        anotherUser = userRepository.save(new User(0, "Another User", "another@mail.com"));
    }

    @Test
    void createRequestThenReturnItemRequestDto() {
        ItemRequestDto inputDto = new ItemRequestDto(null, "Ищу стол", requester.getId(),
                LocalDateTime.now(), null);
        ItemRequestDto outputDto = requestService.createRequest(inputDto, requester.getId());

        assertNotNull(outputDto);
        assertNotNull(outputDto.getId());
        assertEquals("Ищу стол", outputDto.getDescription());

    }

    @Test
    void createRequestFromNotExistUserThenThrowNotFoundException() {
        ItemRequestDto inputDto = new ItemRequestDto(null, "Ищу стол", requester.getId(),
                LocalDateTime.now(), null);

        long noExistUserId = 999L;
        assertThrows(NotFoundException.class, () -> requestService.createRequest(inputDto, noExistUserId));
    }

    @Test
    void getRequestsThenReturnListOfRequests() {
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .description("Запрос 1")
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("Запрос 2")
                .build();
        requestService.createRequest(itemRequestDto1, requester.getId());
        requestService.createRequest(itemRequestDto2, requester.getId());
        Collection<ItemRequestDto> requests = requestService.getRequests(requester.getId());
        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 1")));
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 2")));
    }

    @Test
    void whenGetRequestsForUserWithoutRequestsThenNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> requestService.getRequests((anotherUser.getId())));
    }

    @Test
    void getRequestByIdThenReturnItemRequestDto() {
        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Запрос для получения")
                .build();
        ItemRequestDto created = requestService.createRequest(inputDto, requester.getId());
        ItemRequest request = requestRepository.findById(created.getId()).orElseThrow();
        Item item = Item.builder()
                .name("Метла")
                .description("из соломы")
                .available(true)
                .request(request)
                .owner(anotherUser.getId())
                .build();
        itemRepository.save(item);
        ItemRequestDto outputDto = requestService.getRequestById(created.getId());
        assertNotNull(outputDto);
        assertEquals(created.getId(), outputDto.getId());
        assertEquals("Запрос для получения", outputDto.getDescription());
        assertNotNull(outputDto.getItems());
        assertFalse(outputDto.getItems().isEmpty());
        assertTrue(outputDto.getItems().stream().anyMatch(i -> i.getName().equals("Метла")));
    }

    @Test
    void getNonExistentRequestByIdThenThrowNotFoundException() {
        long nonExistingRequestId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(nonExistingRequestId));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingRequestId)));
    }
}
