package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.response.ArticleIdResponse;
import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.dto.response.CommentResponse;
import com.prady.blogWeb.dto.response.TagResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.Comment;
import com.prady.blogWeb.entity.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArticleMapper {

    public static ArticleIdResponse articleToArticleIdResponse(Article article){

        ArticleIdResponse articleIdResponse = new ArticleIdResponse();
        articleIdResponse.setId(article.getId());
        return articleIdResponse;
    }

    public static ArticleResponse articleToArticleResponse(Article article){

        ArticleResponse articleResponse = new ArticleResponse();
        articleResponse.setId(article.getId());
        articleResponse.setTitle(article.getTitle());
        articleResponse.setArticleType(article.getArticleType());
        articleResponse.setContent(article.getContent());
        articleResponse.setDifficulty(article.getDifficulty());
        articleResponse.setCreatedAt(article.getCreatedAt());
        articleResponse.setUpdatedAt(article.getUpdatedAt());
        articleResponse.setSummary(article.getSummary());

        //For getting set of tagResponses
        Set<TagResponse> tagResponses = new HashSet<>();
        Set<Tag> tags = article.getTags();
        if(tags != null && !tags.isEmpty()){
            for(Tag tag : tags){
                tagResponses.add(TagMapper.tagToTagResponse(tag));
            }
        }
        articleResponse.setTags(tagResponses);

        //For getting author response
        if (article.getUser() != null) {
            articleResponse.setAuthor(
                    UserMapper.userToAuthorResponse(article.getUser())
            );
        }


        //For getting list of commentResponses
        List<CommentResponse> commentResponses = new ArrayList<>();
        List<Comment> comments = article.getComments();
        if(comments != null && !comments.isEmpty()){
            for(Comment comment : comments){
                commentResponses.add(CommentMapper.commentToCommentResponse(comment));
            }
        }
        articleResponse.setComments(commentResponses);

        //For getting problem
        if (article.getProblem() != null) {
            articleResponse.setProblem(
                    ProblemMapper.problemToProblemResponse(article.getProblem())
            );
        }



        return articleResponse;

    }
}
