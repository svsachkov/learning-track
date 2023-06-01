package hse.sachkov.learningtrackbackend.recommendation.request;

import hse.sachkov.learningtrackbackend.recommendation.dto.UserRecommendationProfileDTO;
import lombok.Data;

import java.util.List;

@Data
public class SimilarUsersRequest {

    Double threshold;
    List<UserRecommendationProfileDTO> otherUsers;
    UserRecommendationProfileDTO targetUser;
}
