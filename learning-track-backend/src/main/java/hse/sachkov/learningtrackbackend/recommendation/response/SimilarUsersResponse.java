package hse.sachkov.learningtrackbackend.recommendation.response;

import hse.sachkov.learningtrackbackend.recommendation.dto.UserSimilarityDto;
import lombok.Data;

import java.util.List;

@Data
public class SimilarUsersResponse {

    List<UserSimilarityDto> similarUsers;
}
