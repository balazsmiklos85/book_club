package hu.bmiklos.bc.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.UserRegistrationException;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.Password;
import hu.bmiklos.bc.repository.EmailRepository;
import hu.bmiklos.bc.repository.PasswordRepository;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.UserMapper;
import hu.bmiklos.bc.service.security.SaltedPasswordEncoder;
import hu.bmiklos.bc.util.SaltGenerator;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    public UserService(UserRepository userRepository, PasswordRepository passwordRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
        this.emailRepository = emailRepository;
    }

    @Transactional
    public void deleteUser(Email email) {
        userRepository.delete(email.getUser());
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::mapToDto)
            .toList();
    }


    @Transactional
    public Email registerUser(String externalId, String name, String email, String confirmEmail, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new UserRegistrationException("The passwords do not match.");
        }

        try {
            User user = createUser(externalId, name);
            createPassword(user, password);
            return createEmail(user, email, confirmEmail);
        } catch (NumberFormatException e) {
            throw new UserRegistrationException("The external ID must be a number.");
        }
    }

    @Transactional
    public void setAsAdmin(UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setAdmin(true);
        userRepository.saveAndFlush(user);
    }

    private Email createEmail(User user, String emailAddress, String confirmEmail) {
        if (!emailAddress.equals(confirmEmail)) {
            throw new UserRegistrationException("The email addresses do not match.");
        }

        if (emailRepository.findById(emailAddress).isPresent()) {
            throw new UserRegistrationException("This email address is already registered.");
        }
        var email = new Email(emailAddress, user);
        return emailRepository.save(email);
    }

    private void createPassword(User user, String password) {
        var salt = new SaltGenerator().generateSalt();
        var passwordHash = new SaltedPasswordEncoder("bcrypt", salt)
                .encode(password);
        var userPassword = new Password(user, passwordHash, salt, "bcrypt");
        passwordRepository.save(userPassword);
    }

    private User createUser(String externalId, String name) {
        var extId = Integer.parseInt(externalId);
        if (userRepository.findByExternalId(extId).isPresent()) {
            throw new UserRegistrationException("This external ID is already registered.");
        }
        var user = new User(name, false, extId);
        return userRepository.save(user);
    }
}
