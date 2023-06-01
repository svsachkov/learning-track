package hse.sachkov.learningtrackbackend.recommendation.request;

import hse.sachkov.learningtrackbackend.recommendation.dto.SimilarContentMaterialDTO;
import hse.sachkov.learningtrackbackend.recommendation.dto.SimilarContentUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarContentForUserRequest {

    SimilarContentUserDTO targetUser;
    List<SimilarContentMaterialDTO> materials;
}
