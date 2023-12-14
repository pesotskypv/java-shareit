package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Transactional
    void findItemsByText_shouldReturnListItem() {
        User user = User.builder().name("user").email("user@user.com").build();
        List<Item> expectedItems = List.of(Item.builder().name("Дрель").description("Электроинструмент")
                        .available(true).owner(user).build(),
                Item.builder().name("Перфоратор").description("Эектродрель").available(true).owner(user)
                        .build());

        User savedUser = userRepository.save(User.builder().name("user").email("user@user.com").build());

        itemRepository.save(Item.builder().name("Дрель").description("Электроинструмент").available(true)
                .owner(savedUser).build());
        itemRepository.save(Item.builder().name("Перфоратор").description("Эектродрель").available(true)
                .owner(savedUser).build());
        itemRepository.save(Item.builder().name("Отвертка").description("Аккумуляторная отвертка").available(true)
                .owner(savedUser).build());

        List<Item> actualItems = itemRepository.findItemsByText("дрель");

        Assertions.assertFalse(actualItems.isEmpty());
        Assertions.assertEquals(actualItems.size(), 2);
        Assertions.assertNotNull(actualItems.get(0).getId());
        Assertions.assertNotNull(actualItems.get(1).getId());
        assertThat(expectedItems.get(0)).usingRecursiveComparison().ignoringFields("id", "owner.id")
                .isEqualTo(actualItems.get(0));
        assertThat(expectedItems.get(1)).usingRecursiveComparison().ignoringFields("id", "owner.id")
                .isEqualTo(actualItems.get(1));
    }
}
