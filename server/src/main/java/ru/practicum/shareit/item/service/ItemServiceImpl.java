package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        validateUserExists(userId);
        Item item = ItemMapper.toItem(itemDto, 0, userId);
        item.setOwner(userId);
        if (itemDto.getRequestId() != 0) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Данного id запроса вещи не существует"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        validateUserExists(userId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден"));

        if (existingItem.getOwner() != userId) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }

        ItemMapper.updateItemFields(existingItem, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден с ID: " + itemId));

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(long ownerId) {
        validateUserExists(ownerId);

        List<Item> items = itemRepository.findByOwner(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> lastBookingsList = bookingRepository.findLastBookings(items);
        List<Booking> nextBookingsList = bookingRepository.findNextBookings(items);

        Map<Long, Booking> lastBookings = lastBookingsList.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        Map<Long, Booking> nextBookings = nextBookingsList.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    itemDto.setLastBooking(
                            Optional.ofNullable(lastBookings.get(item.getId()))
                                    .map(Booking::getEnd)
                                    .orElse(null)
                    );

                    itemDto.setNextBooking(
                            Optional.ofNullable(nextBookings.get(item.getId()))
                                    .map(Booking::getStart)
                                    .orElse(null)
                    );

                    List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(comments);

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        validateUserExists(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден"));

        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не может оставить комментарий без завершенного бронирования");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    private void validateUserExists(long userId) {
        if (userId == 0) {
            throw new ValidationException("userId не должно быть пустым");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}
