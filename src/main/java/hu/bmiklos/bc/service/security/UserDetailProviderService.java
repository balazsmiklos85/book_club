package hu.bmiklos.bc.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.EmailAddressNotFoundException;
import hu.bmiklos.bc.exception.UserNotFoundException;
import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.repository.EmailRepository;

@Service
public class UserDetailProviderService implements UserDetailsService {
    private EmailRepository emailRepository;

    public UserDetailProviderService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) {
        Email email = emailRepository.findById(emailAddress)
            .orElse(null);
        if (email == null) {
            throw new EmailAddressNotFoundException(emailAddress);
        }
        if (email.getUser() == null) {
            throw new UserNotFoundException(emailAddress);
        }
        return new EmailPrincipal(email);
    }
}
