package ru.grigoriev.service;


import ru.grigoriev.dto.BookDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface BookService {
    BookDto createBook(@Valid BookDto userDto);

    BookDto updateBook(@Valid BookDto userDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    List<BookDto> getBooksByIdUser(Long userId);
}
