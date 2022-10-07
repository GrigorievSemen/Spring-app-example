package ru.grigoriev.mapper;

import ru.grigoriev.dto.BookDto;
import ru.grigoriev.entity.Book;
import ru.grigoriev.web.request.BookRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    Book bookDtoToBook(BookDto bookDto);

    BookDto bookToBookDto(Book book);
}
