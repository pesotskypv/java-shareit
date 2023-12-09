package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
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
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void addRequest() {
        User user1 = User.builder().name("user1").email("user1@user.com").build();

        em.persist(user1);

        TypedQuery<User> queryUser1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser1 = queryUser1.setParameter("email", user1.getEmail()).getSingleResult();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Запрос вещи").build();

        ItemRequestDto actualItemRequestDto = itemRequestService.addRequest(dbUser1.getId(), itemRequestDto);

        assertThat(actualItemRequestDto.getId(), notNullValue());
        assertThat(actualItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(actualItemRequestDto.getRequestor().getId(), notNullValue());
        assertThat(actualItemRequestDto.getRequestor().getName(), equalTo(user1.getName()));
        assertThat(actualItemRequestDto.getRequestor().getEmail(), equalTo(user1.getEmail()));
        assertThat(actualItemRequestDto.getCreated(), notNullValue());
    }

    @Test
    void findItemRequestsByUser() {
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

        Item item = Item.builder().name("Вещь").description("Описание вещи").available(true).owner(dbUser2)
                .request(dbItemRequest).build();
        em.persist(item);

        List<ItemRequestDtoOwn> actualItemRequestsDto = itemRequestService.findItemRequestsByUser(dbUser1.getId());

        assertThat(actualItemRequestsDto.size(), equalTo(1));
        assertThat(actualItemRequestsDto.get(0).getId(), notNullValue());
        assertThat(actualItemRequestsDto.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualItemRequestsDto.get(0).getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(actualItemRequestsDto.get(0).getItems().size(), equalTo(1));
        assertThat(actualItemRequestsDto.get(0).getItems().get(0).getId(), notNullValue());
        assertThat(actualItemRequestsDto.get(0).getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(actualItemRequestsDto.get(0).getItems().get(0).getDescription(),
                equalTo(item.getDescription()));
        assertThat(actualItemRequestsDto.get(0).getItems().get(0).getAvailable(),
                equalTo(item.getAvailable()));
        assertThat(actualItemRequestsDto.get(0).getItems().get(0).getRequestId(), notNullValue());
    }
}
