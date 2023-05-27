from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view

from .helpers import *
from .serializers import *

from . import RecommendationSystem


@api_view(['POST'])
def similar_users_by_skills(request):
    similar_users_response = SimilarUsersResponse()

    try:
        user_skills, _, other_users, threshold = parse_request_similar_users_by(request)
        similar_users_response.similarUsers = RecommendationSystem.get_similar_users_by_property(
            'skill_set',
            user_skills,
            other_users,
            threshold
        )
        similar_users_response.similarUsers = [UserSerializer(x).data for x in similar_users_response.similarUsers]
    except Exception as e:
        return Response(str(e), status=status.HTTP_400_BAD_REQUEST)

    return Response(SimilarUserResponseSerializer(similar_users_response).data, status=status.HTTP_200_OK)


@api_view(['POST'])
def similar_users_by_cherished_position(request):
    similar_users_response = SimilarUsersResponse()

    try:
        _, user_cherished_position, other_users, threshold = parse_request_similar_users_by(request)
        similar_users_response.similarUsers = RecommendationSystem.get_similar_users_by_property(
            'desired_position',
            user_cherished_position,
            other_users,
            threshold
        )
        similar_users_response.similarUsers = [UserSerializer(x).data for x in similar_users_response.similarUsers]
    except Exception as e:
        return Response(str(e), status=status.HTTP_400_BAD_REQUEST)

    return Response(SimilarUserResponseSerializer(similar_users_response).data, status=status.HTTP_200_OK)


@api_view(['POST'])
def suitable_courses(request):
    response = MatchingMaterialsResponse()

    try:
        target_user_description, materials = parse_request_suitable_courses(request)
        response.matchingMaterials = RecommendationSystem.get_suitable_courses(target_user_description, materials)
        response.matchingMaterials = [MaterialSerializer(x).data for x in response.matchingMaterials]
    except Exception as e:
        return Response(str(e), status=status.HTTP_400_BAD_REQUEST)

    return Response(MatchingMaterialsResponseSerializer(response).data, status=status.HTTP_200_OK)
