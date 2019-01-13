package com.apostal.nonogramsolver.util;

public class Convert {
    public static String arrayToUpMetadataResource(int[][] data) {
        int lastLeftColumn = 0;
        for (int i = data[0].length-1; i > 0; i--) {
            if (data[data.length - 1][i] > 0) {
                lastLeftColumn = i;
                break;
            }
        }
        int lastUpRow = 0;
        for(int i=data.length-1;i>0;i--) {
            if (data[i][data[i].length - 1] > 0) {
                lastUpRow = i;
                break;
            }
        }
        StringBuilder result = new StringBuilder();
        for(int i=lastUpRow+1;i<data.length;i++){
            if(i>lastUpRow+1)result.append(",");
            for(int j=0;j<=lastLeftColumn;j++){
                if(data[i][j] > 0) {
                    result.append(" ");
                    result.append(data[i][j]);
                }
            }
        }
        return result.toString();
    }

    public static String arrayToLeftMetadataResource(int[][] data) {
        int lastLeftColumn = 0;
        for (int i = data[0].length-1; i > 0; i--) {
            if (data[data.length - 1][i] > 0) {
                lastLeftColumn = i;
                break;
            }
        }
        int lastUpRow = 0;
        for(int i=data.length-1;i>0;i--) {
            if (data[i][data[i].length - 1] > 0) {
                lastUpRow = i;
                break;
            }
        }
        StringBuilder result = new StringBuilder();
        for(int j=lastLeftColumn+1;j<data[0].length;j++){
            if (j>lastLeftColumn+1) result.append(",");
            for(int i=0;i<=lastUpRow;i++){
                if(data[i][j] > 0) {
                    result.append(" ");
                    result.append(data[i][j]);
                }
            }
        }
        return result.toString();
    }

}
