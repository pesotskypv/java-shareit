package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void BookingDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1L)
                .start(LocalDateTime.of(2023, 12, 4, 17, 0))
                .end(LocalDateTime.of(2023, 12, 5, 17, 0))
                .item(ItemDto.builder().id(1L).name("Вещь").description("Описание вещи").available(true)
                        .owner(UserDto.builder().id(1L).name("user").email("user@user.com").build())
                        .requestId(1L).build())
                .booker(UserDto.builder().id(2L).name("user2").email("user2@user.com").build())
                .status(BookingStatus.WAITING).build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-04T17:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-05T17:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Вещь");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email")
                .isEqualTo("user@user.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("user2@user.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
