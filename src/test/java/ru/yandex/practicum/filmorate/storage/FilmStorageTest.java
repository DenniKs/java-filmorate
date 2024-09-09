package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageTest {
    final FilmDbStorage filmStorage;

    final Film film1 = new Film(1,
            "film1 name",
            "film1 description",
            LocalDate.now().minusYears(10),
            90,
            7,
            new Mpa(1, "name", "description"),
            new ArrayList<>(),
            new ArrayList<>());

    final Film film = new Film(1,
            "Name film",
            "Description film",
            LocalDate.now().minusYears(10),
            100,
            7,
            new Mpa(1, "Name", "Description"),
            new ArrayList<>(),
            new ArrayList<>());

    @Test
    public void addFilmTest() {
        filmStorage.create(film);
        AssertionsForClassTypes.assertThat(film).extracting("id").isNotNull();
        AssertionsForClassTypes.assertThat(film).extracting("name").isNotNull();
    }

    @Test
    public void getFilmByIdTest() {
        filmStorage.create(film);
        Optional<Film> dbFilm = filmStorage.getById(1);

        assertThat(dbFilm)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void updateFilmTest() {
        Optional<Film> optionalAdded = filmStorage.getById(film.getId());

        assertThat(optionalAdded).isPresent();

        Film added = optionalAdded.get();
        added.setName("film updated");

        filmStorage.update(added);

        Optional<Film> optionalDbFilm = filmStorage.getById(added.getId());

        assertThat(optionalDbFilm).isPresent();

        Film dbFilm = optionalDbFilm.get();
        assertThat(dbFilm).hasFieldOrPropertyWithValue("name", "film updated");
    }

    @Test
    public void deleteFilmTest() {
        Optional<Film> addedFilm1 = filmStorage.create(film1);

        // Получаем список фильмов до удаления
        Collection<Film> beforeDelete = filmStorage.findAll();

        // Удаляем фильм
        Optional<Film> optionalFilmToDelete = filmStorage.getById(addedFilm1.get().getId());

        // Проверяем, что фильм для удаления существует
        assertThat(optionalFilmToDelete).isPresent();

        optionalFilmToDelete.ifPresent(filmStorage::deleteFilm);

        // Получаем список фильмов после удаления
        Collection<Film> afterDelete = filmStorage.findAll();

        // Проверяем, что количество фильмов уменьшилось на 1
        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }
}