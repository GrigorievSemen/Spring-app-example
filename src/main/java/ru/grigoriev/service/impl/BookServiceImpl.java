package ru.grigoriev.service.impl;

import ru.grigoriev.dto.BookDto;
import ru.grigoriev.entity.Book;
import ru.grigoriev.entity.Person;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.mapper.BookMapper;
import ru.grigoriev.repository.BookRepository;
import ru.grigoriev.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);

        Person person = new Person();
        person.setId(bookDto.getUserId());
        book.setPerson(person);

        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        return createBook(bookDto);
    }

    @Override
    public BookDto getBookById(Long bookId) {
        Optional<Book> bookOptional = Optional.of(bookRepository.findById(bookId))
                .orElseThrow(() -> new NotFoundException("Book does not exist in the database"));
        log.info("Book successfully retrieved from database: {}", bookOptional);
        BookDto bookDto = bookMapper.bookToBookDto(bookOptional.get());
        log.info("Mapped book: {}", bookDto);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long bookId) {
        try {
            bookRepository.deleteById(bookId);
            log.info("Book successfully deleted from database: {}", bookId);
        } catch (Exception e) {
            throw new NotFoundException("Book does not exist in the database");
        }
    }

    @Override
    public List<BookDto> getBooksByIdUser(Long personId) {
        return bookRepository.findAllByPersonId(personId)
                .orElse(new ArrayList<>())
                .stream()
                .map(bookMapper::bookToBookDto)
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .toList();
    }
}
