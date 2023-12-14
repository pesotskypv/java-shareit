package ru.practicum.shareit.user.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {

    private Long id;

    private String name;

    private String email;
}