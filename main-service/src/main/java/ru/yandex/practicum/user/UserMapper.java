package ru.yandex.practicum.user;

import jakarta.annotation.Nonnull;

public class UserMapper {

    public static UserModel toModel(@Nonnull UserDto userDto) {
        UserModel user = new UserModel();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static UserDto toDto(@Nonnull UserModel user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(@Nonnull UserModel user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
