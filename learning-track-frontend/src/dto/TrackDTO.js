import {TrackStepDTO} from "./TrackStepDTO";

export class TrackDTO {
    trackId;
    destination;
    trackSteps: TrackStepDTO[] = [];

    addTrackStep(trackStep: TrackStepDTO) {
        this.trackSteps.push(trackStep);
    }
}
