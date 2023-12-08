package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOwn;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoOwnTest {

    @Autowired
    private JacksonTester<ItemDtoOwn> json;

    @Test
    void UserDto() throws Exception {
        ItemDtoOwn itemDtoOwn = ItemDtoOwn.builder().id(1L).name("Вещь").description("Описание вещи").available(true)
                .owner(UserDto.builder().id(1L).name("user").email("user@user.com").build())
                .request(ItemRequestDto.builder().id(1L).description("Запрос вещи")
                        .requestor(UserDto.builder().id(2L).name("user2").email("user2@user.com").build())
                        .created(LocalDateTime.of(2023, 12, 4, 17, 0)).build())
                .lastBooking(BookingDtoOwn.builder().id(1L).bookerId(2L).build())
                .nextBooking(BookingDtoOwn.builder().id(2L).bookerId(3L).build())
                .comments(Collections.singletonList(CommentDto.builder().id(1L).text("Коммент").authorName("user2")
                        .created(LocalDateTime.of(2023, 12, 7, 17, 0)).build()))
                .build();

        JsonContent<ItemDtoOwn> result = json.write(itemDtoOwn);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("user@user.com");
        assertThat(result).extractingJsonPathNumberValue("$.request.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.request.description")
                .isEqualTo("Запрос вещи");
        assertThat(result).extractingJsonPathNumberValue("$.request.requestor.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.request.requestor.name")
                .isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.request.requestor.email")
                .isEqualTo("user2@user.com");
        assertThat(result).extractingJsonPathStringValue("$.request.created")
                .isEqualTo("2023-12-04T17:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Коммент");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2023-12-07T17:00:00");
    }
}
