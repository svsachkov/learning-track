package hse.sachkov.learningtrackbackend.recommendation.client;

import hse.sachkov.learningtrackbackend.api.material.Material;
import hse.sachkov.learningtrackbackend.api.material.MaterialRepository;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.EmptyDesiredPositionException;
import hse.sachkov.learningtrackbackend.recommendation.dto.MatchingMaterialDTO;
import hse.sachkov.learningtrackbackend.recommendation.dto.SimilarContentMaterialDTO;
import hse.sachkov.learningtrackbackend.recommendation.dto.SimilarContentUserDTO;
import hse.sachkov.learningtrackbackend.recommendation.request.SimilarContentForUserRequest;
import hse.sachkov.learningtrackbackend.recommendation.response.SimilarContentForUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingContentClient {

    @Value("${application.recommender.scheme}")
    private String recommenderScheme;

    @Value("${application.recommender.recommenderModelHost}")
    private String recommenderModelHost;

    @Value("${application.recommender.similarContentForUser}")
    private String similarContentForUserEndpoint;

    private final MaterialRepository learningMaterialRepository;

    RestTemplate restTemplate = new RestTemplate();

    public List<MatchingMaterialDTO> requestMatchingContent(User userRequester) {
        String url = recommenderScheme + "://" + recommenderModelHost + "/" + similarContentForUserEndpoint;
        SimilarContentForUserRequest request;
        try {
            request = buildMatchingContentRequest(userRequester);
        } catch (EmptyDesiredPositionException e) {
            log.error("Unable to request matching materials");
            return Collections.emptyList();
        }

        SimilarContentForUserResponse similarMaterialsResponse =
                restTemplate.postForEntity(url, request, SimilarContentForUserResponse.class).getBody();
        if (similarMaterialsResponse == null || CollectionUtils.isEmpty(similarMaterialsResponse.getMatchingMaterials())) {
            return new ArrayList<>();
        }
        return similarMaterialsResponse.getMatchingMaterials();
    }

    private SimilarContentForUserRequest buildMatchingContentRequest(User userRequester) {
        if (!StringUtils.hasText(userRequester.getDesiredPosition())) {
            throw new EmptyDesiredPositionException();
        }

        SimilarContentForUserRequest request = new SimilarContentForUserRequest();
        request.setTargetUser(new SimilarContentUserDTO(userRequester.getDesiredPosition().toLowerCase()));
        List<Material> allMaterials = (List<Material>) learningMaterialRepository.findAll();
        List<SimilarContentMaterialDTO> allMaterialsDto = allMaterials
                .stream()
                .map(x -> new SimilarContentMaterialDTO(x.getId(), x.getTitle().toLowerCase() + " " + x.getDescription().toLowerCase()))
                .collect(Collectors.toList());
        request.setMaterials(allMaterialsDto);
        return request;
    }
}
