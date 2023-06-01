package hse.sachkov.learningtrackbackend.api.track;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.ApiException;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackStepService {

    private final TrackStepRepository trackStepRepository;

    public void deleteUserTrackStepById(User user, Long trackStepId) {
        TrackStep trackStep = trackStepRepository.findById(trackStepId).orElse(null);

        if (trackStep == null) {
            throw new EntityNotFoundException("No track's step found with provided id!");
        }

        Track track = trackStep.getTrack();

        User trackUser = track.getUser();

        if (user != trackUser) {
            throw new ApiException("You have no rules to delete different user's track's step!", HttpStatus.FORBIDDEN);
        }

        trackStepRepository.deleteById(trackStepId);
    }
}
