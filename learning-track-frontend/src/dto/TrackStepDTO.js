import {LearningMaterialDTO} from "./LearningMaterialDTO";

export class TrackStepDTO {
    trackStepId;
    stepOrderNumber;
    completed;
    material: LearningMaterialDTO = new LearningMaterialDTO();
}
