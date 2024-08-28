package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Optional<User> getById(int id);

    Optional<User> deleteById(int id);

    Collection<User> findAll();

    Map<Integer, User> getUsers();

}