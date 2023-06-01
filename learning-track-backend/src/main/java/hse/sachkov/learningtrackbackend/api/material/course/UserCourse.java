package hse.sachkov.learningtrackbackend.api.material.course;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserCourse extends Course {

    Boolean completed;
}
