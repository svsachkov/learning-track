package hse.sachkov.learningtrackbackend.api.material.course;

import com.google.common.collect.Lists;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> getCourses(Pageable pageable, String search) {
        if (search != null) {
            return courseRepository.findAllByTitleContainingIgnoreCase(pageable, search);
        }
        
        return courseRepository.findAll(pageable);
    }

    public List<Course> getAllCourses() {
        return Lists.newArrayList(courseRepository.findAll());
    }

    public Course getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);

        if (course == null) {
            throw new EntityNotFoundException("No course found with provided id!");
        }

        return course;
    }

    public boolean isCourseLikedByUser(Course course, User user) {
        return course.getLikedUsers().contains(user);
    }

    public boolean isCourseCompletedByUser(Course course, User user) {
        return user.getMaterialsCompleted().contains(course);
    }
}
