package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void bookingDtoOwn() throws Exception {
        BookItemRequestDto bookItemRequestDto = BookItemRequestDto.builder().itemId(1L)
                .start(LocalDateTime.of(2023, 12, 4, 17, 0))
                .end(LocalDateTime.of(2023, 12, 5, 17, 0)).build();

        JsonContent<BookItemRequestDto> result = json.write(bookItemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-04T17:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-05T17:00:00");
    }
}
