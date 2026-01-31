package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.response.TagResponse;
import com.prady.blogWeb.entity.Tag;
import org.springframework.stereotype.Component;


@Component
public class TagMapper {

    public  TagResponse tagToTagResponse(Tag tag){

        TagResponse tagResponse = new TagResponse();
        tagResponse.setId(tag.getId());
        tagResponse.setName(tag.getName());
        return tagResponse;
    }
}
