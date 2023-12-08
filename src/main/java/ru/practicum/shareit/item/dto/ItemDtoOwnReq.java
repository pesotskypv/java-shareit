package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOwnReq {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
