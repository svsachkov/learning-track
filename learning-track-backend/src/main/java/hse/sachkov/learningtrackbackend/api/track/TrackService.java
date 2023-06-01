package hse.sachkov.learningtrackbackend.api.track;

import com.google.common.collect.Lists;

import hse.sachkov.learningtrackbackend.api.material.MaterialRepository;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.ApiException;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;
import hse.sachkov.learningtrackbackend.recommendation.RecommendationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final MaterialRepository materialRepository;

    private final RecommendationService recommendationService;

    public List<Track> getUserTracks(User user) {
        List<Track> userTracks = trackRepository.findAllByUserOrderByCreationDateDesc(user).orElse(null);

        if (userTracks == null) {
            throw new ApiException("No generated tracks yet!", HttpStatus.NOT_FOUND);
        }

        for (Track track : userTracks) {
            for (TrackStep trackStep : track.getTrackSteps()) {
                trackStep.setCompleted(user.getMaterialsCompleted().contains(trackStep.getMaterial()));
                materialRepository.save(trackStep.getMaterial());
            }
        }

        return userTracks;
    }

    public Track getLatestUserTrack(User user) {
        Track track = trackRepository.findFirstByUserOrderByCreationDateDesc(user).orElse(null);

        if (track == null) {
            throw new ApiException("No generated tracks yet!", HttpStatus.NOT_FOUND);
        }

        for (TrackStep trackStep : track.getTrackSteps()) {
            trackStep.setCompleted(user.getMaterialsCompleted().contains(trackStep.getMaterial()));
            materialRepository.save(trackStep.getMaterial());
        }

        return track;
    }

    public void deleteUserTrackById(User user, Long trackId) {
        Track track = trackRepository.findById(trackId).orElse(null);

        if (track == null) {
            throw new EntityNotFoundException("No track found with provided id!");
        }

        User trackUser = track.getUser();

        if (user != trackUser) {
            throw new ApiException("You have no rules to delete different user's track!", HttpStatus.FORBIDDEN);
        }

        trackRepository.deleteById(trackId);
    }

    public Track generetaNewUserTrack(User user) {
        Track generatedUserTrack = recommendationService.generateTrack(user);

        trackRepository.save(generatedUserTrack);

        return generatedUserTrack;
    }

    public List<Track> getAllTracks() {
        return Lists.newArrayList(trackRepository.findAll());
    }
}
