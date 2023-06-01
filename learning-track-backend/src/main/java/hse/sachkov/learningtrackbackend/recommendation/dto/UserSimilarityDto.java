package hse.sachkov.learningtrackbackend.recommendation.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class UserSimilarityDto {

    String username;
    Long externalId;

    @EqualsAndHashCode.Exclude
    Double score;
}
