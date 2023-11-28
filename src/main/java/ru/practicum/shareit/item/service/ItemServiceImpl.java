package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.exception.EntityForbiddenException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка добавить вещь несуществующим владельцем"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoOwner getItem(Long userId, Long itemId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получить вещь несуществующим владельцем");

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        if (comments == null) comments = new ArrayList<>();

        ItemDtoOwner itemDtoOwner = itemMapper.toItemDtoOwner(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка получить несуществующую вещь")), comments);

        if (itemDtoOwner.getOwner().getId().equals(userId)) addLastAndNextBookingsToItemDtoOwner(itemDtoOwner);

        return itemDtoOwner;
    }

    @Override
    public List<ItemDtoOwner> findItemsByUser(Long userId) {
        List<ItemDtoOwner> itemDtoOwners = itemRepository.findItemsByOwnerId(userId).stream()
                .map(i -> {
                    List<Comment> comments = commentRepository.findAllByItemId(i.getId());

                    if (comments == null) comments = new ArrayList<>();

                    return itemMapper.toItemDtoOwner(i, comments);
                }).collect(Collectors.toList());

        for (ItemDtoOwner itemDtoOwner : itemDtoOwners) {
            addLastAndNextBookingsToItemDtoOwner(itemDtoOwner);
        }

        return itemDtoOwners;
    }

    @Override
    public List<ItemDto> findItemsByText(Long userId, String text) {
        List<ItemDto> itemDtos = Collections.emptyList();

        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка поиска вещи несуществующим пользователем");

        if (!text.isBlank())
            itemDtos = itemRepository.findItemsByText(text).stream()
                    .map(itemMapper::toItemDto).collect(Collectors.toList());

        return itemDtos;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка отредактировать вещь несуществующим владельцем");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка отредактировать несуществующую вещь"));

        if (!Objects.equals(userId, item.getOwner().getId())) {
            String textError = "Попытка отредактировать вещь пользователем не являющимся её владельцем";

            log.debug(textError);
            throw new EntityForbiddenException(textError);
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (name != null && name.isBlank()) {
            String textError = "Название вещи не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }
        if (description != null && description.isBlank()) {
            String textError = "Описание вещи не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }
        if (name != null) item.setName(name);
        if (description != null) item.setDescription(description);
        if (available != null) item.setAvailable(available);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка добавить комментарий несуществующим " +
                        "пользователем"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка добавить комментарий к несуществующей вещи"));

        if (bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()) == null) {
            String textError = "Пользователь не брал вещь в аренду, или не вышел срок окончания аренды";

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }

        return commentMapper.toCommentDto(commentRepository.save(commentMapper.toComment(commentDto, item, user,
                LocalDateTime.now())));
    }

    private void addLastAndNextBookingsToItemDtoOwner(ItemDtoOwner itemDtoOwner) {
        itemDtoOwner.setLastBooking(bookingMapper.toBookingDtoOwner(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemDtoOwner.getId(), LocalDateTime.now(),
                        BookingStatus.APPROVED)));
        itemDtoOwner.setNextBooking(bookingMapper.toBookingDtoOwner(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemDtoOwner.getId(), LocalDateTime.now(),
                        BookingStatus.APPROVED)));
    }
}