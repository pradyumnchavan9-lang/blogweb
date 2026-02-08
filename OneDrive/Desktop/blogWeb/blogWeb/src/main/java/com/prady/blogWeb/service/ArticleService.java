package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.CreateArticle;
import com.prady.blogWeb.dto.request.UpdateArticle;
import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.dto.response.CommentResponse;
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

    public ArticleService(ArticleMapper articleMapper,UserRepository userRepository
            ,ArticleRepository articleRepository, TagRepository tagRepository
    ,CommentRepository commentRepository, CommentMapper commentMapper) {
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }


    //Get Article
    public ArticleResponse getArticle(Long id){

        Article article = articleRepository.findById(id).orElse(null);
        List<Comment>  comments = commentRepository.findAllByArticleId(id);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for(Comment comment : comments){
            commentResponses.add(commentMapper.commentToCommentResponse(comment));
        }
        ArticleResponse articleResponse = articleMapper.articleToArticleResponse(article);
        articleResponse.setComments(commentResponses);
        return articleResponse;
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


    //Get all Articles (Pagination)
    public Page<ArticleResponse> getAllArticles(@PageableDefault(page = 0,size = 10) Pageable pageable){

        return articleRepository.findAll(pageable)
                .map(article -> articleMapper.articleToArticleResponse(article));
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
}
