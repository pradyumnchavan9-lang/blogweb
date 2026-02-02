package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.CreateProblem;
import com.prady.blogWeb.dto.response.ProblemResponse;
import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.entity.Problem;
import com.prady.blogWeb.exception.ResourceNotFoundException;
import com.prady.blogWeb.mapper.ProblemMapper;
import com.prady.blogWeb.repository.ArticleRepository;
import com.prady.blogWeb.repository.ProblemRepository;
import org.springframework.stereotype.Service;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;
    private final ArticleRepository articleRepository;

    public ProblemService(ProblemRepository problemRepository, ProblemMapper problemMapper,
                          ArticleRepository articleRepository) {
        this.problemRepository = problemRepository;
        this.problemMapper = problemMapper;
        this.articleRepository = articleRepository;
    }

    public ProblemResponse createProblem(Long articleId, CreateProblem createProblem){

        Problem problem = problemRepository.findByPlatformAndPlatformProblemId(
                createProblem.getPlatform(),
                createProblem.getPlatformProblemId()
        ).orElseGet(() ->{
            Problem p = problemMapper.createProblemToProblem(createProblem);
            return problemRepository.save(p);

        });

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + articleId));
        article.setProblem(problem);
        articleRepository.save(article);
        return problemMapper.problemToProblemResponse(problem);
    }
}
