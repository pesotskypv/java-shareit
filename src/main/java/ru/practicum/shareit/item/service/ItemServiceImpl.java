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
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.exception.EntityForbiddenException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDtoReq itemDtoReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка добавить вещь несуществующим владельцем"));
        Item item = itemMapper.toItem(itemDtoReq, user);
        Long requestId = itemDtoReq.getRequestId();

        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException("Попытка добавить вещь с несуществующим его " +
                            "запросом"));

            item.setRequest(itemRequest);
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoOwn getItem(Long userId, Long itemId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получить вещь несуществующим владельцем");

        ItemDtoOwn itemDtoOwn = itemMapper.toItemDtoOwner(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка получить несуществующую вещь")),
                commentRepository.findAllByItemId(itemId));

        if (itemDtoOwn.getOwner().getId().equals(userId)) addLastAndNextBookingsToItemDtoOwner(itemDtoOwn);

        return itemDtoOwn;
    }

    @Override
    public List<ItemDtoOwn> findItemsByUser(Long userId) {
        return itemRepository.findItemsByOwnerId(userId).stream().map(i ->
                itemMapper.toItemDtoOwner(i, commentRepository.findAllByItemId(i.getId())))
                .peek(this::addLastAndNextBookingsToItemDtoOwner).collect(Collectors.toList());
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
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка отредактировать вещь несуществующим владельцем");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка отредактировать несуществующую вещь"));

        if (!Objects.equals(userId, item.getOwner().getId())) {
            String textError = "Попытка отредактировать вещь пользователем не являющимся её владельцем";

            log.debug(textError);
            throw new EntityForbiddenException(textError);
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

    private void addLastAndNextBookingsToItemDtoOwner(ItemDtoOwn itemDtoOwn) {
        itemDtoOwn.setLastBooking(bookingMapper.toBookingDtoOwner(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemDtoOwn.getId(), LocalDateTime.now(),
                        BookingStatus.APPROVED)));
        itemDtoOwn.setNextBooking(bookingMapper.toBookingDtoOwner(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemDtoOwn.getId(), LocalDateTime.now(),
                        BookingStatus.APPROVED)));
    }
}