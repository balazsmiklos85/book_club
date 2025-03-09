package hu.bmiklos.bc.service;

import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_ADMIN;

import java.util.Objects;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.NotAuthenticatedException;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.UserMapper;
import hu.bmiklos.bc.service.security.EmailPrincipal;

@Service
public class ActiveUserService extends AuthenticatedService {

    private final UserRepository userRepository;

    public ActiveUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin() {
        try {
            EmailPrincipal principal = getPrincipal();
            return principal.getAuthorities().contains(BOOKCLUB_ADMIN);
        } catch (NotAuthenticatedException e) {
            return false;
        }
    }

    public UserDto getUser() {
        return userRepository.findById(getUserId())
            .map(UserMapper::mapToDto)
            .orElseThrow(NotAuthenticatedException::new);
    }

    public boolean isCurrentUser(Integer externalId) {
        return externalId != null && Objects.equals(getExternalUserId(), externalId);
    }

    public boolean isCurrentUser(UUID userId) {
        return userId != null && Objects.equals(getUserId(), userId);
    }

    /**
     * Checks if the given user is the currently logged in user.
     *
     * @param  user  the user to be checked.
     * @return       true if the given user is the current user, false otherwise.
     */
    public boolean isCurrentUser(@Nullable User user) {
        if (user == null) {
            return false;
        }
        return isCurrentUser(user.getId()) || isCurrentUser(user.getExternalId());
    }
}
