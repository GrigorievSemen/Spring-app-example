package ru.grigoriev.service.impl;

import ru.grigoriev.dto.BookDto;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {
    final String INSERT_SQL = "INSERT INTO ulab_edu.book(TITLE, AUTHOR, PAGE_COUNT, id,person_id) VALUES (?,?,?,?,?)";
    final String GET_BY_ID_SQL = "SELECT * FROM ulab_edu.book WHERE id=?";
    final String DELETE_SQL = "DELETE FROM ulab_edu.book WHERE id=?";
    final String GET_BY_ID_USER_SQL = "SELECT * FROM ulab_edu.book WHERE person_id=?";
    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, UUID.randomUUID().getMostSignificantBits());
                    ps.setLong(5, bookDto.getUserId());
                    return ps;
                }, keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("New book added to database successfully: {}", bookDto);

        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        return createBook(bookDto);
    }

    @Override
    public BookDto getBookById(Long bookId) {
        Optional<BookDto> bookDto = Optional.ofNullable(jdbcTemplate.query(GET_BY_ID_SQL, new BeanPropertyRowMapper<>(BookDto.class), bookId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Book does not exist in the database")));
        log.info("Book successfully retrieved from database: {}", bookDto);
        return bookDto.get();
    }

    @Override
    public void deleteBookById(Long bookId) {
        Stream.of(bookId)
                .filter(id -> jdbcTemplate.update(DELETE_SQL, id) > 0)
                .findFirst()
                .ifPresentOrElse(integer -> {
                    log.info("Book successfully deleted from database: {}", bookId);
                }, () -> {
                    throw new NotFoundException("Book does not exist in the database");
                });
    }

    @Override
    public List<BookDto> getBooksByIdUser(Long personId) {
        return jdbcTemplate.query(GET_BY_ID_USER_SQL, new BeanPropertyRowMapper<>(BookDto.class), personId);
    }
}
