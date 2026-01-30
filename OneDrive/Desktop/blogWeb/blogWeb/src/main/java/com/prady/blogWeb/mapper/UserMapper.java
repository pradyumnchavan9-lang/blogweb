package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.request.CreateUser;
import com.prady.blogWeb.dto.response.ArticleIdResponse;
import com.prady.blogWeb.dto.response.AuthorResponse;
import com.prady.blogWeb.dto.response.UserResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {


    public static User createUserToUser(CreateUser user){

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        return newUser;
    }

    public static AuthorResponse userToAuthorResponse(User user){

        AuthorResponse authorResponse = new AuthorResponse();
        authorResponse.setId(user.getId());
        authorResponse.setUsername(user.getUsername());
        return authorResponse;
    }

    public static UserResponse userToUserResponse(User user){

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());

        List<ArticleIdResponse> articlesIdResponse = new ArrayList<>();
        List<Article> articles = user.getArticles();
        if(articles != null && !articles.isEmpty()) {
            for (Article article : articles) {
                articlesIdResponse.add(ArticleMapper.articleToArticleIdResponse(article));
            }
        }
        userResponse.setArticles(articlesIdResponse);
        return userResponse;
    }
}
