package hu.bmiklos.bc.business.usecase;

import hu.bmiklos.bc.business.exception.EmailAddressNotFoundException;
import hu.bmiklos.bc.business.exception.WrongPasswordException;
import hu.bmiklos.bc.business.repository.EmailRepository;
import hu.bmiklos.bc.business.security.EmailPrincipal;
import hu.bmiklos.bc.business.security.SaltedPasswordEncoder;
import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.Password;
import hu.bmiklos.bc.domain.entities.User;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginService implements AuthenticationProvider {
    private final EmailRepository emailRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<Email> email = emailRepository.findById(username);
        if (email.isEmpty()) {
            throw new EmailAddressNotFoundException(username);
        }

        User user = email.get().getUser();
        Password storedPassword = user.getPassword();
        boolean authenticationSuccessful = new SaltedPasswordEncoder(storedPassword.getHashAlgorithm(), storedPassword.getSalt())
            .matches(password, storedPassword.getPasswordHash());

        if (authenticationSuccessful) {
            var principal = new EmailPrincipal(email.get());
            return new UsernamePasswordAuthenticationToken(principal, storedPassword.getPasswordHash(), principal.getAuthorities());
        } else {
            throw new WrongPasswordException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
