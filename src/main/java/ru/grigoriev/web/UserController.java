package ru.grigoriev.web;

import ru.grigoriev.facade.UserDataFacade;
import ru.grigoriev.web.constant.WebConstant;
import ru.grigoriev.web.request.UserBookRequest;
import ru.grigoriev.web.response.UserBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import static ru.grigoriev.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static ru.grigoriev.web.constant.WebConstant.RQID;

@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserDataFacade userDataFacade;

    public UserController(UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create user book row.",
            responses = {
                    @ApiResponse(description = "User book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse createUserWithBooks(@RequestBody @Valid UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.createUserWithBooks(request);
        log.info("Response with created user and his books: {}", response);
        return response;
    }

    @PutMapping(value = "/update/{userId}")
    @Operation(summary = "Update a user and her books.",
            responses = {
                    @ApiResponse(description = "User and list of id of his books after update",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse updateUserWithBooks(@PathVariable Long userId, @RequestBody UserBookRequest request) {
        UserBookResponse response = userDataFacade.updateUserWithBooks(userId, request);
        log.info("Response with updated user and his books: {}", response);
        return response;
    }

    @GetMapping(value = "/get/{userId}")
    @Operation(summary = "Getting a user and his books by id.",
            responses = {
                    @ApiResponse(description = "User and list of id of his books",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse getUserWithBooks(@PathVariable Long userId) {
        UserBookResponse response = userDataFacade.getUserWithBooks(userId);
        log.info("Response with user and his books: {}", response);
        return response;
    }

    @DeleteMapping(value = "/delete/{userId}")
    @Operation(summary = "Deleting a user and their books.")
    public void deleteUserWithBooks(@PathVariable Long userId) {
        log.info("Delete user and his books:  userId {}", userId);
        userDataFacade.deleteUserWithBooks(userId);
    }
}