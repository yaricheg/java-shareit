package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "email")
public class User {

    private Integer id;

    private String name;

    private String email;
}
