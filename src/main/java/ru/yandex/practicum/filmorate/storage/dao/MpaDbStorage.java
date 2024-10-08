package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM RATING_MPA";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("RATING_ID"),
                rs.getString("MPA_NAME"),
                rs.getString("DESCRIPTION"));
    }

    @Override
    public Optional<Mpa> getMpaById(int mpaId) {
        String sqlQuery = "SELECT * FROM RATING_MPA WHERE RATING_ID = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, mpaId);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}