package hu.bmiklos.bc.service.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.UserDto;

public class UserMapper {

    private UserMapper() {}

    public static UserDto mapToDto(@NonNull User user) {
        return mapToDto(user, user.getExternalId());
    }

    public static UserDto mapToDto(@Nullable User user, int externalId) {
        Optional<User> safeUser = Optional.ofNullable(user);
        UUID userId = safeUser.map(User::getId)
            .orElse(null);
        String userName = safeUser.map(User::getName)
            .orElse(null);
        List<String> emails = safeUser.map(User::getEmails)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(Email::getEmailAddress)
            .toList();
        return new UserDto(userId, userName, externalId, emails);
    }    
}
