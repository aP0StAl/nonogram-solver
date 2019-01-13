package com.apostal.nonogramsolver.service.solver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NonogramSolverService {
    private Algorithm algorithm;

    public void start(List<List<Integer>> leftLengths, List<List<Integer>> upLengths, int width, int height){
        algorithm = new FastSolver();
        algorithm.init(leftLengths, upLengths, width, height);
        Thread thread = new Thread(algorithm);
        thread.start();
    }

    public int[][] getResult(){
        return algorithm.getStateMatrix();
    }

    public boolean isSolved(){
        return algorithm.isSolved();
    }
}
