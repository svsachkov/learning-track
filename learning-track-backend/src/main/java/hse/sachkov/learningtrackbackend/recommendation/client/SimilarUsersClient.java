package hse.sachkov.learningtrackbackend.recommendation.client;

import hse.sachkov.learningtrackbackend.api.skill.Skill;
import hse.sachkov.learningtrackbackend.api.skill.SkillLevel;
import hse.sachkov.learningtrackbackend.api.skill.SkillRepository;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserRepository;
import hse.sachkov.learningtrackbackend.exception.EmptyDesiredPositionException;
import hse.sachkov.learningtrackbackend.exception.NoSkillsException;
import hse.sachkov.learningtrackbackend.recommendation.dto.UserRecommendationProfileDTO;
import hse.sachkov.learningtrackbackend.recommendation.dto.UserSimilarityDto;
import hse.sachkov.learningtrackbackend.recommendation.request.SimilarUsersRequest;
import hse.sachkov.learningtrackbackend.recommendation.response.SimilarUsersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarUsersClient {

    private static final String SKILL_LEVEL_DELIMITER = ":";
    private static final double SKILL_SIMILARITY_THRESHOLD = 0.1;
    private static final double DESIRED_POSITION_THRESHOLD = 0.3;

    @Value("${application.recommender.scheme}")
    private String recommenderScheme;

    @Value("${application.recommender.recommenderModelHost}")
    private String recommenderModelHost;

    @Value("${application.recommender.similarUsersInSkillsEndpoint}")
    private String similarUsersInSkillsEndpoint;

    @Value("${application.recommender.similarUsersInDesiredPositionEndpoint}")
    private String similarUsersInDesiredPositionEndpoint;

    private final SkillRepository userSkillRepository;

    private final UserRepository userRepository;

    RestTemplate restTemplate = new RestTemplate();

    public List<UserSimilarityDto> requestSimilarUsersInSkill(User userRequester) {
        String url = recommenderScheme + "://" + recommenderModelHost + "/" + similarUsersInSkillsEndpoint;
        SimilarUsersRequest request;
        try {
            request = buildSimilarUsersInSkillRequest(userRequester);
        } catch (NoSkillsException e) {
            log.error("Unable to request similar users in skill");
            return Collections.emptyList();
        }

        SimilarUsersResponse similarUsersResponse =
                restTemplate.postForEntity(url, request, SimilarUsersResponse.class).getBody();
        if (similarUsersResponse == null || CollectionUtils.isEmpty(similarUsersResponse.getSimilarUsers())) {
            return null;
        }
        return similarUsersResponse.getSimilarUsers();
    }

    public List<UserSimilarityDto> requestSimilarUsersInDesiredPosition(User userRequester) {
        String url = recommenderScheme + "://" + recommenderModelHost + "/" + similarUsersInDesiredPositionEndpoint;
        SimilarUsersRequest request;
        try {
            request = buildSimilarUsersInDesiredPositionRequest(userRequester);
        } catch (EmptyDesiredPositionException e) {
            log.error("Unable to request similar users in desired position");
            return Collections.emptyList();
        }
        SimilarUsersResponse similarUsersResponse =
                restTemplate.postForEntity(url, request, SimilarUsersResponse.class).getBody();
        if (similarUsersResponse == null || CollectionUtils.isEmpty(similarUsersResponse.getSimilarUsers())) {
            return null;
        }
        return similarUsersResponse.getSimilarUsers();
    }

    private SimilarUsersRequest buildSimilarUsersInDesiredPositionRequest(User userRequester) {
        if (!StringUtils.hasText(userRequester.getDesiredPosition())) {
            throw new EmptyDesiredPositionException();
        }
        SimilarUsersRequest request = new SimilarUsersRequest();
        request.setThreshold(DESIRED_POSITION_THRESHOLD);

        request.setTargetUser(new UserRecommendationProfileDTO(userRequester.getUsername(), userRequester.getExternalId(),
                null, userRequester.getDesiredPosition()));

        List<UserRecommendationProfileDTO> otherUsersWithDesiredPosition = userRepository.findAllByUsernameNotInAndDesiredPositionIsNotNull(
                Collections.singletonList(userRequester.getUsername()));

        request.setOtherUsers(otherUsersWithDesiredPosition);
        return request;
    }

    private SimilarUsersRequest buildSimilarUsersInSkillRequest(User userRequester) {
        SimilarUsersRequest request = new SimilarUsersRequest();
        request.setThreshold(SKILL_SIMILARITY_THRESHOLD);
        List<Skill> targetUserSkillSet = userSkillRepository.findAllByUserUsername(userRequester.getUsername());
        if (CollectionUtils.isEmpty(targetUserSkillSet)) {
            throw new NoSkillsException();
        }
        String targetUserSkillSetString = buildStringSkillSet(targetUserSkillSet);

        request.setTargetUser(new UserRecommendationProfileDTO(userRequester.getUsername(), userRequester.getExternalId(),
                targetUserSkillSetString, userRequester.getDesiredPosition()));

        // Other users skills. Step 1. Find all users' skills except current's
        List<Skill> otherUserSkills = userSkillRepository.findAllByUserUsernameNotIn(
                Collections.singletonList(userRequester.getUsername()));

        // Other users skills. Step 2. Create a map: User -> Skills list
        Map<User, List<Skill>> userToSkillList = new HashMap<>();
        for (Skill otherUserSkill : otherUserSkills) {
            if (!userToSkillList.containsKey(otherUserSkill.getUser())) {
                userToSkillList.put(otherUserSkill.getUser(), new ArrayList<>());
            }
            userToSkillList.get(otherUserSkill.getUser()).add(otherUserSkill);
        }

        // Other users skills. Step 3. Turn skill set into a string. Pack up into list of (User, skills string)
        List<UserRecommendationProfileDTO> otherUserSkillsDto = new ArrayList<>();
        for (Map.Entry<User, List<Skill>> otherUserSkillsListEntry : userToSkillList.entrySet()) {
            otherUserSkillsDto.add(new UserRecommendationProfileDTO(
                    otherUserSkillsListEntry.getKey().getUsername(),
                    otherUserSkillsListEntry.getKey().getExternalId(),
                    buildStringSkillSet(otherUserSkillsListEntry.getValue()),
                    otherUserSkillsListEntry.getKey().getDesiredPosition()));
        }

        request.setOtherUsers(otherUserSkillsDto);
        return request;
    }

    private String buildStringSkillSet(List<Skill> targetUserSkillSet) {
        StringBuilder result = new StringBuilder();

        for (Skill skill : targetUserSkillSet) {
            result.append(skill.getSkill());
            result.append(SimilarUsersClient.SKILL_LEVEL_DELIMITER);
            result.append(SkillLevel.findByLevel(skill.getLevel()).toString().toLowerCase());
            result.append(" ");
        }
        // delete last space
        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }
}
