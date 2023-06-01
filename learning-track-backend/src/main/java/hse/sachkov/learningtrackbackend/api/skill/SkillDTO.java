package hse.sachkov.learningtrackbackend.api.skill;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillDTO {

    Long id;
    String username;
    String skill;
    Double level;
}
