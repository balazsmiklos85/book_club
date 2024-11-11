package hu.bmiklos.bc.business.security;

import static java.util.Objects.isNull;

import hu.bmiklos.bc.business.exception.EmailAddressNotFoundException;
import hu.bmiklos.bc.business.exception.UserNotFoundException;
import hu.bmiklos.bc.business.repository.EmailRepository;
import hu.bmiklos.bc.domain.entities.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailProviderService implements UserDetailsService {
    private final EmailRepository emailRepository;

    @Override
    public UserDetails loadUserByUsername(String emailAddress) {
        Email email = emailRepository.findById(emailAddress)
            .orElseThrow(() -> new EmailAddressNotFoundException(emailAddress));
        if (isNull(email.getUser())) {
            throw new UserNotFoundException(emailAddress);
        }
        return new EmailPrincipal(email);
    }
}
