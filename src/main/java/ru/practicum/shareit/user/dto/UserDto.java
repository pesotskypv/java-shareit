package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto implements Cloneable {

    private Long id;

    @NotBlank(groups = {CreateUserValidation.class})
    private String name;

    @NotBlank(groups = {CreateUserValidation.class})
    @Email(groups = {CreateUserValidation.class, UpdateUserValidation.class},
            message = "Электронная почта указана неверно")
    private String email;
}