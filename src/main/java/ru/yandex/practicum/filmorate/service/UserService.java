package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public Optional<User> create(User user) {
        validate(user);
        check(user);
        return userStorage.create(user);
    }

    public Optional<User> update(User user) {
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

    public void addFriendship(int supposedUserId, int supposedFriendId) {
        User user = getUserStored(supposedUserId);
        User friend = getUserStored(supposedFriendId);
        userStorage.addFriend(user.getId(), friend.getId());
        log.info("Пользователь с id: '{}' добавлен с список друзей пользователя с id: '{}'", supposedUserId, supposedFriendId);
    }

    public void removeFriendship(int supposedUserId, int supposedFriendId) {
        User user = getUserStored(supposedUserId);
        User friend = getUserStored(supposedFriendId);
        userStorage.deleteFriend(user.getId(), friend.getId());
        log.info("Пользователь с id: '{}' удален с пользователя с id: '{}'", supposedUserId, supposedFriendId);
    }

    public Collection<User> getFriendsListById(int userId) {

        User user = getUserStored(userId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            friends.add(userStorage.getUser(id));
        }
        return friends;
    }

    public Collection<User> getCommonFriendsList(int supposedUserId, int supposedOtherId) {
        User user = getUserStored(supposedUserId);
        User otherUser = getUserStored(supposedOtherId);
        Collection<User> commonFriends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            if (otherUser.getFriends().contains(id)) {
                commonFriends.add(userStorage.getUser(id));
            }
        }
        return commonFriends;
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не заполнено, заменено на логин: '{}'", user.getName());
        }
    }

    private void check(User userToAdd) {
        boolean exists = userStorage.findAll().stream()
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

    private User getUserStored(int supposedId) {

        if (supposedId == Integer.MIN_VALUE) {
            throw new ObjectNotFoundException(String.format("Не удалось найти id пользователя: %d", supposedId));
        }
        User user = userStorage.getUser(supposedId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id: '%d' не зарегистрирован!", supposedId));
        }
        return user;
    }
}