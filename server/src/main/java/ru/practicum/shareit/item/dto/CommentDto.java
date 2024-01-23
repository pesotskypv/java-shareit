package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
