package hse.sachkov.learningtrackbackend.api.skill;

import hse.sachkov.learningtrackbackend.api.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    private SkillDTO mapToUserSkillDto(Skill userSkill) {
        SkillDTO userSkillDto = new SkillDTO();
        userSkillDto.setSkill(userSkill.getSkill());
        userSkillDto.setLevel(userSkill.getLevel());
        userSkillDto.setId(userSkill.getId());
        userSkillDto.setUsername(userSkill.getUser().getUsername());

        return userSkillDto;
    }

    public Skill mapToUserSkill(SkillDTO userSkillDto) {
        Skill userSkill = new Skill();
        userSkill.setSkill(userSkillDto.getSkill());
        userSkill.setLevel(userSkillDto.getLevel());
        userSkill.setId(userSkillDto.getId());
        userSkill.setUser(userRepository.findByUsername(userSkillDto.getUsername()).orElse(null));

        return userSkill;
    }

    public List<SkillDTO> getAllUserSkills(Principal userRequester) {
        List<Skill> allSkills = skillRepository.findAllByUserUsername(userRequester.getName());
        return allSkills.stream().map(this::mapToUserSkillDto).collect(Collectors.toList());
    }
}
