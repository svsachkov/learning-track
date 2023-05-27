from api.consts import *


def parse_request_similar_users_by(request):
    user_skills = request.data.get(TARGET_USER).get(SKILL_SET)
    user_cherished_position = request.data.get(TARGET_USER).get(DESIRED_POSITION)

    other_users = [
        {
            'username': x.get(USERNAME),
            'external_id': x.get(EXTERNAL_ID),
            'skill_set': x.get(SKILL_SET),
            'desired_position': x.get(DESIRED_POSITION)
        } for x in request.data.get(OTHER_USERS)
    ]

    threshold = request.data.get(THRESHOLD)
    if threshold is None:
        threshold = 0

    return user_skills, user_cherished_position, other_users, threshold


def parse_request_suitable_courses(request):
    target_user_description = request.data.get(TARGET_USER).get(DESCRIPTION)

    materials = [
        {
            'id': x.get(ID),
            'overview': x.get(OVERVIEW)
        } for x in request.data.get(MATERIALS)
    ]

    return target_user_description, materials
