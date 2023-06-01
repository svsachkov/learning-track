package hse.sachkov.learningtrackbackend.api.material.course;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping()
    public Page<Course> getCourses(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "12") int page_size,
                                   @RequestParam(required = false) String search) {
        log.info("Start processing GET /courses request . . .");

        Page<Course> coursesPage = courseService.getCourses(PageRequest.of(page, page_size), search);

        log.info("GET /courses request processed!");

        return coursesPage;
    }

    @GetMapping("/all")
    public List<Course> getAllCourses() {
        log.info("Start processing GET /courses/all request . . .");

        List<Course> courses = courseService.getAllCourses();

        log.info("GET /courses/all request processed!");

        return courses;
    }

    @GetMapping("/{courseId}")
    public Course getCourseById(Principal userRequester, @PathVariable Long courseId) {
        log.info("Start processing GET /courses/{id} request . . .");

        Course course = courseService.getCourseById(courseId);

        if (userRequester != null) {
            User user = userService.getUserByUsername(userRequester.getName());
            course.setLiked(courseService.isCourseLikedByUser(course, user));
            course.setCompleted(courseService.isCourseCompletedByUser(course, user));
        }

        log.info("GET /courses/{id} request processed!");

        return course;
    }
}
