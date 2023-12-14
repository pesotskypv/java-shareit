package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private UserService userService;
    @SpyBean
    private UserMapper userMapper;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findAllUsers() {
        List<UserDto> expectedUsersDto = List.of(
                UserDto.builder().name("user1").email("user1@user.com").build(),
                UserDto.builder().name("user2").email("user2@user.com").build(),
                UserDto.builder().name("user3").email("user3@user.com").build());

        for (UserDto userDto : expectedUsersDto) {
            User user = userMapper.toUser(userDto);
            em.persist(user);
        }
        em.flush();

        List<UserDto> actualUsersDto = userService.findAllUsers();

        for (UserDto userDto : expectedUsersDto) {
            assertThat(actualUsersDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void updateUser() {
        User user = User.builder().name("user").email("user@user.com").build();
        em.persist(user);

        TypedQuery<User> queryUser = em.createQuery("Select u from User u where u.email = :email", User.class);
        User dbUser = queryUser.setParameter("email", user.getEmail()).getSingleResult();

        Long userId = dbUser.getId();
        UserDto userDto = UserDto.builder().id(userId).name("userUpd").email("userUpd@user.com").build();
        UserDto actualUserDto = userService.updateUser(userId, userDto);

        assertThat(actualUserDto.getId(), notNullValue());
        assertThat(actualUserDto.getName(), equalTo(userDto.getName()));
        assertThat(actualUserDto.getEmail(), equalTo(userDto.getEmail()));
    }
}
