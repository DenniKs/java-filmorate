package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTests {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Test
    public void testValidUser() {
        User user = new User();
        user.setEmail("valid.email@example.com");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserWithEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("Электронная почта не может быть пустой", violation.getMessage());
    }

    @Test
    public void testUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalidemail");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("Электронная почта должна быть валидной и содержать символ @", violation.getMessage());
    }

    @Test
    public void testUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("valid.email@example.com");
        user.setLogin("invalid login");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("login", violation.getPropertyPath().toString());
        assertEquals("Логин не должен содержать пробелы", violation.getMessage());
    }

    @Test
    public void testUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("valid.email@example.com");
        user.setLogin("");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());

        boolean hasEmptyLoginViolation = violations.stream()
                .anyMatch(violation ->
                        "login".equals(violation.getPropertyPath().toString()) &&
                                "Логин не может быть пустым".equals(violation.getMessage())
                );

        boolean hasLoginWithSpacesViolation = violations.stream()
                .anyMatch(violation ->
                        "login".equals(violation.getPropertyPath().toString()) &&
                                "Логин не должен содержать пробелы".equals(violation.getMessage())
                );

        assertTrue(hasEmptyLoginViolation);
        assertTrue(hasLoginWithSpacesViolation);
    }

    @Test
    public void testUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("valid.email@example.com");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("birthday", violation.getPropertyPath().toString());
    }
}
