package com.prady.blogWeb.mapper;

import com.prady.blogWeb.dto.response.ProblemResponse;
import com.prady.blogWeb.entity.Problem;

public class ProblemMapper {

    public static ProblemResponse problemToProblemResponse(Problem problem){

        ProblemResponse problemResponse = new ProblemResponse();
        problemResponse.setId(problem.getId());
        problemResponse.setTitle(problem.getTitle());
        problemResponse.setDifficulty(problem.getDifficulty());
        problemResponse.setPlatform(problem.getPlatform());

        return problemResponse;
    }
}
