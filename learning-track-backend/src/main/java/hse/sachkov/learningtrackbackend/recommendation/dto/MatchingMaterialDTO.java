package hse.sachkov.learningtrackbackend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingMaterialDTO {

    Long materialId;
    Double score;
}
