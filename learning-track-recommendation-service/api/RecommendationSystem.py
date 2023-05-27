import pymorphy2
import pandas as pd

from nltk.corpus import stopwords

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity

from core.models import User, Material


def create_similarity_matrix(descriptions, metric='linear_kernel'):
    # Define a tfidf vectorizer and remove all stopwords:
    tfidf = TfidfVectorizer(stop_words=stopwords.words('english') + stopwords.words('russian'))

    # Convert tfidf matrix by fitting and transforming the data:
    tfidf_matrix = tfidf.fit_transform(descriptions)

    if metric == 'cosine_similarity':
        # Calculating and return the cosine similarity matrix:
        return cosine_similarity(tfidf_matrix)

    # Calculating and return the linear kernel matrix:
    return linear_kernel(tfidf_matrix, tfidf_matrix)


def get_recommendations(data, descriptions, metric='linear_kernel'):
    # Create the similarity matrix:
    cosine_sim = create_similarity_matrix(descriptions, metric)

    # Get pairwise similarity scores of all the users with new user:
    sim_scores = list(enumerate(cosine_sim[-1][:-1]))

    # Sort the descriptions based on similarity score:
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

    # Get the user indices:
    indices = [i[0] for i in sim_scores]

    return pd.concat(
        [data.iloc[indices], pd.Series([i[1] for i in sim_scores], name='score', index=[i[0] for i in sim_scores])],
        axis=1,
        ignore_index=False
    )


def get_similar_users_by_property(property_name, user_skills, other_users, threshold=0.3):
    data = pd.DataFrame.from_records(other_users)

    descriptions = pd.concat([data[property_name], pd.Series(user_skills)], ignore_index=True)

    recommended_users = get_recommendations(data, descriptions)
    recommended_users = recommended_users[recommended_users['score'] >= threshold]

    return [User(row['username'], row['external_id'], row['score']) for index, row in recommended_users.iterrows()]


def get_suitable_courses(user_description, materials, threshold=0.1):
    morph = pymorphy2.MorphAnalyzer()
    pd.options.mode.chained_assignment = None

    materials = pd.DataFrame.from_records(materials)
    for i in range(len(materials['overview'])):
        materials['overview'][i] = ' '.join(
            [morph.parse(word)[0].normal_form for word in materials['overview'][i].split(' ')])

    materials_description = materials['overview']
    user_description = pd.Series(' '.join([morph.parse(word)[0].normal_form for word in user_description.split(' ')]))
    overall_descriptions = pd.concat([materials_description, user_description])

    recommendations = get_recommendations(materials, overall_descriptions, 'cosine_similarity')
    recommendations = recommendations[recommendations['score'] >= threshold]

    return [Material(row['id'], row['score']) for index, row in recommendations.iterrows()]
