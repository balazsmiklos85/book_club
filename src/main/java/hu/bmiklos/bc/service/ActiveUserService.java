package hu.bmiklos.bc.service;

import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_ADMIN;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.NotAuthenticatedException;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.UserMapper;
import hu.bmiklos.bc.service.security.EmailPrincipal;
import jakarta.transaction.Transactional;

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
        return Objects.equals(getExternalUserId(), externalId);
    }

    public boolean isCurrentUser(UUID userId) {
        return Objects.equals(getUserId(), userId);
    }
}
