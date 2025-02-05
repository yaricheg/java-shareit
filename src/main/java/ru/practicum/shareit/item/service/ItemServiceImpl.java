package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

   // private final BookingRepository bookingRepository;


    @Override
    public ItemDto createItem(ItemDto item, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item itemModel = new Item();
        if (item.getRequest() == null) {
            itemModel = ItemMapper.toItemWithoutRequest(item, user.get());
            itemRepository.save(itemModel);
        }
        if (item.getRequest() != null) {
            itemModel = ItemMapper.toItem(item, user.get());
            itemRepository.save(itemModel);

        }
        return getItemById(itemModel.getId());

    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getById(itemId);
       /* List<Booking> bookings = bookingRepository.findByItemId(itemId);
        if (bookings.size() != 0) {
            Optional<Booking> booking = bookings.stream()
                    .filter(booking1 -> booking1.getStart().getSecond() < LocalDateTime.now().getSecond()
                            && booking1.getEnd().getSecond() > LocalDateTime.now().getSecond())
                    .findFirst();
            if (booking.isEmpty()) {
                booking = Optional.of(bookings.getLast());
            }
            item.setLastBooking(booking.get().getStart());
            item.setNextBooking(booking.get().getEnd());
            itemRepository.save(item);
        }*/
        return ItemMapper.toItemDto(item);
    }


    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateRequestDto itemDto, Long userId) {
        Item item = itemRepository.getById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Данная вещь пользователю с  " +
                    "id " + userId + " не принадлежит");
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }


    @Override
    public Collection<ItemDto> getItemsOfUser(Long userId) {
        Collection<ItemDto> itemsDto = itemRepository.getByOwnerItem(userId).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return itemsDto;
    }


    @Override
    public Collection<ItemDto> getItemsSearch(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemsDtoName = itemRepository
                .findByNameContainingIgnoreCaseAndAvailable(text, true)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());

        Collection<ItemDto> itemsDtoDescription = itemRepository
                .findByDescriptionContainingIgnoreCaseAndAvailable(text, true)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
        itemsDtoName.addAll(itemsDtoDescription);
        return itemsDtoName;
    }

    @Override
    public CommentDto createItemComment(Long itemId, CommentDto commentDto, Long authorId) {
        Item item = itemRepository.getById(itemId);
        User author = userRepository.getById(authorId);
        Comment comment = CommentMapper.toComment(commentDto, author, item);
        if (item.getNextBooking() != null) {
            if (comment.getCreated().isBefore(item.getNextBooking())) {
                throw new BadRequestException("Пользователь не может оставить " +
                        "комментарий до окончания бронирования");
            }
        }
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentRepository.getById(comment.getId()));
    }
}

