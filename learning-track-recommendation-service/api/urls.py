from django.urls import path
from . import views

urlpatterns = [
    path('similar_users_by_skill/', views.similar_users_by_skills),
    path('similar_users_by_cherished_position/', views.similar_users_by_cherished_position),
    path('suitable_courses/', views.suitable_courses),
]
