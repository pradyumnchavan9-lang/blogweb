package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.CreateTag;
import com.prady.blogWeb.dto.response.TagResponse;
import com.prady.blogWeb.entity.Tag;
import com.prady.blogWeb.exception.ResourceAlreadyExistsException;
import com.prady.blogWeb.mapper.TagMapper;
import com.prady.blogWeb.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagMapper tagMapper;
    private final TagRepository tagRepository;

    public TagService(TagMapper tagMapper, TagRepository tagRepository) {
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }


    //Create Tag
    public TagResponse buildTag(CreateTag createTag){

        tagRepository.findByNameAndCategory(
                createTag.getName(),
                createTag.getCategory()
        ).ifPresent(tag -> {
            throw new ResourceAlreadyExistsException(
                    "Tag already exists " + createTag.getName()
            );
        });

        Tag tag = tagMapper.createTagToTag(createTag);
        tagRepository.save(tag);

        return tagMapper.tagToTagResponse(tag);
    }

    //Get all tags
    public Page<TagResponse> getAllTags(Pageable pageable){
        return tagRepository.findAll(pageable).map(tag -> tagMapper.tagToTagResponse(tag));
    }

}
