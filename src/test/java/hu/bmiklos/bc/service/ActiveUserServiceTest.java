package hu.bmiklos.bc.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.bmiklos.bc.business.security.EmailPrincipal;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ActiveUserServiceTest {

    private EmailPrincipal emailPrincipal;

    private SecurityContext securityContext = mock(SecurityContext.class);

    private Supplier<SecurityContext> securityContextSupplier = () -> securityContext;

    private TestableActiveUserService activeUserService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        var authentication = mock(Authentication.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        emailPrincipal = mock(EmailPrincipal.class);
        lenient().when(authentication.getPrincipal()).thenReturn(emailPrincipal);
        activeUserService = new TestableActiveUserService(userRepository);
    }

    @Test
    void isCurrentUserIsNullSafe() {
        User toCheck = null;

        boolean result = activeUserService.isCurrentUser(toCheck);

        assertFalse(result, "A null user cannot be the currently logged in user.");
    }

    @Test
    void userIsCurrentUserById() {
        UUID expectedUserId = UUID.randomUUID();
        hu.bmiklos.bc.domain.entities.User loggedInUser = new hu.bmiklos.bc.domain.entities.User(expectedUserId, "ignored user name", false, -1);
        when(emailPrincipal.getUser()).thenReturn(loggedInUser);
        var toCheck = new User();
        toCheck.setId(expectedUserId);

        boolean result = activeUserService.isCurrentUser(toCheck);

        assertTrue(result, "User objects with the same ID should be considered the same user.");
    }

    @Test
    void userIsCurrentUserByExternalId() {
        var userId = -1;
        when(emailPrincipal.getExternalId()).thenReturn(userId);
        var toCheck = new User();
        toCheck.setExternalId(userId);

        boolean result = activeUserService.isCurrentUser(toCheck);

        assertTrue(result, "User objects with the same external ID should be considered the same user.");
    }

    @Test
    void differentUsersAreNotTheSame() {
        UUID expectedUserId = UUID.randomUUID();
        hu.bmiklos.bc.domain.entities.User loggedInUser = new hu.bmiklos.bc.domain.entities.User(expectedUserId, "ignored user name", false, -1);
        when(emailPrincipal.getUser()).thenReturn(loggedInUser);
        when(emailPrincipal.getExternalId()).thenReturn(-1);
        var toCheck = new User();
        toCheck.setExternalId(-2);
        toCheck.setId(UUID.randomUUID());

        boolean result = activeUserService.isCurrentUser(toCheck);

        assertFalse(result, "User objects with the different IDs should be considered different.");

    }

    private class TestableActiveUserService extends ActiveUserService {

        public TestableActiveUserService(UserRepository userRepository) {
            super(userRepository);
            this.securityContext = securityContextSupplier;
        }
    }
}


