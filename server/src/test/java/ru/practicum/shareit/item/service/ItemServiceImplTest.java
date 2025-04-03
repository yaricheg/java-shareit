package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User user;
    private User booker;
    private Item item1;
    private Item item2;

    private ItemRequest itemRequest1;

    private ItemRequest itemRequest2;

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        itemRequest1 = requestRepository.save(ItemRequest.builder()
                .requestor(booker)
                .created(LocalDateTime.now())
                .description("Description")
                .build());

        itemRequest2 = requestRepository.save(ItemRequest.builder()
                .requestor(booker)
                .created(LocalDateTime.now())
                .description("Description2")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user.getId())
                .name("item1")
                .description("description1")
                .available(true)
                .request(itemRequest1)
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user.getId())
                .name("Item2")
                .description("Desc2")
                .available(true)
                .request(itemRequest2)
                .build());
    }

    @Test
    void createItemThenReturnItemDto() {
        ItemDto newItemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(itemRequest1.getId())
                .build();
        ItemDto created = itemService.createItem(newItemDto, user.getId());
        assertNotNull(created);
        assertEquals("Item", created.getName());
        assertEquals("Description", created.getDescription());
        assertTrue(created.getAvailable());
    }

    @Test
    void createCommentWithoutBookingThenValidationException() {
        CommentDto comment = CommentDto.builder()
                .text("Should fail")
                .build();
        assertThrows(ValidationException.class,
                () -> itemService.addComment(booker.getId(), item2.getId(), comment));
    }

    @Test
    void updateItemThenReturnUpdatedItemDto() {
        ItemDto update = ItemDto.builder()
                .name("Name")
                .description("Description")
                .available(false)
                .requestId(itemRequest1.getId())
                .build();

        ItemDto updatedDto = itemService.updateItem(update, item1.getId(), user.getId());

        assertNotNull(updatedDto);
        assertEquals("Name", updatedDto.getName());
        assertEquals("Description", updatedDto.getDescription());
        assertFalse(updatedDto.getAvailable());
    }

    @Test
    void updateItemWithNonOwnerThenNotFoundException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(update, item1.getId(), booker.getId()));
    }

    @Test
    void updateItemWithNonItemThenNotFoundException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(update, 999L, user.getId()));
    }

    @Test
    void updateItemWithNonUserThenNotFoundException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(update, item1.getId(), 999L));
    }

    @Test
    void getItemByIdThenReturnItemDto() {
        ItemDto itemDto = itemService.getItemById(item1.getId());
        assertNotNull(itemDto);
        assertTrue(itemDto.getName().equals(item1.getName()));
    }

    @Test
    void getItemsThenReturnUpdatedItemDto() {
        List<ItemDto> items = itemService.getItems(user.getId());
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item1.getName())));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item2.getName())));
    }

    @Test
    void getItemByIdWithNonExistingItemThenNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999));
    }

    @Test
    void getByIdForNonOwnerThenReturnDtoWithoutBookingInfo() {
        CommentDto commentRequest = CommentDto.builder()
                .text("Отличный товар!")
                .build();
        try {
            itemService.addComment(booker.getId(), item1.getId(), commentRequest);
        } catch (Exception ignored) {
        }
        ItemDto dto = itemService.getItemById(item1.getId());
        assertNotNull(dto);
        assertNull(dto.getLastBooking(), "lastBooking должна быть null для не-владельца");
        assertNull(dto.getNextBooking(), "nextBooking должна быть null для не-владельца");

        if (dto.getComments() != null && !dto.getComments().isEmpty()) {
            assertTrue(dto.getComments()
                    .stream()
                    .anyMatch(c -> c.getText().equals("Отличный товар!")));
        }
    }

    @Test
    void searchItemsThenReturnSuitableItemsDto() {
        Collection<ItemDto> result = itemService.searchItems("item1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1.getName(), result.stream()
                .collect(Collectors.toList()).getFirst().getName());
    }

    @Test
    void searchByEmptyTextThenReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItems("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void addComment() {
        LocalDateTime now = LocalDateTime.now().minusSeconds(3);
        Booking booking = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusSeconds(1))
                .end(now.plusSeconds(2))
                .build();
        bookingRepository.save(booking);
        CommentDto comment = CommentDto.builder()
                .text("Comment")
                .build();
        CommentDto responseDto = itemService.addComment(booker.getId(), item1.getId(), comment);
        assertNotNull(responseDto);
        assertEquals(responseDto.getAuthorName(), booker.getName());
        assertEquals(responseDto.getText(), comment.getText());
    }
}