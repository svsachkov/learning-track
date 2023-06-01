package hse.sachkov.learningtrackbackend.recommendation.response;

import hse.sachkov.learningtrackbackend.recommendation.dto.MatchingMaterialDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarContentForUserResponse {

    List<MatchingMaterialDTO> matchingMaterials;
}
