package hse.sachkov.learningtrackbackend.security.auth;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserRepository;
import hse.sachkov.learningtrackbackend.security.applicationuser.ApplicationUser;
import hse.sachkov.learningtrackbackend.security.applicationuser.ApplicationUserRepository;
import hse.sachkov.learningtrackbackend.security.applicationuser.ApplicationUserRole;
import hse.sachkov.learningtrackbackend.security.exception.ApplicationUserAlreadyRegisteredException;
import hse.sachkov.learningtrackbackend.security.exception.ApplicationUserNotFoundException;
import hse.sachkov.learningtrackbackend.security.exception.InvalidPasswordException;
import hse.sachkov.learningtrackbackend.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository applicationUserRepository;

    private final JavaMailSender mailSender;

    public void register(AuthRequest registerRequest) {
        if (applicationUserRepository.findById(registerRequest.getUsername()).isPresent()) {
            throw new ApplicationUserAlreadyRegisteredException();
        }

        User user = new User(registerRequest.getUsername());
        while (true) {
            Long id = RandomUtils.nextLong();
            if (userRepository.findUserByExternalId(id).isEmpty()) {
                user.setExternalId(id);
                break;
            }
        }
        userRepository.save(user);

        ApplicationUser applicationUser = new ApplicationUser(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getUsername(),
                Collections.singleton(ApplicationUserRole.USER),
                true, true, true, true
        );
        applicationUserRepository.save(applicationUser);
    }

    public AbstractMap.SimpleEntry<String, String> login(AuthRequest loginRequest) {
        ApplicationUser applicationUser = applicationUserRepository.findById(loginRequest.getUsername()).orElse(null);

        if (applicationUser == null) {
            throw new ApplicationUserNotFoundException();
        } else if (!passwordEncoder.matches(loginRequest.getPassword(), applicationUser.getPassword())) {
            throw new InvalidPasswordException();
        }

        return new AbstractMap.SimpleEntry<>(
                jwtProvider.generateAccessToken(applicationUser),   // access_token
                jwtProvider.generateRefreshToken(applicationUser)); // refresh_token
    }

    public AbstractMap.SimpleEntry<String, String> loginWithCode(AuthRequest authRequest) {
        // If user is not registered:
        if (userRepository.findById(authRequest.getUsername()).isEmpty()) {
            User user = new User(authRequest.getUsername());
            while (true) {
                Long id = RandomUtils.nextLong();
                if (userRepository.findUserByExternalId(id).isEmpty()) {
                    user.setExternalId(id);
                    break;
                }
            }
            userRepository.save(user);
        }

        return login(authRequest);
    }

    public void auth(AuthRequest authRequest) {
        // Generates a random code between 10000 and 99999:
        String code = String.valueOf(new Random().nextInt(90000) + 10000);

        ApplicationUser applicationUser = applicationUserRepository.findById(authRequest.getUsername()).orElse(null);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(authRequest.getUsername());
        message.setSubject(code);
        message.setText(code);

        mailSender.send(message);

        if (applicationUser == null) {
            ApplicationUser newApplicationUser = new ApplicationUser(
                    authRequest.getUsername(),
                    passwordEncoder.encode(code),
                    authRequest.getUsername(),
                    Collections.singleton(ApplicationUserRole.USER),
                    true, true, true, true
            );
            applicationUserRepository.save(newApplicationUser);
        } else {
            applicationUser.setPassword(passwordEncoder.encode(code));
            applicationUserRepository.save(applicationUser);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findById(username).orElse(null);
    }
}
