package ru.grigoriev.facade;

import ru.grigoriev.dto.BookDto;
import ru.grigoriev.dto.UserDto;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.mapper.BookMapper;
import ru.grigoriev.mapper.UserMapper;
import ru.grigoriev.service.impl.BookServiceImpl;
import ru.grigoriev.service.impl.UserServiceImpl;
import ru.grigoriev.web.request.BookRequest;
import ru.grigoriev.web.request.UserBookRequest;
import ru.grigoriev.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Component
public class UserDataFacade {
    //    private final UserServiceImplTemplate userService;
//    private final BookServiceImplTemplate bookService;
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    //   public UserDataFacade(UserServiceImplTemplate userService,
//                          BookServiceImplTemplate bookService,
    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);

        UserDto userDto = validUserBookRequestForUser(userBookRequest);
        log.info("Mapped user request: {}", userDto);
        List<BookRequest> bookListRequest = validUserBookRequestForBookRequest(userBookRequest);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);
        List<Long> bookIdList = bookListRequest.stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();

        log.info("Collected books ids: {}", bookIdList);

        return createUserBookResponse(createdUser.getId(), bookIdList);
    }

    public UserBookResponse updateUserWithBooks(Long userId, UserBookRequest userBookRequest) {
        log.info("Received a request to update a user or books: {}", userBookRequest);

        UserDto userDto = validUserBookRequestForUser(userBookRequest);
        UserDto userDtoFromDateBase = userService.getUserById(userId);

        List<BookRequest> bookListRequest = validUserBookRequestForBookRequest(userBookRequest);
        List<BookDto> userBooksIdFromDateBase = bookService.getBooksByIdUser(userId);
        log.info("The user's books successfully retrieved from the database: {}", userBooksIdFromDateBase);

        userDtoFromDateBase = userMapper.userDtoToUpdateToUserDto(userDto);
        log.info("Mapped user (updating fields): {}", userDtoFromDateBase);
        userDtoFromDateBase.setId(userId);

        UserDto finalUserDtoFromDateBase = userDtoFromDateBase;
        List<Long> bookIdList = bookListRequest
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .filter(bookDto -> !userBooksIdFromDateBase.contains(bookDto))
                .peek(bookDto -> bookDto.setUserId(userId))
                .map(bookService::updateBook)
                .peek(updateBook -> log.info("The book has been successfully added to user: {}", updateBook))
                .map(BookDto::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        userBooksIdFromDateBase.forEach(bookDto -> bookIdList.add(bookDto.getId()));
        userService.updateUser(userDtoFromDateBase);
        return createUserBookResponse(userId, bookIdList);
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        UserDto userDtoFromDateBase = userService.getUserById(userId);

        List<BookDto> userBooksIdFromDateBase = bookService.getBooksByIdUser(userId);
        List<Long> bookIdList = userBooksIdFromDateBase
                .stream()
                .map(BookDto::getId)
                .toList();
        return createUserBookResponse(userId, bookIdList);
    }


    public void deleteUserWithBooks(Long userId) {
        userService.deleteUserById(userId);
        log.info("User successfully deleted from database: {}", userId);
        List<BookDto> userBooksIdFromDateBase = bookService.getBooksByIdUser(userId);

        userBooksIdFromDateBase.forEach(bookDto -> {
            bookService.deleteBookById(bookDto.getId());
            log.info("Book successfully deleted from database: {}", bookDto.getId());
        });
    }

    private UserDto validUserBookRequestForUser(UserBookRequest userBookRequest) {
        return Optional.ofNullable(userMapper.userRequestToUserDto(userBookRequest.getUserRequest()))
                .orElseThrow(() -> new NotFoundException("Invalid request, missing user data!"));
    }

    private List<BookRequest> validUserBookRequestForBookRequest(UserBookRequest userBookRequest) {
        return Optional.ofNullable(userBookRequest.getBookRequests())
                .orElseThrow(() -> new NotFoundException("The request is invalid, the list of books is missing"));
    }

    private UserBookResponse createUserBookResponse(Long id, List<Long> list) {
        return UserBookResponse.builder()
                .userId(id)
                .booksIdList(list)
                .build();
    }
}
