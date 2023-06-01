package hse.sachkov.learningtrackbackend.api.user;

import hse.sachkov.learningtrackbackend.api.material.Material;
import hse.sachkov.learningtrackbackend.exception.ApiException;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping()
    public User getCurrentUser(Principal userRequester) {
        log.info("Start processing GET /user request . . .");

        User user = userRepository.findByUsername(userRequester.getName()).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No users with such username!");
        }

        log.info("GET /user request processed!");

        return user;
    }

    @PutMapping()
    public User editCurrentUser(Principal userRequester, @RequestBody User newUser) {
        log.info("Start processing PUT /user request . . .");

        User user = userRepository.findByUsername(userRequester.getName()).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No users with such username!");
        }

        if (StringUtils.hasText(newUser.getUsername()) && !user.getUsername().equals(newUser.getUsername())) {
            throw new ApiException("Username is not allowed to be changed!", HttpStatus.FORBIDDEN);
        }

        user.setBirthdayYear(newUser.getBirthdayYear());
        user.setCity(newUser.getCity());
        user.setFullName(newUser.getFullName());
        user.setCollege(newUser.getCollege());
        user.setDesiredPosition(newUser.getDesiredPosition());

        user = userRepository.save(user);

        log.info("PUT /user request processed!");

        return user;
    }

    @GetMapping("/{username}")
    public User getUserByUsername(Principal userRequester, @PathVariable String username) {
        log.info("Start processing GET /user/{username} request . . .");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No users found with such username!");
        }

        // If the requested user is not current:
        if (!userRequester.getName().equals(username)) {
            // Limited info provided:
            User userResponse = new User();
            userResponse.setUsername(user.getUsername());
            userResponse.setFullName(user.getFullName());
            userResponse.setLastSeen(user.getLastSeen());
            userResponse.setCity(user.getCity());
            return userResponse;
        }

        log.info("GET /user/{username} request processed!");

        return user;
    }

    @GetMapping("/completedMaterials")
    public Set<Material> getCompletedMaterials(Principal userRequester) {
        log.info("Start processing GET /user/completedMaterials request . . .");

        User user = userRepository.findByUsername(userRequester.getName()).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No users with such username!");
        }

        Set<Material> materials = user.getMaterialsCompleted();

        log.info("GET /user/completedMaterials request processed!");

        return materials;
    }

    @PostMapping("/addCompletedMaterial")
    public void addCompletedMaterial(Principal userRequester, @RequestBody Material material) {
        log.info("Start processing POST /user/addCompletedMaterial request . . .");

        User user = userRepository.findByUsername(userRequester.getName()).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException("No users with such username!");
        }

        if (material == null || material.getId() == null) {
            throw new ApiException("Material id turned out to be null!", HttpStatus.BAD_REQUEST);
        }

        user.getMaterialsCompleted().add(material);

        userRepository.save(user);

        log.info("POST /user/addCompletedMaterial request processed!");
    }
}
