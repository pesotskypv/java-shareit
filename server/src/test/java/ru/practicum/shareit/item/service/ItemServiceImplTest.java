package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoOwn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityForbiddenException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ContextConfiguration(classes = {UserServiceImpl.class, UserMapperImpl.class, ItemServiceImpl.class,
        ItemMapperImpl.class, ItemRequestMapperImpl.class, CommentMapperImpl.class, BookingMapperImpl.class})
public class ItemServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @SpyBean
    private UserMapper userMapper;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @SpyBean
    private ItemMapper itemMapper;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @SpyBean
    private ItemRequestMapper itemRequestMapper;
    @MockBean
    private CommentRepository commentRepository;
    @SpyBean
    private CommentMapper commentMapper;
    @MockBean
    private BookingRepository bookingRepository;
    @SpyBean
    private BookingMapper bookingMapper;

    @Test
    void createItem_returnsItemDtoWhenCreated() {
        Long userId = 1L;
        ItemDtoReq inItemDto = ItemDtoReq.builder().name("Дрель").description("Простая дрель")
                .available(true).requestId(1L).build();
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();
        ItemRequest originalItemRequest = ItemRequest.builder().id(inItemDto.getRequestId())
                .description("Хотел бы воспользоваться Дрелью")
                .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .created(today.minusDays(1)).build();

        Item inItem = Item.builder().name(inItemDto.getName()).description(inItemDto.getDescription())
                .available(inItemDto.getAvailable()).owner(originalUser).request(originalItemRequest).build();
        Item outItem = Item.builder().id(1L).name(inItem.getName()).description(inItem.getDescription()).available(true)
                .owner(inItem.getOwner()).request(inItem.getRequest()).build();

        ItemDto expectedItemDto = ItemDto.builder().id(outItem.getId()).name(outItem.getName())
                .description(outItem.getDescription()).available(outItem.getAvailable())
                .owner(UserDto.builder().id(outItem.getOwner().getId()).name(outItem.getOwner().getName())
                        .email(outItem.getOwner().getEmail()).build())
                .requestId(outItem.getRequest().getId()).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRequestRepository.findById(inItemDto.getRequestId()))
                .thenReturn(Optional.ofNullable(originalItemRequest));
        Mockito.when(itemRepository.save(inItem)).thenReturn(outItem);

        ItemDto actualItemDto = itemService.createItem(userId, inItemDto);

        Assertions.assertEquals(expectedItemDto.getId(), actualItemDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).save(inItem);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(inItemDto.getRequestId());
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItem(inItemDto, originalUser);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDto(outItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void createItem_requestIdIsNullReturnsItemDtoWhenCreated() {
        Long userId = 1L;
        ItemDtoReq inItemDto = ItemDtoReq.builder().name("Дрель").description("Простая дрель")
                .available(true).build();

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item inItem = Item.builder().name(inItemDto.getName()).description(inItemDto.getDescription())
                .available(inItemDto.getAvailable()).owner(originalUser).build();
        Item outItem = Item.builder().id(1L).name(inItem.getName()).description(inItem.getDescription()).available(true)
                .owner(inItem.getOwner()).build();

        ItemDto expectedItemDto = ItemDto.builder().id(outItem.getId()).name(outItem.getName())
                .description(outItem.getDescription()).available(outItem.getAvailable())
                .owner(UserDto.builder().id(outItem.getOwner().getId()).name(outItem.getOwner().getName())
                        .email(outItem.getOwner().getEmail()).build()).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.save(inItem)).thenReturn(outItem);

        ItemDto actualItemDto = itemService.createItem(userId, inItemDto);

        Assertions.assertEquals(expectedItemDto.getId(), actualItemDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).save(inItem);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItem(inItemDto, originalUser);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDto(outItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void getItem_ownerReturnsItemDtoOwnWhenFound() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);
        User user3 = User.builder().id(3L).name("user3").email("user3@user.com").build();

        Item originalItem = Item.builder().id(itemId).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(user3)
                        .created(today.minusDays(10)).build()).build();

        List<Comment> comments = Collections.singletonList(Comment.builder().id(1L).text("Коммент").item(originalItem)
                .author(user3).created(today.minusDays(1)).build());

        Booking lastBooking = Booking.builder().id(1L).start(today.minusDays(7)).end(today.minusDays(6))
                .item(originalItem).booker(user3).status(BookingStatus.APPROVED).build();
        Booking nextBooking = Booking.builder().id(2L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(originalItem).booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        ItemDtoOwn expectedItemDto = ItemDtoOwn.builder().id(originalItem.getId()).name(originalItem.getName())
                .description(originalItem.getDescription()).available(originalItem.getAvailable())
                .owner(UserDto.builder().id(originalItem.getOwner().getId()).name(originalItem.getOwner().getName())
                        .email(originalItem.getOwner().getEmail()).build())
                .request(ItemRequestDto.builder().id(originalItem.getRequest().getId())
                        .description(originalItem.getRequest().getDescription())
                        .requestor(UserDto.builder().id(originalItem.getRequest().getRequestor().getId())
                                .name(originalItem.getRequest().getRequestor().getName())
                                .email(originalItem.getRequest().getRequestor().getEmail()).build())
                        .created(originalItem.getRequest().getCreated()).build())
                .lastBooking(BookingDtoOwn.builder().id(lastBooking.getId()).bookerId(lastBooking.getBooker().getId())
                        .build())
                .nextBooking(BookingDtoOwn.builder().id(nextBooking.getId()).bookerId(nextBooking.getBooker().getId())
                        .build())
                .comments(Collections.singletonList(CommentDto.builder().id(comments.get(0).getId())
                        .text(comments.get(0).getText()).authorName(comments.get(0).getAuthor().getName())
                        .created(comments.get(0).getCreated()).build()))
                .build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));
        Mockito.when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, today,
                BookingStatus.APPROVED)).thenReturn(lastBooking);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, today,
                BookingStatus.APPROVED)).thenReturn(nextBooking);

        ItemDtoOwn actualOutItemDtoOwn;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualOutItemDtoOwn = itemService.getItem(userId, itemId);
        }

        Assertions.assertEquals(expectedItemDto.getId(), actualOutItemDtoOwn.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItemId(itemId);
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoOwner(originalItem, comments);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, today,
                        BookingStatus.APPROVED);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, today,
                        BookingStatus.APPROVED);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDtoOwner(lastBooking);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDtoOwner(nextBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getItem_notOwnerReturnsItemDtoOwnWhenFound() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);
        User user3 = User.builder().id(3L).name("user3").email("user3@user.com").build();

        Item originalItem = Item.builder().id(itemId).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(user3)
                        .created(today.minusDays(10)).build()).build();

        List<Comment> comments = Collections.singletonList(Comment.builder().id(1L).text("Коммент").item(originalItem)
                .author(user3).created(today.minusDays(1)).build());

        ItemDtoOwn expectedItemDto = ItemDtoOwn.builder().id(originalItem.getId()).name(originalItem.getName())
                .description(originalItem.getDescription()).available(originalItem.getAvailable())
                .owner(UserDto.builder().id(originalItem.getOwner().getId()).name(originalItem.getOwner().getName())
                        .email(originalItem.getOwner().getEmail()).build())
                .request(ItemRequestDto.builder().id(originalItem.getRequest().getId())
                        .description(originalItem.getRequest().getDescription())
                        .requestor(UserDto.builder().id(originalItem.getRequest().getRequestor().getId())
                                .name(originalItem.getRequest().getRequestor().getName())
                                .email(originalItem.getRequest().getRequestor().getEmail()).build())
                        .created(originalItem.getRequest().getCreated()).build())
                .comments(Collections.singletonList(CommentDto.builder().id(comments.get(0).getId())
                        .text(comments.get(0).getText()).authorName(comments.get(0).getAuthor().getName())
                        .created(comments.get(0).getCreated()).build()))
                .build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));
        Mockito.when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        ItemDtoOwn actualOutItemDtoOwn;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualOutItemDtoOwn = itemService.getItem(userId, itemId);
        }

        Assertions.assertEquals(expectedItemDto.getId(), actualOutItemDtoOwn.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItemId(itemId);
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoOwner(originalItem, comments);
    }

    @Test
    void getItem_returnsEntityNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemService.getItem(userId, itemId));

        assertTrue(exception.getMessage().contains("Попытка получить вещь несуществующим владельцем"));
    }

    @Test
    void findItemsByUser_returnsListItemDtoOwnWhenFound() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);
        User user3 = User.builder().id(3L).name("user3").email("user3@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(user3)
                        .created(today.minusDays(10)).build()).build();
        List<Item> originalItems = Collections.singletonList(originalItem);

        List<Comment> comments = Collections.singletonList(Comment.builder().id(1L).text("Коммент").item(originalItem)
                .author(user3).created(today.minusDays(1)).build());

        Booking lastBooking = Booking.builder().id(1L).start(today.minusDays(7)).end(today.minusDays(6))
                .item(originalItem).booker(user3).status(BookingStatus.APPROVED).build();
        Booking nextBooking = Booking.builder().id(2L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(originalItem).booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<ItemDtoOwn> expectedItemsDto = Collections.singletonList(ItemDtoOwn.builder().id(originalItem.getId())
                .name(originalItem.getName()).description(originalItem.getDescription())
                .available(originalItem.getAvailable()).owner(UserDto.builder().id(originalItem.getOwner().getId())
                        .name(originalItem.getOwner().getName()).email(originalItem.getOwner().getEmail()).build())
                .request(ItemRequestDto.builder().id(originalItem.getRequest().getId())
                        .description(originalItem.getRequest().getDescription())
                        .requestor(UserDto.builder().id(originalItem.getRequest().getRequestor().getId())
                                .name(originalItem.getRequest().getRequestor().getName())
                                .email(originalItem.getRequest().getRequestor().getEmail()).build())
                        .created(originalItem.getRequest().getCreated()).build())
                .lastBooking(BookingDtoOwn.builder().id(lastBooking.getId()).bookerId(lastBooking.getBooker().getId())
                        .build())
                .nextBooking(BookingDtoOwn.builder().id(nextBooking.getId()).bookerId(nextBooking.getBooker().getId())
                        .build())
                .comments(Collections.singletonList(CommentDto.builder().id(comments.get(0).getId())
                        .text(comments.get(0).getText()).authorName(comments.get(0).getAuthor().getName())
                        .created(comments.get(0).getCreated()).build()))
                .build());

        Mockito.when(commentRepository.findAllByItemId(originalItem.getId())).thenReturn(comments);
        Mockito.when(itemRepository.findItemsByOwnerId(userId)).thenReturn(originalItems);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(originalItem.getId(),
                today, BookingStatus.APPROVED)).thenReturn(lastBooking);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(originalItem.getId(),
                today, BookingStatus.APPROVED)).thenReturn(nextBooking);

        List<ItemDtoOwn> actualOutItemsDtoOwn;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualOutItemsDtoOwn = itemService.findItemsByUser(userId);
        }

        Assertions.assertEquals(expectedItemsDto.get(0).getId(), actualOutItemsDtoOwn.get(0).getId());
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(originalItem.getId());
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findItemsByOwnerId(userId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoOwner(originalItem, comments);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(originalItem.getId(), today,
                        BookingStatus.APPROVED);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(originalItem.getId(), today,
                        BookingStatus.APPROVED);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDtoOwner(lastBooking);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDtoOwner(nextBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void findItemsByText_returnsListItemDtoWhenFound() {
        Long userId = 1L;
        String text = "дрель";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .created(today.minusDays(1)).build()).build();
        List<Item> outItems = Collections.singletonList(originalItem);

        List<ItemDto> expectedItemsDto = Collections.singletonList(ItemDto.builder().id(originalItem.getId())
                .name(originalItem.getName()).description(originalItem.getDescription())
                .available(originalItem.getAvailable())
                .owner(UserDto.builder().id(originalItem.getOwner().getId()).name(originalItem.getOwner().getName())
                        .email(originalItem.getOwner().getEmail()).build())
                .requestId(originalItem.getRequest().getId()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findItemsByText(text)).thenReturn(outItems);

        List<ItemDto> actualOutItemsDto = itemService.findItemsByText(userId, text);

        Assertions.assertEquals(expectedItemsDto.get(0).getId(), actualOutItemsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findItemsByText(text);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDto(originalItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void findItemsByText_returnsEntityNotFoundException() {
        Long userId = 1L;
        String text = "дрель";

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemService.findItemsByText(userId, text));

        assertTrue(exception.getMessage().contains("Попытка поиска вещи несуществующим пользователем"));
    }

    @Test
    void findItemsByText_textIsBlankReturnsEmptyList() {
        Long userId = 1L;
        String text = "";
        List<ItemDto> expectedItemsDto = Collections.emptyList();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        List<ItemDto> actualOutItemsDto = itemService.findItemsByText(userId, text);

        Assertions.assertEquals(expectedItemsDto, actualOutItemsDto);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateItem_returnsItemDtoWhenUpdated() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inItemDto = ItemDto.builder().id(itemId).name("Дрель 2").description("Простая дрель 2").available(false)
                .build();
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .created(today.minusDays(1)).build()).build();
        Item outItem = Item.builder().id(originalItem.getId()).name(inItemDto.getName())
                .description(inItemDto.getDescription()).available(inItemDto.getAvailable())
                .owner(originalItem.getOwner()).request(originalItem.getRequest()).build();
        ItemDto expectedItemDto = ItemDto.builder().id(outItem.getId()).name(outItem.getName())
                .description(outItem.getDescription()).available(outItem.getAvailable())
                .owner(UserDto.builder().id(outItem.getOwner().getId()).name(outItem.getOwner().getName())
                        .email(outItem.getOwner().getEmail()).build())
                .requestId(outItem.getRequest().getId()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(originalItem));
        Mockito.when(itemRepository.save(outItem)).thenReturn(outItem);

        ItemDto actualOutItemDto = itemService.updateItem(userId, itemId, inItemDto);

        Assertions.assertEquals(expectedItemDto.getId(), actualOutItemDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verify(itemRepository, Mockito.times(1)).save(outItem);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDto(outItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void updateItem_nameIsBlankReturnsEntityValidationException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inItemDto = ItemDto.builder().id(itemId).name("").description("Простая дрель 2").available(false)
                .build();

        Exception exception = assertThrows(EntityValidationException.class, () ->
                itemService.updateItem(userId, itemId, inItemDto));

        assertTrue(exception.getMessage().contains("Название вещи не может быть пустым"));
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void updateItem_descriptionIsBlankReturnsEntityValidationException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inItemDto = ItemDto.builder().id(itemId).name("Дрель 2").description("").available(false)
                .build();

        Exception exception = assertThrows(EntityValidationException.class, () ->
                itemService.updateItem(userId, itemId, inItemDto));

        assertTrue(exception.getMessage().contains("Описание вещи не может быть пустым"));
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void updateItem_returnsEntityNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inItemDto = ItemDto.builder().id(itemId).name("Дрель 2").description("Простая дрель 2").available(false)
                .build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemService.updateItem(userId, itemId, inItemDto));

        assertTrue(exception.getMessage().contains("Попытка отредактировать вещь несуществующим владельцем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void updateItem_notOwnerReturnsEntityForbiddenException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inItemDto = ItemDto.builder().id(itemId).name("Дрель 2").description("Простая дрель 2").available(false)
                .build();
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(3L).name("user3").email("user3@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .created(today.minusDays(1)).build()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityForbiddenException.class, () ->
                itemService.updateItem(userId, itemId, inItemDto));

        assertTrue(exception.getMessage().contains("Попытка отредактировать вещь пользователем не являющимся её " +
                "владельцем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void addComment_returnsCommentDtoWhenFound() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto inCommentDto = CommentDto.builder().text("Комментарий").build();
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser).created(today.minusDays(3)).build()).build();

        Booking originalBooking = Booking.builder().id(1L).start(today.minusDays(2)).end(today.minusDays(3))
                .item(originalItem).booker(originalUser).status(BookingStatus.APPROVED).build();

        Comment inComment = Comment.builder().text(inCommentDto.getText()).item(originalItem).author(originalUser)
                .created(today).build();
        Comment outComment = Comment.builder().id(1L).text(inComment.getText()).item(inComment.getItem())
                .author(inComment.getAuthor()).created(inComment.getCreated()).build();
        CommentDto expectedCommentDto = CommentDto.builder().id(1L).text(outComment.getText())
                .authorName(outComment.getAuthor().getName()).created(outComment.getCreated()).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(originalItem));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(userId, itemId,
                BookingStatus.APPROVED, today)).thenReturn(originalBooking);
        Mockito.when(commentRepository.save(inComment)).thenReturn(outComment);

        CommentDto actualCommentDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualCommentDto = itemService.addComment(userId, itemId, inCommentDto);
        }

        Assertions.assertEquals(expectedCommentDto.getId(), actualCommentDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(userId, itemId, BookingStatus.APPROVED,
                        today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(commentRepository, Mockito.times(1)).save(inComment);
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(commentMapper, Mockito.times(1)).toComment(inCommentDto, originalItem,
                originalUser, today);
        Mockito.verify(commentMapper, Mockito.times(1)).toCommentDto(outComment);
        Mockito.verifyNoMoreInteractions(commentMapper);
    }

    @Test
    void addComment_returnsEntityValidationException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto inCommentDto = CommentDto.builder().text("Комментарий").build();
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser).created(today.minusDays(3)).build()).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(originalItem));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(userId, itemId,
                BookingStatus.APPROVED, today)).thenReturn(null);

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                itemService.addComment(userId, itemId, inCommentDto);
            }
        });

        assertTrue(exception.getMessage().contains("Пользователь не брал вещь в аренду, или не вышел срок окончания " +
                "аренды"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(userId, itemId, BookingStatus.APPROVED,
                        today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }
}
