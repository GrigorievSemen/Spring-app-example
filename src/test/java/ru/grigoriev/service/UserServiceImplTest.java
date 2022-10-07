package ru.grigoriev.service;

import ru.grigoriev.dto.UserDto;
import ru.grigoriev.entity.Person;
import ru.grigoriev.exception.NotFoundException;
import ru.grigoriev.mapper.UserMapper;
import ru.grigoriev.repository.UserRepository;
import ru.grigoriev.service.impl.UserServiceImpl;
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
import static org.mockito.BDDMockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then
        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setId(1L);
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person updatePerson = new Person();
        updatePerson.setId(1L);
        updatePerson.setFullName("test name");
        updatePerson.setAge(11);
        updatePerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(updatePerson);
        when(userMapper.personToUserDto(updatePerson)).thenReturn(result);

        //then
        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1L, userDtoResult.getId());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getPerson_Test() {
        //given
        Person getPerson = new Person();
        getPerson.setId(1L);
        getPerson.setFullName("test name");
        getPerson.setAge(11);
        getPerson.setTitle("test title");
        Optional<Person> optionalGetPerson = Optional.of(getPerson);

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when
        when(userRepository.findById(1L)).thenReturn(optionalGetPerson);
        when(userMapper.personToUserDto(optionalGetPerson.get())).thenReturn(result);

        //then
        UserDto userDtoResult = userService.getUserById(1L);
        assertEquals(1L, userDtoResult.getId());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deletePerson_Test() {
        //given
        Long id = 1L;
        willDoNothing().given(userRepository).deleteById(id);

        //when
        userService.deleteUserById(id);

        //then
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Попытка создание пользователя с некорректными данными.")
    void savePerson_FailTest() {
        //given
        UserDto userDto_1 = new UserDto();
        Person person_1 = null;

        UserDto userDto_2 = new UserDto();
        userDto_2.setAge(11);
        userDto_2.setTitle("test title");

        Person person_2 = new Person();
        person_2.setAge(11);
        person_2.setTitle("test title");

        //when
        when(userRepository.save(person_1))
                .thenThrow(new IllegalArgumentException());

        when(userMapper.userDtoToPerson(userDto_2)).thenReturn(person_2);
        when(userRepository.save(person_2))
                .thenThrow(new PropertyValueException("not-null property references a null or transient value",
                        "com.edu.ulab.app.entity",
                        "Person.fullName"));

        //then
        assertThatThrownBy(() -> userService.createUser(userDto_1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(null);

        assertThatThrownBy(() -> userService.createUser(userDto_2))
                .isInstanceOf(PropertyValueException.class)
                .hasMessage("not-null property references a null or transient value : com.edu.ulab.app.entity.Person.fullName");
    }

    @Test
    @DisplayName("Попытка получения не существующего пользователя.")
    void getPerson_FailTest() {
        //given
        Long id = 1L;

        //when
        when(userRepository.findById(id))
                .thenThrow(new NotFoundException("User does not exist in the database"));

        //then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User does not exist in the database");
    }

    @Test
    @DisplayName("Попытка удаления не существующего пользователя.")
    void deletePerson_FailTest() {
        //given
        Long id = 1L;

        //when
        doThrow(new NotFoundException("User does not exist in the database"))
                .when(userRepository).deleteById(id);

        //then
        assertThatThrownBy(() -> userService.deleteUserById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User does not exist in the database");
    }
}
