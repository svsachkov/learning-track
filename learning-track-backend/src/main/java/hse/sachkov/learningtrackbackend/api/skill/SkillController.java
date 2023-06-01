package hse.sachkov.learningtrackbackend.api.skill;

import hse.sachkov.learningtrackbackend.api.user.UserRepository;
import hse.sachkov.learningtrackbackend.exception.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @GetMapping()
    public List<SkillDTO> getCompletedArticles(Principal userRequester) {
        log.info("Start processing GET /skills request . . .");

        List<SkillDTO> skills = skillService.getAllUserSkills(userRequester);

        log.info("GET /skills request processed!");

        return skills;
    }

    @PostMapping()
    public List<SkillDTO> updateSkills(Principal userRequester, @RequestBody List<SkillDTO> userSkillDtos) {
        log.info("Start processing POST /skills request . . .");

        if (userSkillDtos == null) {
            throw new ApiException("", HttpStatus.BAD_REQUEST);
        }
        for (SkillDTO userSkillDto : userSkillDtos) {
            Skill userSkill = skillService.mapToUserSkill(userSkillDto);
            if (userSkill.getUser() == null) {
                userSkill.setUser(userRepository.findByUsername(userRequester.getName()).orElse(null));
            }
            if (!StringUtils.hasText(userSkill.getSkill()) || userSkill.getLevel() == null) {
                continue;
            }
            if (userSkill.getId() == null) {
                Skill searchBySkill = skillRepository.findFirstByUserUsernameAndSkill(userSkill.getUser().getUsername(),
                        userSkill.getSkill());
                if (searchBySkill != null) {
                    userSkill.setId(searchBySkill.getId());
                    userSkill.setLevel(searchBySkill.getLevel());
                }
            } else {
                if (skillRepository.findFirstByUserUsernameAndSkillAndIdNot(userSkill.getUser().getUsername(),
                        userSkill.getSkill(), userSkill.getId()) != null) {
                    continue;
                }
            }
            skillRepository.save(userSkill);
        }

        List<SkillDTO> skills = skillService.getAllUserSkills(userRequester);

        log.info("POST /skills request processed!");

        return skills;
    }

    @PostMapping("/add")
    public void addSkill(Principal userRequester, @RequestBody SkillDTO userSkillDto) {
        log.info("Start processing POST /skills/add request . . .");

        Skill userSkill = skillService.mapToUserSkill(userSkillDto);

        if (!Objects.equals(userSkill.getUser().getUsername(), userRequester.getName())) {
            throw new ApiException("Current user is different from who you're trying to edit", HttpStatus.BAD_REQUEST);
        }

        skillRepository.save(userSkill);

        log.info("POST /skills/add request processed!");
    }

    @DeleteMapping("/remove")
    public List<SkillDTO> deleteSkill(Principal userRequester, @RequestBody SkillDTO userSkillDto) {
        log.info("Start processing DELETE /skills/remove request . . .");

        skillRepository.deleteById(userSkillDto.getId());

        List<SkillDTO> skills = skillService.getAllUserSkills(userRequester);

        log.info("DELETE /skills/remove request processed!");

        return skills;
    }

    @GetMapping("/names")
    public List<String> getSkillNames() {
        log.info("Start processing GET /skills/names request . . .");

        List<String> allSkills = skillRepository.findAllSkillNames();
        Collections.sort(allSkills);

        log.info("GET /skills/names request processed!");

        return allSkills;
    }
}
