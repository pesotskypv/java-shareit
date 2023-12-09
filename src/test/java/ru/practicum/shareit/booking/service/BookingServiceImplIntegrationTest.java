package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private BookingService bookingService;

    @Test
    void createBooking() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();
        User user3 = User.builder().name("user3").email("user3@user.com").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();
        ItemRequest itemRequest = ItemRequest.builder().description("Запрос вещи").requestor(dbUser1)
                .created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        TypedQuery<User> queryUser2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser2 = queryUser2.setParameter("email", user2.getEmail()).getSingleResult();

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery("Select ir from ItemRequest ir where " +
                "ir.description = :description", ItemRequest.class);
        ItemRequest dbItemRequest = queryItemRequest.setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item dbItem = queryItem.setParameter("name", item.getName()).getSingleResult();

        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(dbItem.getId())
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();

        BookingDto actualBookingDto = bookingService.createBooking(bookingDtoReq, dbUser1.getId());

        assertThat(actualBookingDto.getId(), notNullValue());
        assertThat(actualBookingDto.getStart(), equalTo(bookingDtoReq.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(bookingDtoReq.getEnd()));
        assertThat(actualBookingDto.getItem().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(actualBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualBookingDto.getItem().getOwner().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualBookingDto.getItem().getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualBookingDto.getItem().getRequestId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getName(), equalTo(user1.getName()));
        assertThat(actualBookingDto.getBooker().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualBookingDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void approveOrRejectBooking() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();
        User user3 = User.builder().name("user3").email("user3@user.com").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();
        ItemRequest itemRequest = ItemRequest.builder().description("Запрос вещи").requestor(dbUser1)
                .created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        TypedQuery<User> queryUser2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser2 = queryUser2.setParameter("email", user2.getEmail()).getSingleResult();

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery("Select ir from ItemRequest ir where " +
                "ir.description = :description", ItemRequest.class);
        ItemRequest dbItemRequest = queryItemRequest.setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item dbItem = queryItem.setParameter("name", item.getName()).getSingleResult();
        Booking bookingLast = Booking.builder().start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.WAITING)
                .build();
        em.persist(bookingLast);

        TypedQuery<Booking> queryBooking = em.createQuery("Select b from Booking b where b.status = :status",
                Booking.class);
        Booking dbBooking = queryBooking.setParameter("status", bookingLast.getStatus()).getSingleResult();

        BookingDto actualBookingDto = bookingService.approveOrRejectBooking(dbBooking.getId(), true,
                user2.getId());

        assertThat(actualBookingDto.getId(), notNullValue());
        assertThat(actualBookingDto.getStart(), equalTo(bookingLast.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(bookingLast.getEnd()));
        assertThat(actualBookingDto.getItem().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(actualBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualBookingDto.getItem().getOwner().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualBookingDto.getItem().getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualBookingDto.getItem().getRequestId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getName(), equalTo(user1.getName()));
        assertThat(actualBookingDto.getBooker().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();
        User user3 = User.builder().name("user3").email("user3@user.com").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();
        ItemRequest itemRequest = ItemRequest.builder().description("Запрос вещи").requestor(dbUser1)
                .created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        TypedQuery<User> queryUser2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser2 = queryUser2.setParameter("email", user2.getEmail()).getSingleResult();

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery("Select ir from ItemRequest ir where " +
                "ir.description = :description", ItemRequest.class);
        ItemRequest dbItemRequest = queryItemRequest.setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item dbItem = queryItem.setParameter("name", item.getName()).getSingleResult();
        Booking bookingLast = Booking.builder().start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.WAITING)
                .build();
        em.persist(bookingLast);

        TypedQuery<Booking> queryBooking = em.createQuery("Select b from Booking b where b.status = :status",
                Booking.class);
        Booking dbBooking = queryBooking.setParameter("status", bookingLast.getStatus()).getSingleResult();

        BookingDto actualBookingDto = bookingService.getBookingById(dbBooking.getId(), user2.getId());

        assertThat(actualBookingDto.getId(), notNullValue());
        assertThat(actualBookingDto.getStart(), equalTo(bookingLast.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(bookingLast.getEnd()));
        assertThat(actualBookingDto.getItem().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(actualBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualBookingDto.getItem().getOwner().getId(), notNullValue());
        assertThat(actualBookingDto.getItem().getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualBookingDto.getItem().getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualBookingDto.getItem().getRequestId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getId(), notNullValue());
        assertThat(actualBookingDto.getBooker().getName(), equalTo(user1.getName()));
        assertThat(actualBookingDto.getBooker().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualBookingDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getAllBookingsByUserId() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();
        User user3 = User.builder().name("user3").email("user3@user.com").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();
        ItemRequest itemRequest = ItemRequest.builder().description("Запрос вещи").requestor(dbUser1)
                .created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        TypedQuery<User> queryUser2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser2 = queryUser2.setParameter("email", user2.getEmail()).getSingleResult();

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery("Select ir from ItemRequest ir where " +
                "ir.description = :description", ItemRequest.class);
        ItemRequest dbItemRequest = queryItemRequest.setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item dbItem = queryItem.setParameter("name", item.getName()).getSingleResult();
        Booking bookingLast = Booking.builder().start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.WAITING)
                .build();
        em.persist(bookingLast);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsByUserId("ALL", user1.getId());

        assertThat(actualBookingsDto.get(0).getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getStart(), equalTo(bookingLast.getStart()));
        assertThat(actualBookingsDto.get(0).getEnd(), equalTo(bookingLast.getEnd()));
        assertThat(actualBookingsDto.get(0).getItem().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(actualBookingsDto.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualBookingsDto.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualBookingsDto.get(0).getItem().getRequestId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getBooker().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getBooker().getName(), equalTo(user1.getName()));
        assertThat(actualBookingsDto.get(0).getBooker().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualBookingsDto.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getAllBookingsForAllItemsByUserId() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();
        User user3 = User.builder().name("user3").email("user3@user.com").build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();
        ItemRequest itemRequest = ItemRequest.builder().description("Запрос вещи").requestor(dbUser1)
                .created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        TypedQuery<User> queryUser2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser2 = queryUser2.setParameter("email", user2.getEmail()).getSingleResult();

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery("Select ir from ItemRequest ir where " +
                "ir.description = :description", ItemRequest.class);
        ItemRequest dbItemRequest = queryItemRequest.setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item dbItem = queryItem.setParameter("name", item.getName()).getSingleResult();
        Booking bookingLast = Booking.builder().start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.WAITING)
                .build();
        em.persist(bookingLast);

        List<BookingDto> actualBookingsDto = bookingService.getAllBookingsForAllItemsByUserId("ALL",
                user2.getId());

        assertThat(actualBookingsDto.get(0).getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getStart(), equalTo(bookingLast.getStart()));
        assertThat(actualBookingsDto.get(0).getEnd(), equalTo(bookingLast.getEnd()));
        assertThat(actualBookingsDto.get(0).getItem().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(actualBookingsDto.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualBookingsDto.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualBookingsDto.get(0).getItem().getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualBookingsDto.get(0).getItem().getRequestId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getBooker().getId(), notNullValue());
        assertThat(actualBookingsDto.get(0).getBooker().getName(), equalTo(user1.getName()));
        assertThat(actualBookingsDto.get(0).getBooker().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualBookingsDto.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }
}
