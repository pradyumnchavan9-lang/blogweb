package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.response.TagResponse;
import com.prady.blogWeb.entity.Tag;

public class TagMapper {

    public static TagResponse tagToTagResponse(Tag tag){

        TagResponse tagResponse = new TagResponse();
        tagResponse.setId(tag.getId());
        tagResponse.setName(tag.getName());
        return tagResponse;
    }
}
