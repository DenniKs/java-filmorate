package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> getById(int id);

    Collection<Film> findAll();

    List<Film> getFilms();

    boolean deleteFilm(Film film);

    boolean addLike(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);

    Collection<Film> getPopularFilms(Integer count);
}