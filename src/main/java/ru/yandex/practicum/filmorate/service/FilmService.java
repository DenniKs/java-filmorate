package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Optional<Film> create(Film film) throws ValidationException {
        check(film);

        if (mpaStorage.getMpaById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException(String.format("Возрастной рейтинг с id: '%d' не найден", film.getMpa().getId()));
        }

        Set<Integer> genreFilmIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Integer> genresIds = genreStorage.getAllGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!genresIds.containsAll(genreFilmIds)) {
            throw new ValidationException("Один или несколько жанров не найдены в списке доступных жанров");
        }

        return filmStorage.create(film);
    }

    public Optional<Film> update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> findAll() {
        log.info("Список фильмов отправлен");
        return filmStorage.findAll();
    }

    public Film getById(int id) {
        log.info("Фильм с id: '{}' отправлен", id);
        return getFilmStored(id);
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден"));
        User user = userStorage.getById(userId)
            .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        filmStorage.addLike(film.getId(), user.getId());
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        return film;
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmStored(filmId);
        User user = userService.getById(userId);
        filmStorage.deleteLike(film.getId(), user.getId());
        log.info("У Фильм с id: '{}' удалён лайк", filmId);
    }

    public Collection<Film> getPopularFilms(int count) {

        log.info("Список популярных фильмов отправлен");
        return filmStorage.getPopularFilms(count);
    }

    private Film getFilmStored(int supposedId) {
        if (supposedId == Integer.MIN_VALUE) {
            throw new ObjectNotFoundException(String.format("Не удалось найти id фильма: '%d'", supposedId));
        }
        return filmStorage.getById(supposedId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Фильм с id: '%d' не найден", supposedId)));
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
                filmToAdd.getReleaseDate().equals(film.getReleaseDate()) &&
                filmToAdd.getDuration() == (film.getDuration()) &&
                filmToAdd.getMpa().equals(film.getMpa()) &&
                filmToAdd.getGenres().equals(film.getGenres());
    }
}