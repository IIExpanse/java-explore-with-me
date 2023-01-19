package ru.yandex.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.exception.DuplicateEmailException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserServiceTest {

    private UserService service;

    @Test
    public void addUserTest() {
        UserDto userDto = makeDefaultUser();
        UserDto newUser = service.addUser(userDto);
        userDto.setId(1L);

        assertEquals(userDto, newUser);
    }

    @Test
    public void shouldThrowExceptionForAddingDuplicateEmail() {
        service.addUser(makeDefaultUser());

        assertThrows(DuplicateEmailException.class, () -> service.addUser(makeDefaultUser()));
    }

    @Test
    public void getUsersTest() {
        UserDto user1 = service.addUser(makeDefaultUser());
        UserDto user2 = makeDefaultUser();
        user2.setEmail("new@mail.ru");
        user2 = service.addUser(user2);

        assertEquals(
                service.getUsers(List.of(user1.getId(), user2.getId()), 0, 10),
                List.of(user1, user2));
    }

    @Test
    public void deleteUserTest() {
        UserDto userDto = service.addUser(makeDefaultUser());
        service.deleteUser(userDto.getId());

        assertTrue(service.getUsers(List.of(userDto.getId()), 0, 10).isEmpty());
    }

    private UserDto makeDefaultUser() {
        return UserDto.builder()
                .email("some@mail.ru")
                .name("John")
                .build();
    }


}
