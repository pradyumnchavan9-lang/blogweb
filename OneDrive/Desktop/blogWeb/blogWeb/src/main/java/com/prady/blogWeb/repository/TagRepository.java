package com.prady.blogWeb.repository;

import com.prady.blogWeb.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    public Optional<Tag> findTagById(Long Id);
}
