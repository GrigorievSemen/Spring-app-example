package ru.grigoriev.service;

import ru.grigoriev.dto.UserDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface UserService {
    UserDto createUser(@Valid UserDto userDto);

    UserDto updateUser(@Valid UserDto userDto);

    UserDto getUserById(Long userId);

    void deleteUserById(Long userId);
}
