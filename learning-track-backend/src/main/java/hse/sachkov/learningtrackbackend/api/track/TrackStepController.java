package hse.sachkov.learningtrackbackend.api.track;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserService;
import hse.sachkov.learningtrackbackend.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/track-steps")
public class TrackStepController {

    private final TrackStepService trackStepService;
    private final UserService userService;

    @DeleteMapping("/{trackStepId}")
    public void deleteTrackStepById(Principal userRequester, @PathVariable Long trackStepId) {
        log.info("Start processing DELETE /track-steps/{trackStepId} request . . .");

        if (userRequester == null) {
            throw new ApiException("You are NOT authorized!", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUsername(userRequester.getName());

        trackStepService.deleteUserTrackStepById(user, trackStepId);

        log.info("DELETE /track-steps/{trackStepId} request processed!");
    }
}
