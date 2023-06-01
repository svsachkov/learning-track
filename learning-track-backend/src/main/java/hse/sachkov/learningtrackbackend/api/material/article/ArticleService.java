package hse.sachkov.learningtrackbackend.api.material.article;

import hse.sachkov.learningtrackbackend.api.material.Material;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class ArticleService {

    public UserArticle mapArticleToDto(Article article, Set<Material> readArticles) {
        UserArticle userArticle = new UserArticle();
        userArticle.setCategory(article.getCategory());
        userArticle.setDate(article.getDate());
        userArticle.setDescription(article.getDescription());
        userArticle.setId(article.getId());
        userArticle.setContent(article.getContent());
        userArticle.setTags(article.getTags());
        userArticle.setTitle(article.getTitle());
        userArticle.setLiked(article.getLiked());
        userArticle.setRead(readArticles.stream().anyMatch(x -> Objects.equals(x.getId(), article.getId())));
        return userArticle;
    }
}
