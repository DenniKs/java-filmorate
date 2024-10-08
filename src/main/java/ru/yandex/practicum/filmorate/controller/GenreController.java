package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Получен GET запрос по адресу '/genres'");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Optional<Genre> getGenreById(@PathVariable int id) {
        log.info("Получен GET запрос по адресу '/genres/{}'", id);
        return genreService.getGenreById(id);
    }
}