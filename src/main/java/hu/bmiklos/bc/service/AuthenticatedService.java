package hu.bmiklos.bc.service;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import hu.bmiklos.bc.exception.NotAuthenticatedException;
import hu.bmiklos.bc.service.security.EmailPrincipal;

/**
 * @deprecated The functionality is to be moved to {@link ActiveUserService}. Then include that class in other services instead of extending this one. Composition over inheritance.
 */
@Deprecated
public abstract class AuthenticatedService {

    protected Supplier<SecurityContext> securityContext = SecurityContextHolder::getContext;

    @Autowired
    private UserDetailsService userDetailsService;

    public int getExternalUserId() {
        EmailPrincipal principal = getPrincipal();
        return principal.getExternalId();
    }

    public UUID getUserId() {
        EmailPrincipal principal = getPrincipal();
        return principal.getUser().getId();
    }

    protected EmailPrincipal getPrincipal() {
        Authentication authentication = securityContext.get().getAuthentication();
        if (authentication == null) {
            throw new NotAuthenticatedException();
        }
        if (authentication.getPrincipal() instanceof EmailPrincipal sessionPrincipal) {
            return sessionPrincipal;
        }
        if (authentication.getPrincipal() instanceof User user) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            if (userDetails instanceof EmailPrincipal dbPrincipal) {
                return dbPrincipal;
            }
        }
        throw new NotAuthenticatedException();
    }
}
