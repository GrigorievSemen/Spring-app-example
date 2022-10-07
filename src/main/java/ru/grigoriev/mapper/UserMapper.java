package ru.grigoriev.mapper;

import ru.grigoriev.dto.UserDto;
import ru.grigoriev.entity.Person;
import ru.grigoriev.web.request.UserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userRequestToUserDto(UserRequest userRequest);

    UserRequest userDtoToUserRequest(UserDto userDto);

    Person userDtoToPerson(UserDto userDto);

    UserDto personToUserDto(Person person);

    UserDto userDtoToUpdateToUserDto(UserDto userDto);
}
