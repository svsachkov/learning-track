package hse.sachkov.learningtrackbackend.api.skill;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SkillRepository extends CrudRepository<Skill, Long> {

    List<Skill> findAllByUserUsername(String username);

    List<Skill> findAllByUserUsernameNotIn(Collection<String> userUsername);

    Skill findFirstByUserUsernameAndSkill(String username, String skill);

    Skill findFirstByUserUsernameAndSkillAndIdNot(String username, String skill, Long id);

    @Override
    @Modifying
    @Query(value = "delete from skills where id=:id", nativeQuery = true)
    void deleteById(Long id);

    @Query(value = "select distinct s.skill from skills s", nativeQuery = true)
    List<String> findAllSkillNames();
}
