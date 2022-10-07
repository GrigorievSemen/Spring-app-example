package ru.grigoriev.repository;

import ru.grigoriev.config.SystemJpaTest;
import ru.grigoriev.entity.Book;
import ru.grigoriev.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить данные юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getAge()).isEqualTo(111);
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getFullName()).isEqualTo("Test Test");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить данные юзера по id. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {
        //Given
        Long id = 1001L;

        //When
        Person result = userRepository.findById(id).get();

        //Then
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getFullName()).isEqualTo("default uer");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера по id и проверить, что связанный с ним книга не удалилась." +
            " Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given
        Long idPerson = 1001L;
        Long idBook = 2002L;

        //When
        userRepository.deleteById(idPerson);
        Optional<Person> result = userRepository.findById(idPerson);
        Optional<Book> book = bookRepository.findById(idBook);

        //Then
        assertThat(result).isEqualTo(Optional.empty());
        assertThat(book).isNotEmpty();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить ошибку при сохранении и обнавлении юзера.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertAndUpdatePerson_thenInvalidDataAccessApiUsageException() {
        //Given
        Person person = null;

        //When
        //Then

        assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userRepository.save(person)
        );
    }

    @DisplayName("Получить ошибку при получении юзера.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenInvalidDataAccessApiUsageException() {
        //Given
        Long id = null;

        //When
        //Then
        assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userRepository.findById(id)
        );
    }

    @DisplayName("Получить ошибку при удалении юзера.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenInvalidDataAccessApiUsageException() {
        //Given
        Long id = null;

        //When
        //Then
        assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userRepository.deleteById(id)
        );
    }
}
