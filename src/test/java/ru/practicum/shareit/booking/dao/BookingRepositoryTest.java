package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Transactional
    void findAllBookingsForAllItemsByUserId_shouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.APPROVED).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.plusDays(2))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.APPROVED).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.plusDays(3))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.APPROVED).build());

        List<Booking> actualBookings = bookingRepository.findAllBookingsForAllItemsByUserId(user1.getId());

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking3, expectedBooking2, expectedBooking1);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 3);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
        assertThat(actualBookings.get(2)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(2));
    }

    @Test
    @Transactional
    void findCurrentBookingsForAllItemsByUserId_shouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.APPROVED).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.minusDays(2))
                .end(today.plusDays(1))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.APPROVED).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.minusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.APPROVED).build());

        List<Booking> actualBookings = bookingRepository.findCurrentBookingsForAllItemsByUserId(user1.getId(), today);

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking3, expectedBooking2);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 2);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
    }

    @Test
    @Transactional
    void findPastBookingsForAllItemsByUserId_shouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.minusDays(3))
                .end(today.minusDays(2))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.APPROVED).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.minusDays(2))
                .end(today.minusDays(1))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.APPROVED).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.APPROVED).build());

        List<Booking> actualBookings = bookingRepository.findPastBookingsForAllItemsByUserId(user1.getId(), today);

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking2, expectedBooking1);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 2);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
    }

    @Test
    @Transactional
    void findFutureBookingsForAllItemsByUserId_shouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.plusDays(2))
                .end(today.plusDays(3))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.APPROVED).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.minusDays(2))
                .end(today.minusDays(1))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.APPROVED).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.APPROVED).build());

        List<Booking> actualBookings = bookingRepository.findFutureBookingsForAllItemsByUserId(user1.getId(), today);

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking1, expectedBooking3);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 2);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
    }

    @Test
    @Transactional
    void findStatusBookingsForAllItemsByUserId_waitingShouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.plusDays(3))
                .end(today.plusDays(4))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.WAITING).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.plusDays(2))
                .end(today.plusDays(3))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.WAITING).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.APPROVED).build());

        List<Booking> actualBookings = bookingRepository.findStatusBookingsForAllItemsByUserId(user1.getId(),
                BookingStatus.WAITING);

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking1, expectedBooking2);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 2);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
    }

    @Test
    @Transactional
    void findStatusBookingsForAllItemsByUserId_rejectedShouldReturnListBooking() {
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        User user1 = userRepository.save(User.builder().name("user1").email("user1@user.com").build());
        User user2 = userRepository.save(User.builder().name("user2").email("user2@user.com").build());
        User user3 = userRepository.save(User.builder().name("user3").email("user3@user.com").build());
        User user4 = userRepository.save(User.builder().name("user4").email("user4@user.com").build());

        Booking actualBooking1 = bookingRepository.save(Booking.builder().start(today.plusDays(3))
                .end(today.plusDays(4))
                .item(itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Дрель")
                                .requestor(user2).created(today.minusDays(1)).build())).build()))
                .booker(user2).status(BookingStatus.WAITING).build());

        Booking actualBooking2 = bookingRepository.save(Booking.builder().start(today.plusDays(2))
                .end(today.plusDays(3))
                .item(itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                        .owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужен Перфоратор")
                                .requestor(user3).created(today.minusDays(1)).build())).build()))
                .booker(user3).status(BookingStatus.REJECTED).build());

        Booking actualBooking3 = bookingRepository.save(Booking.builder().start(today.plusDays(1))
                .end(today.plusDays(2))
                .item(itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                        .available(true).owner(user1)
                        .request(itemRequestRepository.save(ItemRequest.builder().description("Нужна Отвертка")
                                .requestor(user4).created(today.minusDays(1)).build())).build()))
                .booker(user4).status(BookingStatus.REJECTED).build());

        List<Booking> actualBookings = bookingRepository.findStatusBookingsForAllItemsByUserId(user1.getId(),
                BookingStatus.REJECTED);

        Booking expectedBooking1 = Booking.builder().start(actualBooking1.getStart()).end(actualBooking1.getEnd())
                .item(Item.builder().name(actualBooking1.getItem().getName())
                        .description(actualBooking1.getItem().getDescription())
                        .available(actualBooking1.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking1.getItem().getOwner().getName())
                                .email(actualBooking1.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking1.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking1.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking1.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking1.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking1.getBooker().getName())
                        .email(actualBooking1.getBooker().getEmail()).build())
                .status(actualBooking1.getStatus()).build();

        Booking expectedBooking2 = Booking.builder().start(actualBooking2.getStart()).end(actualBooking2.getEnd())
                .item(Item.builder().name(actualBooking2.getItem().getName())
                        .description(actualBooking2.getItem().getDescription())
                        .available(actualBooking2.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking2.getItem().getOwner().getName())
                                .email(actualBooking2.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking2.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking2.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking2.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking2.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking2.getBooker().getName())
                        .email(actualBooking2.getBooker().getEmail()).build())
                .status(actualBooking2.getStatus()).build();

        Booking expectedBooking3 = Booking.builder().start(actualBooking3.getStart()).end(actualBooking3
                        .getEnd())
                .item(Item.builder().name(actualBooking3.getItem().getName())
                        .description(actualBooking3.getItem().getDescription())
                        .available(actualBooking3.getItem().getAvailable())
                        .owner(User.builder().name(actualBooking3.getItem().getOwner().getName())
                                .email(actualBooking3.getItem().getOwner().getEmail()).build())
                        .request(ItemRequest.builder()
                                .description(actualBooking3.getItem().getRequest().getDescription())
                                .requestor(User.builder()
                                        .name(actualBooking3.getItem().getRequest().getRequestor().getName())
                                        .email(actualBooking3.getItem().getRequest().getRequestor().getEmail())
                                        .build())
                                .created(actualBooking3.getItem().getRequest().getCreated()).build()).build())
                .booker(User.builder().name(actualBooking3.getBooker().getName())
                        .email(actualBooking3.getBooker().getEmail()).build())
                .status(actualBooking3.getStatus()).build();

        List<Booking> expectedBookings = List.of(expectedBooking2, expectedBooking3);

        Assertions.assertFalse(actualBookings.isEmpty());
        Assertions.assertEquals(actualBookings.size(), 2);
        Assertions.assertNotNull(actualBookings.get(0).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(1).getId());
        Assertions.assertNotNull(actualBookings.get(0).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(1).getBooker().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getOwner().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getId());
        Assertions.assertNotNull(actualBookings.get(0).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        Assertions.assertNotNull(actualBookings.get(1).getItem().getRequest().getRequestor().getId());
        assertThat(actualBookings.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(0));
        assertThat(actualBookings.get(1)).usingRecursiveComparison()
                .ignoringFields("id", "booker.id", "item.id", "item.owner.id", "item.request.id",
                        "item.request.requestor.id").isEqualTo(expectedBookings.get(1));
    }
}
