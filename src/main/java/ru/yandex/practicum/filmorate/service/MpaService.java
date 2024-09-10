package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Optional<Mpa> getMpaById(int strId) {
        Optional<Mpa> mpa = mpaStorage.getMpaById(strId);
        if (mpa.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Возрастной рейтинг с id: '%d' не найден", strId));
        }
        return mpa;
    }
}