from django.db import models


class User(models.Model):
    username = models.TextField(null=True)
    externalId = models.IntegerField(null=True)
    score = models.FloatField(null=True)

    def __str__(self):
        return self.username + ": " + str(self.score)

    def __init__(self, username="", external_id=0, score=0, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.username = username
        self.externalId = external_id
        self.score = score


class SimilarUsersResponse(models.Model):
    similarUsers = []

    def __str__(self):
        return '1'


class Material(models.Model):
    materialId = models.IntegerField(null=True)
    score = models.FloatField(null=True)

    def __init__(self, material_id=0, score=0, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.materialId = material_id
        self.score = score


class MatchingMaterialsResponse(models.Model):
    matchingMaterials = []

    def __str__(self):
        return '1'
