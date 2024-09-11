package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> create(User user) {
        final String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement prepareStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setString(2, user.getLogin());
            prepareStatement.setString(3, user.getName());
            prepareStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return prepareStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            String friendsSqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";

            List<Object[]> batchArgs = user.getFriends().stream()
                    .map(friendId -> new Object[] {id, friendId})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(friendsSqlQuery, batchArgs);
        }

        return this.getById(id);
    }

    @Override
    public Optional<User> update(User user) {
        final String sqlQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return this.getById(user.getId());
    }

    public Optional<User> getById(int id) {
        final String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователь с id: '" + id + "' не зарегистрирован!");
        }
    }

    public Optional<User> deleteById(int id) {
        return getById(id).map(user -> {
            final String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
            jdbcTemplate.update(sqlQuery, id);
            return user;
        });
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        String sqlGetReversFriend = "SELECT * FROM FRIENDSHIP " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlGetReversFriend, friendId, userId);
        boolean friendAccepted = rs.next();
        String sqlSetFriend = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlSetFriend, userId, friendId, friendAccepted);
        if (friendAccepted) {
            String sqlSetStatus = "UPDATE FRIENDSHIP SET STATUS = true " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?";
            jdbcTemplate.update(sqlSetStatus, friendId, userId);
        }
        return friendAccepted;
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        String sqlSetStatus = "UPDATE FRIENDSHIP SET STATUS = false " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlSetStatus, friendId, userId);
        return false;
    }

    @Override
    public User getUser(Integer id) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователь с id: " +
                    id + " не зарегистрирован!");
        }
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("USER_ID");
        return new User(
                userId,
                resultSet.getString("EMAIL"),
                resultSet.getString("LOGIN"),
                resultSet.getString("USER_NAME"),
                Objects.requireNonNull(resultSet.getDate("BIRTHDAY")).toLocalDate(),
                getUserFriends(userId));
    }

    private List<Integer> getUserFriends(int userId) {
        String sqlQuery = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }
}