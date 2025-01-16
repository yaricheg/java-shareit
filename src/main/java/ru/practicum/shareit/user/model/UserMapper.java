package ru.practicum.shareit.user.model;

public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
