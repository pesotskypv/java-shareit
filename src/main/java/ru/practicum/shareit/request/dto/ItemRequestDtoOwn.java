package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDtoOwn {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDtoOwnReq> items;
}
