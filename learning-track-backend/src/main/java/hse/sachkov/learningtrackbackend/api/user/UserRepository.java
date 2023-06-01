package hse.sachkov.learningtrackbackend.api.user;

import hse.sachkov.learningtrackbackend.recommendation.dto.UserRecommendationProfileDTO;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findUserByExternalId(Long id);

    @Query(value = "select u.external_id from users u where u.username=:username", nativeQuery = true)
    Long findExternalIdByUsername(String username);

    @Query(value =
            "select " +
                    "new hse.sachkov.learningtrackbackend.recommendation.dto.UserRecommendationProfileDTO(username, externalId, desiredPosition) " +
                    "from User " +
                    "where desiredPosition is not null and username not in :username")
    List<UserRecommendationProfileDTO> findAllByUsernameNotInAndDesiredPositionIsNotNull(List<String> username);

}
