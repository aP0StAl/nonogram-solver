package com.apostal.nonogramsolver.ui.view.datapanel;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NonogramDataHolder {
    private int rowsCount;
    private int columnsCount;
    private NonogramDataLocation location;
    private KeyListener keyListener;
    private List<List<IndexedTextField>> textFields;

    NonogramDataHolder(int rowsCount, NonogramDataLocation location, KeyListener keyListener){
        this.rowsCount = rowsCount;
        this.columnsCount = 1;
        this.location = location;
        this.keyListener = keyListener;
        textFields = new ArrayList<>();
        for(int i=0;i<rowsCount; i++){
            List<IndexedTextField> currentRow = new ArrayList<>();
            currentRow.add(new IndexedTextField(i,0, location, keyListener));
            textFields.add(currentRow);
        }
    }

    List<IndexedTextField> getAll(){
        List<IndexedTextField> result = new ArrayList<>();
        for (List<IndexedTextField> textFieldList : textFields) {
            result.addAll(textFieldList);
        }
        return result;
    }

    IndexedTextField nextRow(IndexedTextField field){
        int row = field.getRow();
        int column = field.getColumn();
        if(row + 1 >= rowsCount) return null;
        List<IndexedTextField> nextRowList = textFields.get(row+1);
        int actualColumn = Math.min(column, nextRowList.size() - 1);
        return nextRowList.get(actualColumn);
    }

    IndexedTextField prevRow(IndexedTextField field){
        int row = field.getRow();
        int column = field.getColumn();
        if(row - 1 < 0) return null;
        List<IndexedTextField> prevRowList = textFields.get(row-1);
        int actualColumn = Math.min(column, prevRowList.size() - 1);
        return prevRowList.get(actualColumn);
    }

    IndexedTextField nextColumn(IndexedTextField field){
        int row = field.getRow();
        int column = field.getColumn();
        List<IndexedTextField> rowList = textFields.get(row);
        if(column + 1 >= rowList.size()) return null;
        return rowList.get(column+1);
    }

    IndexedTextField prevColumn(IndexedTextField field){
        int row = field.getRow();
        int column = field.getColumn();
        List<IndexedTextField> rowList = textFields.get(row);
        if(column - 1 < 0) return null;
        return rowList.get(column-1);
    }

    IndexedTextField deleteField(IndexedTextField field){
        int row = field.getRow();
        int column = field.getColumn();
        List<IndexedTextField> rowList = textFields.get(row);
        if(column >= rowList.size() - 1) return null;
        for(int i=column+1;i<rowList.size();i++)
            rowList.get(i-1).setText(rowList.get(i).getText());
        IndexedTextField result = rowList.remove(rowList.size()-1);
        columnsCount = 1;
        for (List<IndexedTextField> textFieldList : textFields)
            columnsCount = Math.max(columnsCount, textFieldList.size());
        return result;
    }

    IndexedTextField insertEmptyToEnd(IndexedTextField field){
        int row = field.getRow();
        List<IndexedTextField> rowList = textFields.get(row);
        IndexedTextField result = new IndexedTextField(row, rowList.size(), location, keyListener);
        rowList.add(result);
        if(rowList.size() > columnsCount) columnsCount = rowList.size();
        return result;
    }

    IndexedTextField newRowAtEnd(){
        List<IndexedTextField> fieldList = new ArrayList<>();
        IndexedTextField result = new IndexedTextField(rowsCount, 0, location, keyListener);
        fieldList.add(result);
        textFields.add(fieldList);
        rowsCount++;
        return result;
    }

    int getRowsCount() {
        return rowsCount;
    }

    int getColumnsCount() {
        return columnsCount;
    }

    void update(String data){
        String[] elements = data.split("\\|");
        List<IndexedTextField> fields = new ArrayList<>();
        for (String element : elements)
            fields.add(new IndexedTextField(element, location, keyListener));
        fields.sort(Comparator.comparing(IndexedTextField::getRow));
        int currentRow = -1;
        textFields = new ArrayList<>();
        for (IndexedTextField field : fields) {
            columnsCount = Math.max(columnsCount, field.getColumn()+1);
            if(field.getRow() == currentRow){
                textFields.get(currentRow).add(field);
            } else {
                currentRow++;
                textFields.add(new ArrayList<>());
                textFields.get(currentRow).add(field);
                if(currentRow > 0)
                    textFields.get(currentRow-1).sort(Comparator.comparing(IndexedTextField::getColumn));
            }
        }
        rowsCount = currentRow + 1;
    }

    void updateFromResource(String data){
        String[] rows = data.split(",");
        List<List<Integer>> values = new ArrayList<>();
        for (String row : rows) {
            String[] nums = row.trim().split(" ");
            List<Integer> valueRow = new ArrayList<>();
            valueRow.add(0);
            for (String num : nums) {
                valueRow.add(Integer.valueOf(num));
            }
            values.add(valueRow);
        }
        List<IndexedTextField> fields = new ArrayList<>();
        for(int i = 0; i<values.size(); i++){
            for(int j = values.get(i).size()-1; j>=0; j--){
                fields.add(new IndexedTextField(i + " " + (values.get(i).size()-1-j) + " " + values.get(i).get(j), location, keyListener ));
            }
        }
        fields.sort(Comparator.comparing(IndexedTextField::getRow));
        int currentRow = -1;
        textFields = new ArrayList<>();
        for (IndexedTextField field : fields) {
            columnsCount = Math.max(columnsCount, field.getColumn()+1);
            if(field.getRow() == currentRow){
                textFields.get(currentRow).add(field);
            } else {
                currentRow++;
                textFields.add(new ArrayList<>());
                textFields.get(currentRow).add(field);
                if(currentRow > 0)
                    textFields.get(currentRow-1).sort(Comparator.comparing(IndexedTextField::getColumn));
            }
        }
        rowsCount = currentRow + 1;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        List<IndexedTextField> fieldList = getAll();
        for (IndexedTextField field : fieldList) {
            stringBuilder.append(field.toString());
            stringBuilder.append("|");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    List<List<Integer>> getVolumes(){
        List<List<Integer>> result = new ArrayList<>();
        for (List<IndexedTextField> textFieldList : textFields) {
            List<Integer> row = new ArrayList<>();
            for (IndexedTextField field : textFieldList) {
                int n = field.getNumber();
                if (n <= 0) continue;
                row.add(0,n);
            }
            result.add(row);
        }
        return result;
    }
}
