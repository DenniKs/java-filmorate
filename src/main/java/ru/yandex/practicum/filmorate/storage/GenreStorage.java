package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> getAllGenres();

    Collection<Genre> getGenresByFilmId(int filmId);

    Optional<Genre> getGenreById(int genreId);

    void addFilmGenres(int filmId, Collection<Genre> genres);

    void deleteFilmGenres(int filmId);
}