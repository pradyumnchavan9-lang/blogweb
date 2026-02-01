package com.prady.blogWeb.controller;


import com.prady.blogWeb.dto.request.CreateArticle;
import com.prady.blogWeb.dto.request.UpdateArticle;
import com.prady.blogWeb.dto.response.ArticleResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    //Get Article
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id){

        return ResponseEntity.ok().body(articleService.getArticle(id));
    }


    //Create Article
    @PostMapping("")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody CreateArticle createArticle){

        return ResponseEntity.ok().body(articleService.create(createArticle));
    }

    //Update Article
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id,@RequestBody UpdateArticle updateArticle){

        return ResponseEntity.ok().body(articleService.updateArticleById(id,updateArticle));
    }

    //Delete Article
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteArticle(@PathVariable Long id){

        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }


    //Get Articles(Pagination :- Only a set of articles)
    @GetMapping("")
    public ResponseEntity<Page<ArticleResponse>> getAllArticles(@PageableDefault(page = 0,size = 10) Pageable pageable){

            return ResponseEntity.ok().body(articleService.getAllArticles(pageable));
    }


}
