package hu.bmiklos.bc.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.exception.UserRegistrationException;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.UserPassword;
import hu.bmiklos.bc.repository.PasswordRepository;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.util.SaltGenerator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void registerUser(User user, String password) {
        if (userRepository.findByExternalId(user.getExternalId()) != null) {
            throw new UserRegistrationException("External ID is already registered.");
        }

        var salt = new SaltGenerator().generateSalt();
        var passwordHash = passwordEncoder.encode(password + salt);
        userRepository.save(user);
        var userPassword = new UserPassword(user, passwordHash, salt, "bcrypt");
        passwordRepository.save(userPassword);
    }
}
