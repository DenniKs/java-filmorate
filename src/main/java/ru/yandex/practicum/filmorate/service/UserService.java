package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.StatusRelation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        validate(user);
        check(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        return userStorage.update(user);
    }

    public Collection<User> findAll() {
        log.info("Список пользователей отправлен");
        return userStorage.findAll();
    }

    public User getById(int id) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        log.info("Пользователь с id: '{}' отправлен", id);
        return user;
    }

    public User deleteById(int id) {
        User user = userStorage.deleteById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден. Невозможно удалить неизвестного пользователя"));
        log.info("Пользователь с id: '{}' удален", id);
        return user;
    }

    public List<User> addFriendship(int firstId, int secondId) {
        User firstUser = getById(firstId);
        User secondUser = getById(secondId);
        if (firstUser.getFriendshipStatus(secondId) == StatusRelation.CONFIRMED_REQUEST) {
            throw new InternalException("Пользователи уже являются друзьями");
        }
        firstUser.addOrUpdateFriend(secondId, StatusRelation.SENT_REQUEST);
        secondUser.addOrUpdateFriend(firstId, StatusRelation.SENT_REQUEST);

        log.info("Запрос на добавление в друзья отправлен от '{}' к '{}'",
                firstUser.getName(), secondUser.getName());

        return Arrays.asList(firstUser, secondUser);
    }

    public List<User> removeFriendship(int firstId, int secondId) {
        User firstUser = getById(firstId);
        User secondUser = getById(secondId);
        if (firstUser.getFriendshipStatus(secondId) == null) {
            log.info("Пользователи: '{}' и '{}' не являются друзьями.",
                    firstUser.getName(), secondUser.getName());
            return Arrays.asList(firstUser, secondUser);
        }
        firstUser.removeFriend(secondId);
        secondUser.removeFriend(firstId);
        log.info("Пользователи: '{}' и '{}' больше не друзья :(",
                firstUser.getName(), secondUser.getName());
        return Arrays.asList(firstUser, secondUser);
    }

    public List<User> getFriendsListById(int id) {
        User user = getById(id);
        log.info("Запрос на получение списка друзей пользователя '{}' выполнен успешно", user.getName());
        return user.getFriends().keySet().stream()
                .map(userStorage::getById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        User firstUser = getById(firstId);
        User secondUser = getById(secondId);
        log.info("Запрос на получение списка общих друзей пользователей '{}' и '{}' выполнен успешно",
                firstUser.getName(), secondUser.getName());
        return firstUser.getFriends().keySet().stream()
                .filter(secondUser.getFriends()::containsKey)
                .map(userStorage::getById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не заполнено, заменено на логин: '{}'", user.getName());
        }
    }

    private void check(User userToAdd) {
        boolean exists = userStorage.getUsers().values().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Введенный email пользователя: '{}'", userToAdd);
            throw new ValidationException("Пользователь с таким email или логином уже существует");
        }
    }

    private boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());
    }
}
