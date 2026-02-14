package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.CreateArticle;
import com.prady.blogWeb.dto.request.UpdateArticle;
import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.dto.response.CommentResponse;
import com.prady.blogWeb.dto.response.PageResponseDto;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.Comment;
import com.prady.blogWeb.entity.Tag;
import com.prady.blogWeb.entity.User;
import com.prady.blogWeb.exception.ResourceNotFoundException;
import com.prady.blogWeb.exception.UnauthorizedActionException;
import com.prady.blogWeb.mapper.ArticleMapper;
import com.prady.blogWeb.mapper.CommentMapper;
import com.prady.blogWeb.repository.ArticleRepository;
import com.prady.blogWeb.repository.CommentRepository;
import com.prady.blogWeb.repository.TagRepository;
import com.prady.blogWeb.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleService {



    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final RedisService redisService;


    public ArticleService(ArticleMapper articleMapper,UserRepository userRepository
            ,ArticleRepository articleRepository, TagRepository tagRepository
            ,CommentRepository commentRepository, CommentMapper commentMapper
            ,RedisService redisService) {
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.redisService = redisService;
    }


    //Get Article from user
    public PageResponseDto<ArticleResponse> getArticle(Long userId,Pageable pageable){

        String key = "articles-userId:" + userId
                    + "page:" + pageable.getPageNumber()
                    + "size:" + pageable.getPageSize();
        PageResponseDto<ArticleResponse> cache = redisService.get(key,PageResponseDto.class);
        if(cache != null){
            return cache;
        }
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Article> articlesPage = articleRepository.findAllByUser(user,pageable);
        Page<ArticleResponse> articles = articlesPage.map(articleMapper::articleToArticleResponse);
        PageResponseDto<ArticleResponse> dto = new PageResponseDto<>();
        try
        {
            dto.setContent(articles.getContent());
            dto.setPage(articles.getNumber());
            dto.setSize(articles.getSize());
            dto.setTotalElements(articles.getTotalElements());
            dto.setTotalPages(articles.getTotalPages());
            redisService.set(key,dto,300l);

        }catch (Exception e){
            System.err.println("Error in getting articles");
        }
        return dto;
    }


    //Create Article
    public ArticleResponse create(CreateArticle createArticle){

        Article article = articleMapper.createArticleToArticle(createArticle);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null) {
            String username = auth.getName();
            Optional<User> user = userRepository.findByUsername(username);
            if(user != null && user.isPresent()){
                article.setUser(user.get());
            }
        }

        Set<Tag> tags = new HashSet<>();
        for(Long tagId : createArticle.getTagIds()) {
            Tag tag = tagRepository.findById(tagId).orElse(null);
            if (tag != null) {
                tags.add(tag);
            }
        }
        article.setTags(tags);

         articleRepository.save(article);
         redisService.deleteByPrefix("get-articles:");
         redisService.deleteByPrefix("articles-userId");
         ArticleResponse articleResponse = articleMapper.articleToArticleResponse(article);
         return articleResponse;

    }

    //Update article
    public ArticleResponse updateArticleById(Long id, UpdateArticle updateArticle){

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article not found with id: " + id
                ));

        //Check if logged-in user and articles author match
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with name: " + username
                        )
                );

        if(article.getUser().equals(user) || user.getRole().equals("ADMIN")){
            article = articleMapper.updateArticleToArticle(updateArticle,article);
            articleRepository.save(article);
        }else{
            throw new UnauthorizedActionException(
                    "User doesn't have permission to edit this article"
            );
        }

        return articleMapper.articleToArticleResponse(article);
    }

    //Delete Article
    public void deleteArticle(Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article not found with id: " + id
                ));
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user != null && (article.getUser().equals(user) || user.getRole().equals("ADMIN"))){
            articleRepository.deleteById(id);
        }else{
            throw new UnauthorizedActionException(
                    "User doesn't have permission to delete this article"
            );
        }
    }


    //Get articles
    public PageResponseDto<ArticleResponse> getAllArticles(Pageable pageable) {

        String key = "get-articles:page" + pageable.getPageNumber()
                + ":size" + pageable.getPageSize();

        PageResponseDto<ArticleResponse> cached =
                redisService.get(key, PageResponseDto.class);

        if (cached != null) {
            return cached;
        }

        Page<ArticleResponse> articles =
                articleRepository.findAll(pageable)
                        .map(article -> articleMapper.articleToArticleResponse(article));

        PageResponseDto<ArticleResponse> dto = new PageResponseDto<>();
        dto.setContent(articles.getContent());
        dto.setPage(articles.getNumber());
        dto.setSize(articles.getSize());
        dto.setTotalElements(articles.getTotalElements());
        dto.setTotalPages(articles.getTotalPages());

        redisService.set(key, dto, 300L);

        return dto;
    }


    //Add Tag to Article
    public void addTagToArticle(Long id,Long tagId){

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag Not Found with id "+ tagId
                ));

        Article article =articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article Not Found with id "+ id
                ));

        article.addTag(tag);
        articleRepository.save(article);
    }


    //Get Article By ID
    public ArticleResponse getArticleById(Long articleId) {

        String key = "get-article-by-id:" + articleId;
        String key2 = "article-views:" + articleId;

        Long views = redisService.getViews(key2);
        views = (views != null) ? views : 0;

        ArticleResponse cache =  redisService.get(key,ArticleResponse.class);
        if(cache != null){
            cache.setViews(views + cache.getViews());
            return cache;
        }


        cache =  articleMapper.articleToArticleResponse(articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article not found with id: " + articleId
                )));
        redisService.set(key,cache,300L);
        return cache;
    }
}
