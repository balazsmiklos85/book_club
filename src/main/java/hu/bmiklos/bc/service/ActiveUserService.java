package hu.bmiklos.bc.service;

import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_ADMIN;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.NotAuthenticatedException;
import hu.bmiklos.bc.service.security.EmailPrincipal;

@Service
public class ActiveUserService extends AuthenticatedService {

    public boolean isAdmin() {
        try {
            EmailPrincipal principal = getPrincipal();
            return principal.getAuthorities().contains(BOOKCLUB_ADMIN);
        } catch (NotAuthenticatedException e) {
            return false;
        }
    }
}
