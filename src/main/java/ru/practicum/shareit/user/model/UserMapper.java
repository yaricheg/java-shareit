package ru.practicum.shareit.user.model;

public class UserMapper {
    public static UserDto toUserDto(UserModel user) {
        return new UserDto(
                user.getName(),
                user.getEmail());
    }
}
