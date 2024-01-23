package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoOwnTest {

    @Autowired
    private JacksonTester<BookingDtoOwn> json;

    @Test
    void bookingDtoOwn() throws Exception {
        BookingDtoOwn bookingDtoOwn = BookingDtoOwn.builder().id(1L).bookerId(1L).build();

        JsonContent<BookingDtoOwn> result = json.write(bookingDtoOwn);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
