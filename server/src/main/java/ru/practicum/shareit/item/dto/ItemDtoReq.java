package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDtoReq {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
