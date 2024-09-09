package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    Optional<User> create(User user);

    Optional<User> update(User user);

    Optional<User> getById(int id);

    Optional<User> deleteById(int id);

    boolean deleteUser(User user);

    Collection<User> findAll();

    Map<Integer, User> getUsers();

    boolean addFriend(Integer firstId, Integer secondId);

    boolean deleteFriend(Integer userId, Integer friendId);

    User getUser(final Integer id);
}