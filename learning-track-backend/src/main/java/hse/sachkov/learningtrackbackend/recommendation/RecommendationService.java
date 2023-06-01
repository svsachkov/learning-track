package hse.sachkov.learningtrackbackend.recommendation;

import hse.sachkov.learningtrackbackend.api.material.Material;
import hse.sachkov.learningtrackbackend.api.material.MaterialRepository;
import hse.sachkov.learningtrackbackend.api.material.UserMaterialLike;
import hse.sachkov.learningtrackbackend.api.material.article.Article;
import hse.sachkov.learningtrackbackend.api.material.course.Course;
import hse.sachkov.learningtrackbackend.api.material.vacancy.Vacancy;
import hse.sachkov.learningtrackbackend.api.track.Track;
import hse.sachkov.learningtrackbackend.api.track.TrackStep;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.ApiException;
import hse.sachkov.learningtrackbackend.recommendation.client.MatchingContentClient;
import hse.sachkov.learningtrackbackend.recommendation.client.SimilarUsersClient;
import hse.sachkov.learningtrackbackend.recommendation.dto.MatchingMaterialDTO;
import hse.sachkov.learningtrackbackend.recommendation.dto.UserSimilarityDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.BooleanPreference;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    // Max number of recommendations per track
    private static final int RECOMMENDATIONS_COUNT_MAX = 5;

    private final SimilarUsersClient similarUsersClient;
    private final MatchingContentClient matchingContentClient;

    private final MaterialRepository materialRepository;

    private List<String> extractUsernames(List<UserSimilarityDto> similarUsers) {
        return similarUsers
                .stream()
                .map(UserSimilarityDto::getUsername)
                .collect(Collectors.toList());
    }

    private List<RecommendedItem> mahoutRec(User userRequester, List<String> sourceUsersUsernames) {
        // Add a current userRequester
        sourceUsersUsernames.add(userRequester.getUsername());

        List<UserMaterialLike> allLikes = materialRepository.getAllLikesWhereUsernameIn(sourceUsersUsernames);
        FastByIDMap<Collection<Preference>> data = new FastByIDMap<>();

        for (UserMaterialLike userMaterialLike :
                allLikes) {
            if (!data.containsKey(userMaterialLike.getUserExternalId())) {
                data.put(userMaterialLike.getUserExternalId(), new ArrayList<>());
            }
            data.get(userMaterialLike.getUserExternalId()).add(new BooleanPreference(
                    userMaterialLike.getUserExternalId(),
                    userMaterialLike.getMaterialId()
            ));
        }
        // if current user has no likes, add him manually
        if (!data.containsKey(userRequester.getExternalId())) {
            data.put(userRequester.getExternalId(), new ArrayList<>());
        }

        FastByIDMap<PreferenceArray> userData = GenericDataModel.toDataMap(data, true);
        GenericDataModel res = new GenericDataModel(userData);
        try {
            CityBlockSimilarity similarity = new CityBlockSimilarity(res);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, res);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(res, neighborhood, similarity);

            // UserID and number of items to be recommended
            return recommender.recommend(userRequester.getExternalId(), RECOMMENDATIONS_COUNT_MAX * 5);
        } catch (Exception ex) {
            System.out.println("An exception occurred!");
        }
        return null;
    }

    /**
     * calculates similar users list if possible and generates recommendations
     *
     * @return recommendations for any or both similar users lists OR  0 items
     */
    private List<RecommendedItem> buildRecommendationsOnUserSimilarity(List<UserSimilarityDto> usersSimilarInSkills,
                                                                       List<UserSimilarityDto> usersSimilarInDesiredPosition,
                                                                       User userRequester) {
        List<RecommendedItem> recommendedItems = new ArrayList<>();
        List<UserSimilarityDto> similarUsers;
        // Step 1. Check for emptiness of lists of both similarities
        if (!CollectionUtils.isEmpty(usersSimilarInSkills) && !CollectionUtils.isEmpty(usersSimilarInDesiredPosition)) {
            // Step 2. If both lists have users then build the intersection
            similarUsers = usersSimilarInDesiredPosition.stream()
                    .distinct()
                    .filter(usersSimilarInSkills::contains)
                    .collect(Collectors.toList());
            List<RecommendedItem> similarUsersIntersectionRecommendations =
                    mahoutRec(userRequester, extractUsernames(similarUsers));

            // Step 3. Check the recommendations for intersection
            if (similarUsersIntersectionRecommendations == null || similarUsersIntersectionRecommendations.size() < RECOMMENDATIONS_COUNT_MAX) {
                // Step 4. If recommendation list for intersection contains < 5 values then build the union of initial users lists
                similarUsers = Stream.concat(usersSimilarInSkills.stream(), usersSimilarInDesiredPosition.stream())
                        .collect(Collectors.toList());

                List<RecommendedItem> similarUsersUnionRecommendations =
                        mahoutRec(userRequester, extractUsernames(similarUsers));
                // Step 5. Output any recommendations for union
                if (similarUsersUnionRecommendations != null) {
                    recommendedItems = similarUsersUnionRecommendations;
                }
            } else {
                // Step 6. If recommendation list for intersection contains >= 5 values then output the recommendations for intersection
                recommendedItems = similarUsersIntersectionRecommendations;
            }
        } else {
            // Step 7. If any of two initial lists is empty then try to find not empty one
            similarUsers = CollectionUtils.isEmpty(usersSimilarInSkills) ? usersSimilarInDesiredPosition : usersSimilarInSkills;
            if (!CollectionUtils.isEmpty(similarUsers)) {
                // Step 8. If at least one of the initial lists is not empty then output recommendations for it
                List<RecommendedItem> similarUsersRecommendations =
                        mahoutRec(userRequester, extractUsernames(similarUsers));
                if (similarUsersRecommendations != null) {
                    recommendedItems = similarUsersRecommendations;
                }
            }
        }
        return recommendedItems;
    }

    private List<Recommendation> mapRecommendedItemToMaterialRecommendation(List<RecommendedItem> recommendedItems) {
        List<Recommendation> materialRecommendations = new ArrayList<>();
        for (RecommendedItem recommendedItem : recommendedItems) { // for (long id : new long[]{1, 5}) {
            Optional<Material> learningMaterial = materialRepository.findById(recommendedItem.getItemID()); //.findById(id);
            if (learningMaterial.isEmpty()) {
                continue;
            }
            materialRecommendations.add(new Recommendation(
                    learningMaterial.get(),
                    recommendedItem.getValue() // 0.7
            ));
        }
        return materialRecommendations;
    }

    private List<Recommendation> mapMatchingMaterialDtoToMaterialRecommendation(List<MatchingMaterialDTO> recommendedMaterials) {
        List<Recommendation> materialRecommendations = new ArrayList<>();
        for (MatchingMaterialDTO recommendedItem : recommendedMaterials) { // for (long id : new long[]{10}) {
            Optional<Material> learningMaterial = materialRepository.findById(recommendedItem.getMaterialId()); //.findById(id);
            if (learningMaterial.isEmpty()) {
                continue;
            }
            materialRecommendations.add(new Recommendation(
                    learningMaterial.get(),
                    recommendedItem.getScore() // 0.7
            ));
        }
        return materialRecommendations;
    }

    private List<Recommendation> tryIntersectOrUnite(List<Recommendation> collaborativeFilteringRecommendations, List<Recommendation> contentBasedRecommendations) {
        List<Recommendation> recommendationsIntersection = new ArrayList<>();
        if (!CollectionUtils.isEmpty(collaborativeFilteringRecommendations) && !CollectionUtils.isEmpty(contentBasedRecommendations)) {
            recommendationsIntersection = contentBasedRecommendations.stream()
                    .distinct()
                    .filter(collaborativeFilteringRecommendations::contains)
                    .collect(Collectors.toList());
            // TODO: may be tried to sum the scores for better performance
            if (!CollectionUtils.isEmpty(recommendationsIntersection) && recommendationsIntersection.size() >= RECOMMENDATIONS_COUNT_MAX) {
                return recommendationsIntersection;
            }
        }
        // TODO: may be ordered by score (if user-based will stop returning 1.0)
        List<Recommendation> recommendationsConcatenation = Stream.concat(recommendationsIntersection.stream(),
                        Stream.concat(contentBasedRecommendations.stream(), collaborativeFilteringRecommendations.stream()))
                .distinct()
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(recommendationsConcatenation)) {
            return recommendationsConcatenation;
        }
        throw new ApiException("No recommendations were generated.", HttpStatus.NO_CONTENT);
    }

    private List<Recommendation> buildHybridRecommendations(List<Recommendation> collaborativeFilteringRecommendations,
                                                            List<Recommendation> contentBasedRecommendations) {
        List<Recommendation> hybridRecommendations = tryIntersectOrUnite(collaborativeFilteringRecommendations, contentBasedRecommendations);
        if (hybridRecommendations.size() <= RECOMMENDATIONS_COUNT_MAX) {
            return hybridRecommendations;
        }
        List<Recommendation> hybridRecommendationsPostProcessed;
        List<Recommendation> vacancies = new ArrayList<>();
        List<Recommendation> articles = new ArrayList<>();
        List<Recommendation> courses = new ArrayList<>();

        for (Recommendation recCandidate : hybridRecommendations) {
            if (recCandidate.getMaterial() instanceof Vacancy) {
                vacancies.add(recCandidate);
            } else if (recCandidate.getMaterial() instanceof Article) {
                articles.add(recCandidate);
            } else if (recCandidate.getMaterial() instanceof Course) {
                courses.add(recCandidate);
            }
        }
        if (CollectionUtils.isEmpty(articles) && CollectionUtils.isEmpty(courses)) {
            return hybridRecommendations.subList(0, RECOMMENDATIONS_COUNT_MAX);
        }
        if (CollectionUtils.isEmpty(articles) || CollectionUtils.isEmpty(courses)) {
            List<Recommendation> nonEmpty = CollectionUtils.isEmpty(articles) ? courses : articles;
            hybridRecommendationsPostProcessed = new ArrayList<>(nonEmpty.subList(0, Math.min(nonEmpty.size(), RECOMMENDATIONS_COUNT_MAX)));
            if (!CollectionUtils.isEmpty(vacancies)) {
                if (hybridRecommendationsPostProcessed.size() == RECOMMENDATIONS_COUNT_MAX) {
                    hybridRecommendationsPostProcessed.set(RECOMMENDATIONS_COUNT_MAX - 1, vacancies.get(0));
                } else {
                    hybridRecommendationsPostProcessed.add(vacancies.get(0));
                }
            }
        } else {
            hybridRecommendationsPostProcessed = new ArrayList<>(articles.subList(0, Math.min(articles.size(), RECOMMENDATIONS_COUNT_MAX / 2)));
            hybridRecommendationsPostProcessed.addAll(courses.subList(0, Math.min(courses.size(), RECOMMENDATIONS_COUNT_MAX - hybridRecommendationsPostProcessed.size() - 1)));
            if (!CollectionUtils.isEmpty(vacancies)) {
                hybridRecommendationsPostProcessed.add(vacancies.get(0));
            }
            for (int i = hybridRecommendationsPostProcessed.size(); i < Math.min(courses.size(), RECOMMENDATIONS_COUNT_MAX); i++) {
                hybridRecommendationsPostProcessed.add(courses.get(i));
            }
            for (int i = hybridRecommendationsPostProcessed.size(); i < Math.min(articles.size(), RECOMMENDATIONS_COUNT_MAX); i++) {
                hybridRecommendationsPostProcessed.add(0, articles.get(i));
            }

        }
        return hybridRecommendationsPostProcessed;
    }

    private List<TrackStep> generateTrackSteps(Track track) {
        List<Material> materials = (List<Material>) materialRepository.findAll();

        List<TrackStep> trackSteps = new ArrayList<>();
        for (int i = 0; i < RandomUtils.nextInt(2, 6); i++) {
            trackSteps.add(
                    new TrackStep(
                            null,
                            (long) RandomUtils.nextInt(0, 10),
                            false,
                            materials.get(RandomUtils.nextInt(0, materials.size())),
                            track
                    )
            );
        }
        return trackSteps;
    }

    private Track buildTrack(User user, List<Material> learningMaterials) {
        Track track = new Track();
        track.setDestination(user.getDesiredPosition());
        track.setUser(user);
        track.setCreationDate(new Date());
        if (learningMaterials == null) {
            track.setTrackSteps(generateTrackSteps(track));
        } else {
            List<TrackStep> trackSteps = new ArrayList<>();
            for (int i = 0; i < learningMaterials.size(); i++) {
                trackSteps.add(
                        new TrackStep(
                                null,
                                (long) i,
                                false,
                                learningMaterials.get(i),
                                track
                        )
                );
            }
            track.setTrackSteps(trackSteps);
        }

        return track;
    }

    public Track generateTrack(User user) {
        List<UserSimilarityDto> usersSimilarInSkills = similarUsersClient.requestSimilarUsersInSkill(user);
        List<UserSimilarityDto> usersSimilarInDesiredPosition = similarUsersClient.requestSimilarUsersInDesiredPosition(user);

        List<RecommendedItem> recommendedItemsOnUserSimilarity = buildRecommendationsOnUserSimilarity(usersSimilarInSkills, usersSimilarInDesiredPosition, user);
        List<Recommendation> collaborativeFilteringRecommendations = mapRecommendedItemToMaterialRecommendation(recommendedItemsOnUserSimilarity);

        List<MatchingMaterialDTO> recommendedMaterialsOnContentMatch = matchingContentClient.requestMatchingContent(user);
        List<Recommendation> contentBasedRecommendations = mapMatchingMaterialDtoToMaterialRecommendation(recommendedMaterialsOnContentMatch);

        List<Recommendation> recommendations = buildHybridRecommendations(collaborativeFilteringRecommendations, contentBasedRecommendations);

        return buildTrack(user, recommendations.stream().map(Recommendation::getMaterial).collect(Collectors.toList()));
    }
}
