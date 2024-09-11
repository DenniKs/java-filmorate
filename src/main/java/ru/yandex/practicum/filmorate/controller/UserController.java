package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public Optional<User> createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public Optional<User> updateUser(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriendship(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriendship(id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getFriendsList(@PathVariable int id) {
        return userService.getFriendsListById(id);
    }

    @GetMapping("/{firstId}/friends/common/{secondId}")
    public Collection<User> getCommonFriends(@PathVariable int firstId, @PathVariable int secondId) {
        return userService.getCommonFriendsList(firstId, secondId);
    }
}