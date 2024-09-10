package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Collection<Genre> getFilmGenres(int filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

    public Optional<Genre> getGenreById(int strId) {
        Optional<Genre> genre = genreStorage.getGenreById(strId);
        if (genre.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Жанр с id: '%d' не найден", strId));
        }
        return genre;
    }

    public void deleteFilmGenres(int filmId) {
        genreStorage.deleteFilmGenres(filmId);
    }

    public void addFilmGenres(int filmId, Collection<Genre> genres) {
        genreStorage.addFilmGenres(filmId, genres);
    }
}