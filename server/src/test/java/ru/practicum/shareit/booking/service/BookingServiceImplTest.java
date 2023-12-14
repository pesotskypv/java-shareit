package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ContextConfiguration(classes = {UserMapperImpl.class, ItemMapperImpl.class, ItemRequestMapperImpl.class,
        CommentMapperImpl.class, BookingServiceImpl.class, BookingMapperImpl.class})
public class BookingServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @SpyBean
    private UserMapper userMapper;
    @MockBean
    private ItemRepository itemRepository;
    @SpyBean
    private ItemMapper itemMapper;
    @SpyBean
    private ItemRequestMapper itemRequestMapper;
    @SpyBean
    private CommentMapper commentMapper;
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private BookingService bookingService;
    @SpyBean
    private BookingMapper bookingMapper;

    @Test
    void createBooking_returnsBookingDtoWhenCreated() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser)
                        .created(today.minusDays(1)).build())
                .build();

        Booking outBooking = Booking.builder().id(1L).start(bookingDtoReq.getStart()).end(bookingDtoReq.getEnd())
                .item(originalItem).booker(originalUser).status(BookingStatus.WAITING).build();

        BookingDto expectedBookingDto = BookingDto.builder().id(outBooking.getId()).start(outBooking.getStart())
                .end(outBooking.getEnd())
                .item(ItemDto.builder().id(outBooking.getItem().getId()).name(outBooking.getItem().getName())
                        .description(outBooking.getItem().getDescription())
                        .available(outBooking.getItem().getAvailable())
                        .owner(UserDto.builder().id(outBooking.getItem().getOwner().getId())
                                .name(outBooking.getItem().getOwner().getName())
                                .email(outBooking.getItem().getOwner().getEmail()).build())
                        .requestId(outBooking.getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBooking.getBooker().getId()).name(outBooking.getBooker().getName())
                        .email(outBooking.getBooker().getEmail()).build())
                .status(outBooking.getStatus())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));
        Mockito.when(bookingRepository.save(Booking.builder().start(bookingDtoReq.getStart()).end(bookingDtoReq
                        .getEnd()).item(originalItem).booker(originalUser).status(BookingStatus.WAITING).build()))
                .thenReturn(outBooking);

        BookingDto actualBookingDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingDto = bookingService.createBooking(userId, bookingDtoReq);
        }

        Assertions.assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Booking.builder()
                .start(bookingDtoReq.getStart()).end(bookingDtoReq.getEnd()).item(originalItem).booker(originalUser)
                .status(BookingStatus.WAITING).build());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void createBooking_returnsEntityNotFoundException() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(originalUser)
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .created(today.minusDays(1)).build())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.createBooking(userId, bookingDtoReq);
            }
        });

        assertTrue(exception.getMessage().contains("Нельзя арендовать собсьвенную вещь"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createBooking_itemNotAvailableReturnsEntityValidationException() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .build();

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(false)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser)
                        .created(today.minusDays(1)).build())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.createBooking(userId, bookingDtoReq);
            }
        });

        assertTrue(exception.getMessage().contains("Вещь не доступна для аренды"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createBooking_startIsBeforeNowReturnsEntityValidationException() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.minusDays(1))
                .end(today.plusDays(1)).build();

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser)
                        .created(today.minusDays(1)).build())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.createBooking(userId, bookingDtoReq);
            }
        });

        assertTrue(exception.getMessage().contains("Некорректный период аренды"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createBooking_startEqualsEndReturnsEntityValidationException() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 4, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.plusDays(1)).end(today.plusDays(1))
                .build();

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser)
                        .created(today.minusDays(1)).build())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.createBooking(userId, bookingDtoReq);
            }
        });

        assertTrue(exception.getMessage().contains("Некорректный период аренды"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createBooking_startIsAfterEndReturnsEntityValidationException() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 3, 17, 0);
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(1L).start(today.plusDays(2)).end(today.plusDays(1))
                .build();

        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                        .requestor(originalUser)
                        .created(today.minusDays(1)).build())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(itemRepository.findById(bookingDtoReq.getItemId())).thenReturn(Optional.ofNullable(originalItem));

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.createBooking(userId, bookingDtoReq);
            }
        });

        assertTrue(exception.getMessage().contains("Некорректный период аренды"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDtoReq.getItemId());
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void approveOrRejectBooking_approvedReturnsBookingDtoWhenApproved() {
        Long bookingId = 1L;
        Boolean approved = true;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.WAITING).build();

        Booking outBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        BookingDto expectedBookingDto = BookingDto.builder().id(outBooking.getId()).start(outBooking
                        .getStart()).end(outBooking.getEnd())
                .item(ItemDto.builder().id(outBooking.getItem().getId()).name(outBooking.getItem().getName())
                        .description(outBooking.getItem().getDescription())
                        .available(outBooking.getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBooking.getItem().getOwner().getId())
                                .name(outBooking.getItem().getOwner().getName())
                                .email(outBooking.getItem().getOwner().getEmail()).build())
                        .requestId(outBooking.getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBooking.getBooker().getId())
                        .name(outBooking.getBooker().getName()).email(outBooking.getBooker().getEmail())
                        .build())
                .status(outBooking.getStatus()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));
        Mockito.when(bookingRepository.save(outBooking)).thenReturn(outBooking);

        BookingDto actualBookingDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingDto = bookingService.approveOrRejectBooking(userId, bookingId, approved);
        }

        Assertions.assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void approveOrRejectBooking_rejectedReturnsBookingDtoWhenApproved() {
        Long bookingId = 1L;
        Boolean approved = false;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.WAITING).build();

        Booking outBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.REJECTED).build();

        BookingDto expectedBookingDto = BookingDto.builder().id(outBooking.getId()).start(outBooking
                        .getStart()).end(outBooking.getEnd())
                .item(ItemDto.builder().id(outBooking.getItem().getId()).name(outBooking.getItem().getName())
                        .description(outBooking.getItem().getDescription())
                        .available(outBooking.getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBooking.getItem().getOwner().getId())
                                .name(outBooking.getItem().getOwner().getName())
                                .email(outBooking.getItem().getOwner().getEmail()).build())
                        .requestId(outBooking.getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBooking.getBooker().getId())
                        .name(outBooking.getBooker().getName()).email(outBooking.getBooker().getEmail())
                        .build())
                .status(outBooking.getStatus()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));
        Mockito.when(bookingRepository.save(outBooking)).thenReturn(outBooking);

        BookingDto actualBookingDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingDto = bookingService.approveOrRejectBooking(userId, bookingId, approved);
        }

        Assertions.assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void approveOrRejectBooking_canceledReturnsEntityValidationException() {
        Long bookingId = 1L;
        Boolean approved = false;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.CANCELED).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));

        Exception exception = assertThrows(EntityValidationException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.approveOrRejectBooking(userId, bookingId, approved);
            }
        });

        assertTrue(exception.getMessage().contains("Попытка подтверждения или отклонения запроса находящегося не в " +
                "статусе «ожидает подтверждения»"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveOrRejectBooking_notOwnerReturnsEntityNotFoundException() {
        Long bookingId = 1L;
        Boolean approved = false;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(3L).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.WAITING).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.approveOrRejectBooking(userId, bookingId, approved);
            }
        });

        assertTrue(exception.getMessage().contains("Пользователь не является владельцем вещи"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveOrRejectBooking_userNotExistsReturnsEntityNotFoundException() {
        Long bookingId = 1L;
        Boolean approved = false;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                    Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(today);

                bookingService.approveOrRejectBooking(userId, bookingId, approved);
            }
        });

        assertTrue(exception.getMessage().contains("Попытка подтверждения или отклонения запроса на бронирование " +
                "несуществующим пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getBookingById_ownerRreturnsBookingDtoWhenFound() {
        Long bookingId = 1L;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        BookingDto expectedBookingDto = BookingDto.builder().id(originalBooking.getId()).start(originalBooking
                        .getStart()).end(originalBooking.getEnd())
                .item(ItemDto.builder().id(originalBooking.getItem().getId()).name(originalBooking.getItem().getName())
                        .description(originalBooking.getItem().getDescription())
                        .available(originalBooking.getItem().getAvailable()).owner(UserDto.builder()
                                .id(originalBooking.getItem().getOwner().getId())
                                .name(originalBooking.getItem().getOwner().getName())
                                .email(originalBooking.getItem().getOwner().getEmail()).build())
                        .requestId(originalBooking.getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(originalBooking.getBooker().getId())
                        .name(originalBooking.getBooker().getName()).email(originalBooking.getBooker().getEmail())
                        .build())
                .status(originalBooking.getStatus()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));

        BookingDto actualBookingDto = bookingService.getBookingById(bookingId, userId);

        Assertions.assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getBookingById_bookerRreturnsBookingDtoWhenFound() {
        Long bookingId = 1L;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(userId).name("user").email("user@user.com").build())
                .status(BookingStatus.APPROVED).build();

        BookingDto expectedBookingDto = BookingDto.builder().id(originalBooking.getId()).start(originalBooking
                        .getStart()).end(originalBooking.getEnd())
                .item(ItemDto.builder().id(originalBooking.getItem().getId()).name(originalBooking.getItem().getName())
                        .description(originalBooking.getItem().getDescription())
                        .available(originalBooking.getItem().getAvailable()).owner(UserDto.builder()
                                .id(originalBooking.getItem().getOwner().getId())
                                .name(originalBooking.getItem().getOwner().getName())
                                .email(originalBooking.getItem().getOwner().getEmail()).build())
                        .requestId(originalBooking.getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(originalBooking.getBooker().getId())
                        .name(originalBooking.getBooker().getName()).email(originalBooking.getBooker().getEmail())
                        .build())
                .status(originalBooking.getStatus()).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));

        BookingDto actualBookingDto = bookingService.getBookingById(bookingId, userId);

        Assertions.assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(originalBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getBookingById_notOwnerNotBookerRreturnsBookingDtoWhenFound() {
        Long bookingId = 1L;
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking originalBooking = Booking.builder().id(bookingId).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(3L).name("user3").email("user3@user.com").build())
                .status(BookingStatus.APPROVED).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(originalBooking));


        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingById(bookingId, userId));

        assertTrue(exception.getMessage().contains("Пользователь не является автором бронирования или владельцем " +
                "вещи"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingById_userNotExistsRreturnsBookingDtoWhenFound() {
        Long bookingId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingById(bookingId, userId));

        assertTrue(exception.getMessage().contains("Попытка получения данных о бронировании несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllBookingsByUserId_allRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "ALL";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdOrderByStartDesc(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_currentRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "CURRENT";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, today,
                today)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, today, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_pastRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "PAST";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, today))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_futureRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "FUTURE";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, today))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(userId, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_waitingRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "WAITING";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_rejectedRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "REJECTED";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsByUserId_returnsEntityValidationException() {
        Long userId = 1L;
        String state = "TOP";

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        Exception exception = assertThrows(EntityValidationException.class, () ->
                bookingService.getAllBookingsByUserId(userId, state));

        assertTrue(exception.getMessage().contains(String.format("Unknown state: %s", state)));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllBookingsByUserId_returnsEntityNotFoundException() {
        Long userId = 1L;
        String state = "ALL";

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.getAllBookingsByUserId(userId, state));

        assertTrue(exception.getMessage().contains("Попытка получения данных о бронировании несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_allRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "ALL";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findAllBookingsForAllItemsByUserId(userId)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllBookingsForAllItemsByUserId(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_currentRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "CURRENT";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findCurrentBookingsForAllItemsByUserId(userId, today)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findCurrentBookingsForAllItemsByUserId(userId, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_pastRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "PAST";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findPastBookingsForAllItemsByUserId(userId, today)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findPastBookingsForAllItemsByUserId(userId, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_futureRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "FUTURE";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findFutureBookingsForAllItemsByUserId(userId, today)).thenReturn(outBookings);

        List<BookingDto> actualBookingsDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);
        }

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFutureBookingsForAllItemsByUserId(userId, today);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_waitingRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "WAITING";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findStatusBookingsForAllItemsByUserId(userId, BookingStatus.WAITING))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findStatusBookingsForAllItemsByUserId(userId, BookingStatus.WAITING);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_rejectedRreturnsListBookingDtoWhenFound() {
        Long userId = 1L;
        String state = "REJECTED";
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        Booking outBooking = Booking.builder().id(1L).start(today.plusDays(1)).end(today.plusDays(2))
                .item(Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                        .owner(User.builder().id(userId).name("user").email("user@user.com").build())
                        .request(ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                                .requestor(User.builder().id(2L).name("user").email("user@user.com").build())
                                .created(today.minusDays(1)).build()).build())
                .booker(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.APPROVED).build();

        List<Booking> outBookings = Collections.singletonList(outBooking);

        List<BookingDto> expectedBookingsDto = Collections.singletonList(BookingDto.builder()
                .id(outBookings.get(0).getId()).start(outBookings.get(0).getStart())
                .end(outBookings.get(0).getEnd())
                .item(ItemDto.builder().id(outBookings.get(0).getItem().getId())
                        .name(outBookings.get(0).getItem().getName())
                        .description(outBookings.get(0).getItem().getDescription())
                        .available(outBookings.get(0).getItem().getAvailable()).owner(UserDto.builder()
                                .id(outBookings.get(0).getItem().getOwner().getId())
                                .name(outBookings.get(0).getItem().getOwner().getName())
                                .email(outBookings.get(0).getItem().getOwner().getEmail()).build())
                        .requestId(outBookings.get(0).getItem().getRequest().getId()).build())
                .booker(UserDto.builder().id(outBookings.get(0).getBooker().getId())
                        .name(outBookings.get(0).getBooker().getName())
                        .email(outBookings.get(0).getBooker().getEmail()).build())
                .status(outBookings.get(0).getStatus()).build());

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(bookingRepository.findStatusBookingsForAllItemsByUserId(userId, BookingStatus.REJECTED))
                .thenReturn(outBookings);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);

        Assertions.assertEquals(expectedBookingsDto.get(0).getId(), actualBookingsDto.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findStatusBookingsForAllItemsByUserId(userId, BookingStatus.REJECTED);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(bookingMapper, Mockito.times(1)).toBookingDto(outBooking);
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_returnsEntityValidationException() {
        Long userId = 1L;
        String state = "TOP";

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        Exception exception = assertThrows(EntityValidationException.class, () ->
                bookingService.getAllBookingsForAllItemsByUserId(userId, state));

        assertTrue(exception.getMessage().contains(String.format("Unknown state: %s", state)));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllBookingsForAllItemsByUserId_returnsEntityNotFoundException() {
        Long userId = 1L;
        String state = "ALL";

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.getAllBookingsForAllItemsByUserId(userId, state));

        assertTrue(exception.getMessage().contains("Попытка получения данных о бронировании несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
