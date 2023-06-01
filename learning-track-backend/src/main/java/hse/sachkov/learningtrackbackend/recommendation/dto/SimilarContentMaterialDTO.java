package hse.sachkov.learningtrackbackend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarContentMaterialDTO {

    Long id;
    String overview;
}
