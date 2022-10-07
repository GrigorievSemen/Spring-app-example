package ru.grigoriev.service.impl;

import ru.grigoriev.dto.UserDto;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    final String INSERT_SQL = "INSERT INTO ulab_edu.person(id, full_name, title, age) VALUES (?,?,?,?)";
    final String UPDATE_SQL = "UPDATE ulab_edu.person SET full_name=?,title=?,age=? WHERE id =?";
    final String GET_SQL = "SELECT * FROM ulab_edu.person WHERE id=?";
    final String DELETE_SQL = "DELETE FROM ulab_edu.person WHERE id=?";
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setLong(1, UUID.randomUUID().getMostSignificantBits());
                    ps.setString(2, userDto.getFullName());
                    ps.setString(3, userDto.getTitle());
                    ps.setLong(4, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("New user added to database successfully: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        jdbcTemplate.update(UPDATE_SQL,
                userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userDto.getId());
        log.info("New user updated to database successfully: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<UserDto> userDto = Optional.ofNullable(jdbcTemplate.query(GET_SQL, new BeanPropertyRowMapper<>(UserDto.class), userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User does not exist in the database")));
        log.info("User successfully retrieved from database: {}", userDto);
        return userDto.get();
    }

    @Override
    public void deleteUserById(Long userId) {
        Stream.of(userId)
                .filter(id -> jdbcTemplate.update(DELETE_SQL, id) > 0)
                .findFirst()
                .ifPresentOrElse(integer -> {
                    log.info("User successfully deleted from database: {}", userId);
                }, () -> {
                    throw new NotFoundException("User does not exist in the database");
                });
    }
}
