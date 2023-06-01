package hse.sachkov.learningtrackbackend.api.track;

import hse.sachkov.learningtrackbackend.api.user.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {

    Optional<List<Track>> findAllByUserOrderByCreationDateDesc(User user);

    Optional<Track> findFirstByUserOrderByCreationDateDesc(User user);
}
