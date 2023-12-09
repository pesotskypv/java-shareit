package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDtoOwnReq {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
