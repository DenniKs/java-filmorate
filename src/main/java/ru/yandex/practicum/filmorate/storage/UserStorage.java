package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User getById(int id);

    User deleteById(int id);

    Collection<User> findAll();

    Map<Integer, User> getUsers();

}