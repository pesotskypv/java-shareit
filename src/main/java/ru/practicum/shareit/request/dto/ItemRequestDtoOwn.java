package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoOwn {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDtoOwnReq> items;
}
