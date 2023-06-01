package hse.sachkov.learningtrackbackend.api.material.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserVacancy extends Vacancy {

    Boolean viewed;
}
