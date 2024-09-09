package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean deleteFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        return true;
    }

    @Override
    public boolean addFilmGenres(int filmId, Collection<Genre> genres) {
        for (Genre genre : genres) {
            String sqlQuery = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) KEY (FILM_ID, GENRE_ID) VALUES (?, ?);";
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }
        return true;
    }

    @Override
    public Collection<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT GENRES.GENRE_ID, GENRES.GENRE_NAME FROM GENRES " +
                "INNER JOIN FILM_GENRE ON GENRES.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("GENRE_ID"), resultSet.getString("GENRE_NAME"));
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME FROM GENRES ORDER BY GENRE_ID";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
    }
}