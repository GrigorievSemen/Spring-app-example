package ru.grigoriev.service.impl;

import ru.grigoriev.dto.UserDto;
import ru.grigoriev.entity.Person;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.mapper.UserMapper;
import ru.grigoriev.repository.UserRepository;
import ru.grigoriev.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person updateUser = userRepository.save(user);
        log.info("New user updated to database successfully: {}", userDto);
        return userMapper.personToUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<Person> user = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist in the database")));
        log.info("User successfully retrieved from database: {}", user);

        UserDto userDto = userMapper.personToUserDto(user.get());
        log.info("Mapped user: {}", user);
        return userDto;
    }

    @Override
    public void deleteUserById(Long userId) {
        try {
            userRepository.deleteById(userId);
            log.info("User successfully deleted from database: {}", userId);
        } catch (Exception e) {
            throw new NotFoundException("User does not exist in the database");
        }
    }
}
