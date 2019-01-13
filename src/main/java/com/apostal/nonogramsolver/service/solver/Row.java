package com.apostal.nonogramsolver.service.solver;

import lombok.Data;

import javax.annotation.Nonnull;

@Data
public class Row implements Comparable<Row>{
    private int row;
    private int startPosition;
    private int length;
    private int lastPositionPlusOne;
    private int[] blocks;
    private int firstBlock;
    private int lastBlockPlusOne;
    private int sumBlocks;
    private int diff;
    private boolean isUpdated;
    private boolean isSolved;

    private int[][][] calc;
    private int[] white;
    private int[] black;
    private int[] canWhite;
    private int[] canBlack;

    Row(int row, int length, int[] blocks) {
        this.row = row;
        this.startPosition = 0;
        this.length = length;
        this.lastPositionPlusOne = length;
        this.blocks = blocks;
        this.firstBlock = 0;
        this.lastBlockPlusOne = blocks.length;
        sumBlocks = 0;
        for (int block : blocks)
            sumBlocks += block;
        diff = length - sumBlocks - blocks.length + 1;
        isUpdated = true;
        isSolved = false;

        white = new int[length];
        black = new int[length];
        canWhite = new int[length+1];
        canBlack = new int[length+1];
    }

    private int getWhites(int a, int b) {
        int result = white[Math.min(b, lastPositionPlusOne - 1)];
        if (a > 0)
            result -= white[a - 1];
        return result;
    }

    boolean solve(int[] state, int[][][] calc){
        this.calc = calc;
        for(int i=startPosition;i<lastPositionPlusOne;i++)
            for(int j=firstBlock;j<=lastBlockPlusOne;j++)
                for(int q=0;q<2;q++)
                    calc[i][j][q] = -1;

        for(int i=startPosition;i<lastPositionPlusOne;i++){
            black[i] = white[i] = canBlack[i] = canWhite[i] = 0;
        }
        for(int i=startPosition;i<lastPositionPlusOne;i++) {
            if (state[i] == -1)
                white[i] = 1;
            else if (state[i] == 3)
                black[i] = 1;
        }
        for(int i=startPosition+1;i<lastPositionPlusOne;i++)
            white[i] += white[i-1];

        int answer = deepSolve(startPosition, firstBlock, 0);
        isUpdated = false;
        if(answer == 0) {
            System.out.println("Невозможно решить");
            return false;
        }
        boolean result = false;
        for (int i = startPosition+1; i < lastPositionPlusOne; i++)
            canBlack[i] += canBlack[i - 1];
        for (int i = startPosition; i < lastPositionPlusOne; i++) {
            if (!(canBlack[i] > 0 && canWhite[i] > 0)) {
                if (canBlack[i] > 0) {
                    if (state[i] != 3) {
                        state[i] = 3;
                        result = true;
                    }
                } else {
                    if (state[i] != -1) {
                        state[i] = -1;
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private int deepSolve(int startPosition, int currentBlock, int lastIsBlack){
        if (startPosition == lastPositionPlusOne)
            return currentBlock == lastBlockPlusOne ? 1 : 0;
        if (startPosition > lastPositionPlusOne)
            return 0;
        if (calc[startPosition][currentBlock][lastIsBlack] != -1)
            return calc[startPosition][currentBlock][lastIsBlack];
        int result = 0;
        int val;
        if (currentBlock < lastBlockPlusOne && lastIsBlack != 1 && getWhites(startPosition, startPosition + blocks[currentBlock] - 1) == 0) {
            val = deepSolve(startPosition + blocks[currentBlock], currentBlock + 1, 1);
            if (val == 1) {
                result = 1;
                canBlack[startPosition]++;
                canBlack[Math.min(lastPositionPlusOne, startPosition + blocks[currentBlock])]--;
            }
        }
        if (black[startPosition] == 0) {
            val = deepSolve(startPosition + 1, currentBlock, 0);
            if (val == 1) {
                result = 1;
                canWhite[startPosition] = 1;
            }
        }
        calc[startPosition][currentBlock][lastIsBlack] = result;
        return result;
    }

    @Override
    public int compareTo(@Nonnull Row anotherRow) {
        return diff - anotherRow.diff;
    }

}
