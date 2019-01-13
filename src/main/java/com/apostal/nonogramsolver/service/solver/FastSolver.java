package com.apostal.nonogramsolver.service.solver;

import com.apostal.nonogramsolver.util.Const;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class FastSolver extends Algorithm {
    private int[][] stateMatrix;
    private int[][] reverseStateMatrix;
    private List<Row> leftRows;
    private Row[] leftRowsArray;
    private List<Row> upRows;
    private Row[] upRowsArray;
    private int[][][] calc;
    @Getter
    private boolean isSolved;

    @Override
    public void init(List<List<Integer>> leftLengths, List<List<Integer>> upLengths, int width, int height) {
        isSolved = false;
        int maxFitnessLeft = 0;
        int maxBlocksLen = 0;
        for (List<Integer> row : leftLengths) {
            for (Integer length : row)
                maxFitnessLeft += length;
            maxBlocksLen = Math.max(row.size(), maxBlocksLen);
        }
        int maxFitnessUp = 0;
        for (List<Integer> row : upLengths) {
            for (Integer length : row)
                maxFitnessUp += length;
            maxBlocksLen = Math.max(row.size(), maxBlocksLen);
        }

        if (maxFitnessLeft != maxFitnessUp) {
            log.error(Const.Pattern.LOG, Const.Messages.INCORRECT_INPUT_DATA_ERROR, maxFitnessLeft + " " + maxFitnessUp);
            return;
        }

        leftRows = new ArrayList<>();
        upRows = new ArrayList<>();
        leftRowsArray = new Row[leftLengths.size()];
        upRowsArray = new Row[upLengths.size()];
        stateMatrix = new int[leftLengths.size()][upLengths.size()];
        reverseStateMatrix = new int[upLengths.size()][leftLengths.size()];
        calc = new int[Math.max(height, width)][maxBlocksLen+1][2];
        for(int i=0;i<leftLengths.size();i++) {
            Row row = new Row(i, width, leftLengths.get(i).stream().mapToInt(Integer::intValue).toArray());
            leftRows.add(row);
            leftRowsArray[i] = row;
        }
        for(int i=0;i<upLengths.size();i++) {
            Row row = new Row(i, height, upLengths.get(i).stream().mapToInt(Integer::intValue).toArray());
            upRows.add(row);
            upRowsArray[i] = row;
        }

    }

    @Override
    public int[][] getStateMatrix() {
        return stateMatrix;
    }

    @Override
    public void run() {
        long timeStart = System.nanoTime();
        boolean[] isFinished = new boolean[2];
        while (!(isFinished[0] && isFinished[1])){
            for(int i=0;i<2;i++){
                List<Row> rows = i == 0 ? leftRows : upRows;
                int[][] matrix = i == 0 ? stateMatrix : reverseStateMatrix;
                int[][] reverseMatrix = i == 0 ? reverseStateMatrix : stateMatrix;
                Row[] otherArray = i == 0 ? upRowsArray : leftRowsArray;
                isFinished[i] = true;
                Collections.sort(rows);
                for (Row row : rows) {
                    if(!row.isUpdated()) continue;
                    int[] state = matrix[row.getRow()];
                    boolean status = row.solve(state, calc);
                    if(status){
                        for(int j=0;j<state.length;j++){
                            if(state[j] != 0 && reverseMatrix[j][row.getRow()] != state[j]){
                                reverseMatrix[j][row.getRow()] = state[j];
                                otherArray[j].setUpdated(true);
                            }
                        }
                        isFinished[i] = false;
                    }
                }
            }
        }
        long timeFinish = System.nanoTime();
        log.info(Const.Pattern.LOG+" ms.", Const.Messages.SOLVING_DONE_INFO, (timeFinish - timeStart)/1e6);
        isSolved = true;
    }
}
