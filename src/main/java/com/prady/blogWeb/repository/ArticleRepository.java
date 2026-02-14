package com.prady.blogWeb.repository;


import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    Page<Article> findAll(Pageable pageable);
    Page<Article> findAllByUser(User user, Pageable pageable);
    @Modifying
    @Query("UPDATE Article a SET a.views = a.views + :views WHERE a.id = :id")
    void incrementViews(@Param("id") Long id, @Param("views") long views);

}
