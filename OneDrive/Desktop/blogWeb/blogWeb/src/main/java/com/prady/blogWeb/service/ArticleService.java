package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.CreateArticle;
import com.prady.blogWeb.dto.request.UpdateArticle;
import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.Tag;
import com.prady.blogWeb.entity.User;
import com.prady.blogWeb.mapper.ArticleMapper;
import com.prady.blogWeb.repository.ArticleRepository;
import com.prady.blogWeb.repository.TagRepository;
import com.prady.blogWeb.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    public ArticleService(ArticleMapper articleMapper,UserRepository userRepository
            ,ArticleRepository articleRepository, TagRepository tagRepository) {
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
    }


    //Get Article
    public ArticleResponse getArticle(Long id){

        Article article = articleRepository.findById(id).orElse(null);
        return articleMapper.articleToArticleResponse(article);
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

         ArticleResponse articleResponse = articleMapper.articleToArticleResponse(article);
         return articleResponse;

    }

    //Update article
    public ArticleResponse updateArticleById(Long id, UpdateArticle updateArticle){

        Article article = articleRepository.findById(id).get();

        //Check if logged-in user and articles author match
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if(article.getUser().equals(user)){
            article = articleMapper.updateArticleToArticle(updateArticle,article);
            articleRepository.save(article);
        }

        return articleMapper.articleToArticleResponse(article);
    }

    //Delete Article
    public void deleteArticle(Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Article article = articleRepository.findById(id).get();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user != null && article.getUser().equals(user)){
            articleRepository.deleteById(id);
        }
    }


    //Get all Articles (Pagination)
    public Page<ArticleResponse> getAllArticles(@PageableDefault(page = 0,size = 10) Pageable pageable){

        return articleRepository.findAll(pageable)
                .map(article -> articleMapper.articleToArticleResponse(article));
    }
}
