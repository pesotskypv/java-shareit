package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService itemService;

    @Test
    void createItem() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();
        User user2 = User.builder().name("user2").email("user2@user.com").build();

        em.persist(user1);
        em.persist(user2);

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

        ItemDtoReq itemDtoReq = ItemDtoReq.builder().name("Вещь").description("Описание вещи").available(true)
                .requestId(dbItemRequest.getId()).build();

        ItemDto actualItemDto = itemService.createItem(dbUser2.getId(), itemDtoReq);

        assertThat(actualItemDto.getId(), notNullValue());
        assertThat(actualItemDto.getName(), equalTo(itemDtoReq.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(itemDtoReq.getDescription()));
        assertThat(actualItemDto.getAvailable(), equalTo(itemDtoReq.getAvailable()));
        assertThat(actualItemDto.getOwner().getId(), notNullValue());
        assertThat(actualItemDto.getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualItemDto.getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualItemDto.getRequestId(), notNullValue());
    }

    @Test
    void findItemsByUser() {
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
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingLast);

        TypedQuery<User> queryUser3 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser3 = queryUser3.setParameter("email", user3.getEmail()).getSingleResult();
        Booking bookingNext = Booking.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).item(dbItem).booker(dbUser3).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingNext);

        Comment comment = Comment.builder().text("Ком").item(item).author(dbUser1).created(LocalDateTime.now()).build();
        em.persist(comment);

        List<ItemDtoOwn> actualOutItemsDtoOwn = itemService.findItemsByUser(dbUser2.getId(), 0, 2);

        assertThat(actualOutItemsDtoOwn.size(), equalTo(1));
        assertThat(actualOutItemsDtoOwn.get(0).getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getName(), equalTo(item.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(actualOutItemsDtoOwn.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getName(), equalTo(user1.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualOutItemsDtoOwn.get(0).getLastBooking().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getLastBooking().getBookerId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getNextBooking().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getNextBooking().getBookerId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getComments().size(), equalTo(1));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getAuthorName(), equalTo(user1.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void updateItem() {
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
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingLast);

        TypedQuery<User> queryUser3 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser3 = queryUser3.setParameter("email", user3.getEmail()).getSingleResult();
        Booking bookingNext = Booking.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).item(dbItem).booker(dbUser3).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingNext);

        Comment comment = Comment.builder().text("Ком").item(item).author(dbUser1).created(LocalDateTime.now()).build();
        em.persist(comment);

        ItemDto itemDto = ItemDto.builder().name("Вещь!").description("Описание").available(false).build();

        ItemDto actualItemDto = itemService.updateItem(dbUser2.getId(), dbItem.getId(), itemDto);

        assertThat(actualItemDto.getId(), notNullValue());
        assertThat(actualItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(actualItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(actualItemDto.getOwner().getId(), notNullValue());
        assertThat(actualItemDto.getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualItemDto.getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualItemDto.getRequestId(), notNullValue());
    }

    @Test
    void addComment() {
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
                .end(LocalDateTime.now().minusDays(1)).item(dbItem).booker(dbUser1).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingLast);

        TypedQuery<User> queryUser3 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser3 = queryUser3.setParameter("email", user3.getEmail()).getSingleResult();
        Booking bookingNext = Booking.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).item(dbItem).booker(dbUser3).status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingNext);

        CommentDto commentDto = CommentDto.builder().text("Ком").build();

        CommentDto actualCommentDto = itemService.addComment(dbUser1.getId(), dbItem.getId(), commentDto);

        assertThat(actualCommentDto.getId(), notNullValue());
        assertThat(actualCommentDto.getText(), equalTo(actualCommentDto.getText()));
        assertThat(actualCommentDto.getAuthorName(), equalTo(user1.getName()));
        assertThat(actualCommentDto.getCreated(), notNullValue());

        List<ItemDtoOwn> actualOutItemsDtoOwn = itemService.findItemsByUser(dbUser2.getId(), 0, 2);

        assertThat(actualOutItemsDtoOwn.size(), equalTo(1));
        assertThat(actualOutItemsDtoOwn.get(0).getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getName(), equalTo(item.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(actualOutItemsDtoOwn.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getName(), equalTo(user2.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getOwner().getEmail(), equalTo(user2.getEmail()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getName(), equalTo(user1.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getRequest().getRequestor().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualOutItemsDtoOwn.get(0).getLastBooking().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getLastBooking().getBookerId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getNextBooking().getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getNextBooking().getBookerId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getComments().size(), equalTo(1));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getId(), notNullValue());
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getText(), equalTo(commentDto.getText()));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getAuthorName(), equalTo(user1.getName()));
        assertThat(actualOutItemsDtoOwn.get(0).getComments().get(0).getCreated(), notNullValue());
    }
}
