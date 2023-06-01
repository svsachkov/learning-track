package hse.sachkov.learningtrackbackend.api.material.course;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long>, PagingAndSortingRepository<Course, Long> {

    @NonNull Page<Course> findAll(@NonNull Pageable pageable);

    Page<Course> findAllByTitleContainingIgnoreCase(Pageable pageable, String search);
}
