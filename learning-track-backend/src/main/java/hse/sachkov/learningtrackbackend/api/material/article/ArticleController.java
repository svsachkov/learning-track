package hse.sachkov.learningtrackbackend.api.material.article;

import com.google.common.collect.Lists;

import hse.sachkov.learningtrackbackend.api.material.Material;
import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserRepository;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("article")
public class ArticleController {

    private final ArticleService articleService;

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @GetMapping("/list")
    public List<UserArticle> getArticleList(Principal userRequester) {
        log.info("Start processing GET /article/list request . . .");

        ArrayList<Article> articles = Lists.newArrayList(articleRepository.findAllByOrderByDateDesc());

        Set<Material> readArticles = new HashSet<>();
        if (userRequester != null) {
            User user = userRepository.findByUsername(userRequester.getName()).orElse(null);
            readArticles = user.getMaterialsCompleted();
        }

        List<UserArticle> response = new ArrayList<>();

        for (Article article : articles) {
            response.add(articleService.mapArticleToDto(article, readArticles));
        }

        log.info("GET /article/list request processed!");

        return response;
    }

    @GetMapping("/{id}")
    public UserArticle getArticleById(Principal userRequester, @PathVariable Long id) {
        log.info("Start processing GET /article/{id} request . . .");

        Optional<Article> articleOptional = articleRepository.findById(id);

        if (articleOptional.isEmpty()) {
            throw new EntityNotFoundException("Nothing found with provided id.");
        }

        Article article = articleOptional.get();

        Set<Material> readArticles = new HashSet<>();
        if (userRequester != null) {
            User user = userRepository.findByUsername(userRequester.getName()).orElse(null);
            readArticles = user.getMaterialsCompleted();
            article.setLiked(article.getLikedUsers().contains(user));
        }

        UserArticle userArticle = articleService.mapArticleToDto(article, readArticles);

        log.info("GET /article/{id} request processed!");

        return userArticle;
    }
}
