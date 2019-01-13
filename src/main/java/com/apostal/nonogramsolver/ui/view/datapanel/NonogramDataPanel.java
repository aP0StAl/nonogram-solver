package com.apostal.nonogramsolver.ui.view.datapanel;

import info.clearthought.layout.TableLayout;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import static com.apostal.nonogramsolver.util.Const.Default.CELL_SIZE;

@Slf4j
public class NonogramDataPanel extends JPanel{
    private NonogramDataLocation location;

    private TableLayout layout;

    private int rowsCount = 7;
    private int columnsCount = 1;
    private NonogramDataHolder metaDataHolder;

    NonogramDataPanel(NonogramDataLocation location){
        this.location = location;
    }

    @PostConstruct
    private void initUI() {
        double size[][] = initTableSize(rowsCount,columnsCount);

        layout = new TableLayout(size);
        setLayout(layout);

        KeyListener kl = textFieldKeyListener();

        metaDataHolder = new NonogramDataHolder(rowsCount, location, kl);

        List<IndexedTextField> fieldList = metaDataHolder.getAll();

        for (IndexedTextField field : fieldList)
            addToLayout(field);

        if(location.equals(NonogramDataLocation.LEFT))
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        else
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        log.info(location + " NonogramDataPanel инициализирован");
        log.debug(location+" NonogramDataPanel - prefered size : "+getPreferredSize());
    }

    private void addToLayout(IndexedTextField field){
        int row = field.getRow() + 1;
        int column = columnsCount - field.getColumn();
        if(location.equals(NonogramDataLocation.LEFT))
            add(field, column+", "+row);
        if(location.equals(NonogramDataLocation.UP))
            add(field, row+", "+column);
    }

    private double[][] initTableSize(int rows, int columns){
        double[][] result = new double[2][Math.max(rows, columns)+2];
        int index0 = location.equals(NonogramDataLocation.LEFT) ? 0 : 1;
        int index1 = 1 - index0;

        result[index0][0] = 0;
        result[index0][columns+1] = 0;
        for(int i=1; i<=columns;i++)
            result[index0][i] = CELL_SIZE;
        result[index1][0] = 0;
        result[index1][rows+1] = 0;
        for(int i=1; i<=rows;i++)
            result[index1][i] = CELL_SIZE;
        return result;
    }

    private KeyListener textFieldKeyListener(){
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                IndexedTextField src = (IndexedTextField)e.getSource();
                IndexedTextField focusField = null;
                int keyCode = e.getKeyCode();
                if(location.equals(NonogramDataLocation.UP)){
                    switch (keyCode) {
                        case KeyEvent.VK_UP:
                            keyCode = KeyEvent.VK_LEFT;
                            break;
                        case KeyEvent.VK_DOWN:
                            keyCode = KeyEvent.VK_RIGHT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            keyCode = KeyEvent.VK_DOWN;
                            break;
                        case KeyEvent.VK_LEFT:
                            keyCode = KeyEvent.VK_UP;
                            break;
                    }
                }
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        focusField = metaDataHolder.prevRow(src);
                        break;
                    case KeyEvent.VK_DOWN:
                        focusField = metaDataHolder.nextRow(src);
                        if(focusField == null)
                            focusField = insertLastRow();
                            updateUI();
                        break;
                    case KeyEvent.VK_LEFT:
                        focusField = metaDataHolder.nextColumn(src);
                        break;
                    case KeyEvent.VK_RIGHT:
                        focusField = metaDataHolder.prevColumn(src);
                        break;
                    case KeyEvent.VK_DELETE:
                        IndexedTextField forDeleteField = metaDataHolder.deleteField(src);
                        if(forDeleteField != null){
                            layout.removeLayoutComponent(forDeleteField);
                            if(columnsCount > metaDataHolder.getColumnsCount()){
                                columnsCount--;
                                deleteColumnFromLayout(1);
                            }
                            updateUI();
                            throw new NullPointerException("Выключаем звук");
                        }
                        break;
                }
                if (focusField != null) {
                    focusField.requestFocus();
                    focusField.setCaretPosition(focusField.getText().length());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                IndexedTextField src = (IndexedTextField)e.getSource();
                if(metaDataHolder.nextColumn(src)==null && src.getNumber() > 0) {
                    IndexedTextField additionalField = metaDataHolder.insertEmptyToEnd(src);
                    if(columnsCount < metaDataHolder.getColumnsCount()) {
                        columnsCount++;
                        insertColumnToLayout(1,CELL_SIZE);
                    }
                    addToLayout(additionalField);
                    updateUI();
                }
            }
        };
    }

    private void insertColumnToLayout(int i, double size){
        if(location.equals(NonogramDataLocation.LEFT))
            layout.insertColumn(i, size);
        if(location.equals(NonogramDataLocation.UP))
            layout.insertRow(i, size);
    }

    private void insertRowToLayout(int i, double size){
        if(location.equals(NonogramDataLocation.LEFT))
            layout.insertRow(i, size);
        if(location.equals(NonogramDataLocation.UP))
            layout.insertColumn(i, size);
    }

    private void deleteColumnFromLayout(int i){
        if(location.equals(NonogramDataLocation.LEFT))
            layout.deleteColumn(i);
        if(location.equals(NonogramDataLocation.UP))
            layout.deleteRow(i);
    }


    private IndexedTextField insertLastRow(){
        IndexedTextField result = metaDataHolder.newRowAtEnd();
        rowsCount++;
        insertRowToLayout(rowsCount,CELL_SIZE);
        addToLayout(result);
        return result;
    }

    public void update(String data){
        metaDataHolder.update(data);
        this.rowsCount = metaDataHolder.getRowsCount();
        this.columnsCount = metaDataHolder.getColumnsCount();

        double[][] size = initTableSize(rowsCount, columnsCount);

        layout = new TableLayout(size);
        setLayout(layout);
        List<IndexedTextField> fieldList = metaDataHolder.getAll();

        for (IndexedTextField field : fieldList)
            addToLayout(field);

        if(location.equals(NonogramDataLocation.LEFT))
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        else
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        updateUI();
    }

    public void updateFromResource(String date){
        metaDataHolder.updateFromResource(date);
        this.rowsCount = metaDataHolder.getRowsCount();
        this.columnsCount = metaDataHolder.getColumnsCount();

        double[][] size = initTableSize(rowsCount, columnsCount);

        layout = new TableLayout(size);
        setLayout(layout);
        List<IndexedTextField> fieldList = metaDataHolder.getAll();

        for (IndexedTextField field : fieldList)
            addToLayout(field);

        if(location.equals(NonogramDataLocation.LEFT))
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        else
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        updateUI();
    }

    @Override
    public String toString(){
        return metaDataHolder.toString();
    }

    public List<List<Integer>> getVolumes(){
        return metaDataHolder.getVolumes();
    }

    public int getRowsCount() {
        return rowsCount;
    }
}
