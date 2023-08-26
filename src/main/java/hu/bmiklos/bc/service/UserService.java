package hu.bmiklos.bc.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.UserRegistrationException;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Password;
import hu.bmiklos.bc.repository.PasswordRepository;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.service.security.SaltedPasswordEncoder;
import hu.bmiklos.bc.util.SaltGenerator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    public UserService(UserRepository userRepository, PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

    public void registerUser(User user, String password) {
        if (userRepository.findByExternalId(user.getExternalId()) != null) {
            throw new UserRegistrationException("External ID is already registered.");
        }

        userRepository.save(user);
        var salt = new SaltGenerator().generateSalt();
        var passwordHash = new SaltedPasswordEncoder("bcrypt", salt)
            .encode(password) ;
        var userPassword = new Password(user, passwordHash, salt, "bcrypt");
        passwordRepository.save(userPassword);
    }
}
