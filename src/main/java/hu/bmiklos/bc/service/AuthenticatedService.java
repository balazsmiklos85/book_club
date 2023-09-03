package hu.bmiklos.bc.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import hu.bmiklos.bc.exception.NotAuthenticatedException;
import hu.bmiklos.bc.service.security.EmailPrincipal;

public abstract class AuthenticatedService {

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

    private EmailPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
