package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
public class User {
    private int id;

    @JsonIgnore
    private Map<Integer, StatusRelation> friends = new HashMap<>();

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть валидной и содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public void addOrUpdateFriend(int friendId, StatusRelation status) {
        friends.put(friendId, status);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public StatusRelation getFriendshipStatus(int friendId) {
        return friends.get(friendId);
    }
}