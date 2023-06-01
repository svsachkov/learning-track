package hse.sachkov.learningtrackbackend.api.track;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackStepRepository extends CrudRepository<TrackStep, Long> {
}
