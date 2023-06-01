package hse.sachkov.learningtrackbackend.api.material;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialController {

    private final MaterialService materialService;
    private final UserService userService;

    @PostMapping("/{materialId}/like")
    public void like(Principal userRequester, @PathVariable Long materialId) {
        log.info("Start processing POST /material/like request . . .");

        User user = userService.getUserByUsername(userRequester.getName());

        materialService.like(materialId, user.getUsername());

        log.info("POST /material/like request processed!");
    }

    @PostMapping("/{materialId}/remove-like")
    public void removeLike(Principal userRequester, @PathVariable Long materialId) {
        log.info("Start processing POST /material/remove-like request . . .");

        User user = userService.getUserByUsername(userRequester.getName());

        materialService.removeLike(materialId, user.getUsername());

        log.info("POST /material/remove-like request processed!");
    }
}
