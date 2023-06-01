package hse.sachkov.learningtrackbackend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationProfileDTO {

    String username;
    Long externalId;
    String skillSet;
    String desiredPosition;

    public UserRecommendationProfileDTO(String username, Long externalId, String desiredPosition) {
        this.username = username;
        this.externalId = externalId;
        this.desiredPosition = desiredPosition;
    }
}
