package com.apostal.nonogramsolver.service.solver;

import java.util.List;

public abstract class Algorithm implements Runnable{
    public abstract void init(List<List<Integer>> leftLengths, List<List<Integer>> upLengths, int width, int height);
    public abstract int[][] getStateMatrix();
    public abstract boolean isSolved();
}
