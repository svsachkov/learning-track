package hse.sachkov.learningtrackbackend.api.user;

import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No user found with provided username!");
        }

        return user;
    }
}
