package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoReqTest {

    @Autowired
    private JacksonTester<ItemDtoReq> json;

    @Test
    void itemDtoReqTest() throws Exception {
        ItemDtoReq itemDtoReq = ItemDtoReq.builder().name("Вещь").description("Описание вещи").available(true)
                .requestId(1L).build();

        JsonContent<ItemDtoReq> result = json.write(itemDtoReq);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
