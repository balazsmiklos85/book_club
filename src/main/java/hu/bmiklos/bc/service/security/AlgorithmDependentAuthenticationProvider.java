package hu.bmiklos.bc.service.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import hu.bmiklos.bc.exception.EmailAddressNotFoundException;
import hu.bmiklos.bc.exception.WrongPasswordException;
import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Password;
import hu.bmiklos.bc.repository.EmailRepository;

@Component
public class AlgorithmDependentAuthenticationProvider implements AuthenticationProvider {
    private EmailRepository emailRepository;

    public AlgorithmDependentAuthenticationProvider(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

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
            return new UsernamePasswordAuthenticationToken(principal, password);
        } else {
            throw new WrongPasswordException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
