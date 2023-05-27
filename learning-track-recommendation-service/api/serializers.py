from rest_framework import serializers

from api.consts import *

from core.models import User, SimilarUsersResponse, Material, MatchingMaterialsResponse


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [USERNAME, EXTERNAL_ID, SCORE]


class SimilarUserResponseSerializer(serializers.ModelSerializer):
    class Meta:
        model = SimilarUsersResponse
        fields = [SIMILAR_USERS]
        serializers = UserSerializer


class MaterialSerializer(serializers.ModelSerializer):
    class Meta:
        model = Material
        fields = [MATERIAL_ID, SCORE]


class MatchingMaterialsResponseSerializer(serializers.ModelSerializer):
    class Meta:
        model = MatchingMaterialsResponse
        fields = [MATCHING_MATERIALS]
        serializers = MaterialSerializer
