package ru.grigoriev.service;

import ru.grigoriev.dto.BookDto;
import ru.grigoriev.entity.Book;
import ru.grigoriev.entity.Person;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.mapper.BookMapper;
import ru.grigoriev.repository.BookRepository;
import ru.grigoriev.service.impl.BookServiceImpl;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book updateBook = new Book();
        updateBook.setId(1L);
        updateBook.setPageCount(1000);
        updateBook.setTitle("test title");
        updateBook.setAuthor("test author");
        updateBook.setPerson(person);

        //when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updateBook);
        when(bookMapper.bookToBookDto(updateBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1000, bookDtoResult.getPageCount());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1L, bookDtoResult.getUserId());
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        Book getBook = new Book();
        getBook.setId(1L);
        getBook.setPageCount(1000);
        getBook.setTitle("test title");
        getBook.setAuthor("test author");
        getBook.setPerson(person);
        Optional<Book> optionalGetBook = Optional.of(getBook);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        //when
        when(bookRepository.findById(1L)).thenReturn(optionalGetBook);
        when(bookMapper.bookToBookDto(getBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1000, bookDtoResult.getPageCount());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1L, bookDtoResult.getUserId());
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBook_Test() {
        //given
        Long id = 1L;
        willDoNothing().given(bookRepository).deleteById(id);

        //when
        bookService.deleteBookById(id);

        //then
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Попытка создание книги с некорректными данными.")
    void saveBook_FailTest() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        Book book = new Book();
        book.setPerson(person);
        book.setId(1L);
        book.setTitle("test title");
        book.setPageCount(1000);

        //when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book))
                .thenThrow(new PropertyValueException("not-null property references a null or transient value",
                        "com.edu.ulab.app.entity",
                        "Book.author"));

        //then
        assertThatThrownBy(() -> bookService.createBook(bookDto))
                .isInstanceOf(PropertyValueException.class)
                .hasMessage("not-null property references a null or transient value : com.edu.ulab.app.entity.Book.author");
    }

    @Test
    @DisplayName("Попытка получения не существующей книги.")
    void getBook_FailTest() {
        //given
        Long id = 1L;

        //when
        when(bookRepository.findById(id))
                .thenThrow(new NotFoundException("Book does not exist in the database"));

        //then
        assertThatThrownBy(() -> bookService.getBookById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book does not exist in the database");
    }

    @Test
    @DisplayName("Попытка удаления не существующей книги.")
    void deleteBook_FailTest() {
        //given
        Long id = 1L;

        //when
        doThrow(new NotFoundException("User does not exist in the database"))
                .when(bookRepository).deleteById(id);
        //then
        assertThatThrownBy(() -> bookService.deleteBookById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book does not exist in the database");
    }
}
