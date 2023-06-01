package hse.sachkov.learningtrackbackend.recommendation;

import hse.sachkov.learningtrackbackend.api.material.Material;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    Material material;

    @EqualsAndHashCode.Exclude
    double matchingScore;
}
