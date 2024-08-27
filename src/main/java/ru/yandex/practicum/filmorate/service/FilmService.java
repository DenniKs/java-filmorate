package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        check(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> findAll() {
        log.info("Список фильмов отправлен");
        return filmStorage.findAll();
    }

    public Film getById(int id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден"));
        log.info("Фильм с id: {} отправлен", id);
        return film;
    }

    public Film deleteById(int id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден, удаление невозможно"));
        filmStorage.deleteById(id);
        log.info("Фильм с id: {} удалён", id);
        return film;
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден"));
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        film.getUsersLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден"));
        if (!film.getUsersLikes().contains(userId)) {
            throw new ObjectNotFoundException("Нет лайка от пользователя");
        }
        film.getUsersLikes().remove(userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id {}", userId, filmId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Список популярных фильмов отправлен");

        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void check(Film filmToAdd) {
        boolean exists = filmStorage.findAll().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Фильм к добавлению: {}", filmToAdd);
            throw new ValidationException("Такой фильм уже существует в коллекции");
        }
    }

    private boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}