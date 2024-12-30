package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "email")
public class UserModel {

    @NotNull
    private Integer id;

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;
}
