package hse.sachkov.learningtrackbackend.api.track;

import hse.sachkov.learningtrackbackend.api.user.UserService;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;
    private final UserService userService;

    @GetMapping()
    public List<Track> getUserTracks(Principal userRequester) {
        log.info("Start processing GET /tracks request . . .");

        if (userRequester == null) {
            throw new ApiException("You are NOT authorized!", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUsername(userRequester.getName());

        List<Track> userTracks = trackService.getUserTracks(user);

        log.info("GET /tracks request processed!");

        return userTracks;
    }

    @GetMapping("/latest")
    public Track getLatestUserTrack(Principal userRequester) {
        log.info("Start processing GET /tracks/latest request . . .");

        if (userRequester == null) {
            throw new ApiException("You are NOT authorized!", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUsername(userRequester.getName());

        Track track = trackService.getLatestUserTrack(user);

        log.info("GET /tracks/latest request processed!");

        return track;
    }

    @PostMapping("/generate")
    public Track generateNewTrack(Principal userRequester) {
        log.info("Start processing POST /tracks/generate request . . .");

        if (userRequester == null) {
            throw new ApiException("You are NOT authorized!", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUsername(userRequester.getName());

        Track generatedUserTrack = trackService.generetaNewUserTrack(user);

        log.info("POST /tracks/generate request processed!");

        return generatedUserTrack;
    }

    @DeleteMapping("/{trackId}")
    public void deleteUserTrackById(Principal userRequester, @PathVariable Long trackId) {
        log.info("Start processing DELETE /tracks/{trackId} request . . .");

        if (userRequester == null) {
            throw new ApiException("You are NOT authorized!", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUsername(userRequester.getName());

        trackService.deleteUserTrackById(user, trackId);

        log.info("DELETE /tracks/{trackId} request processed!");
    }

    @GetMapping("/all")
    public List<Track> getAllTracks() {
        log.info("Start processing GET /tracks/all request . . .");

        List<Track> tracks = trackService.getAllTracks();

        log.info("GET /tracks/all request processed!");

        return tracks;
    }
}
