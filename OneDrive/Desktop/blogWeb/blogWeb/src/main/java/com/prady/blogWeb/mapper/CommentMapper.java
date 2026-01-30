package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.request.CreateComment;
import com.prady.blogWeb.dto.response.CommentResponse;
import com.prady.blogWeb.entity.Comment;

public class CommentMapper {

    public static Comment createCommentToComment(CreateComment createComment) {

        Comment comment = new Comment();
        comment.setContent(createComment.getContent());
        return comment;
    }

    public static CommentResponse commentToCommentResponse(Comment comment) {

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        if(comment.getUser() != null) {
            commentResponse.setAuthor(
                    UserMapper.userToAuthorResponse(comment.getUser())
            );
        }
        return commentResponse;
    }
}
