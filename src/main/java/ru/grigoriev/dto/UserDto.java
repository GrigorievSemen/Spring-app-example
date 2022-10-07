package ru.grigoriev.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private Long id;

    @NotEmpty(message = "FullName should not be empty")
    @Size(min = 2, max = 50, message = "FullName should be between 2 and 50 characters")
    private String fullName;

    @NotEmpty(message = "Title should not be empty")
    @Size(min = 2, max = 30, message = "Title should be between 2 and 30 characters")
    private String title;

    @Min(value = 7, message = "Age should be over six")
    private int age;
}
