package ru.grigoriev.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(exclude = {"id", "userId"})
public class BookDto {
    private Long id;
    private Long userId;

    @NotEmpty(message = "Title should not be empty")
    @Size(min = 2, max = 70, message = "Title should be between 2 and 70 characters")
    private String title;

    @NotEmpty(message = "Author should not be empty")
    @Size(min = 2, max = 30, message = "Author should be between 2 and 30 characters")
    private String author;

    @Min(value = 1, message = "PageCount count should be over zero")
    private long pageCount;
}
