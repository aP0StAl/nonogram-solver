package com.apostal.nonogramsolver.ui.view.datapanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Slf4j
@Getter
public class IndexedTextField extends JTextField{
    private static Border staticBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
    private static Border upBorder = new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
            BorderFactory.createMatteBorder(0, 1, 1, 1, Color.LIGHT_GRAY));
    private static Border downBorder = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
            BorderFactory.createMatteBorder(1, 1, 0, 1, Color.LIGHT_GRAY));
    private static Border leftBorder = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK),
            BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
    private static Border rightBorder = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK),
            BorderFactory.createMatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY));

    private static Font staticFont = new Font("Arial", Font.PLAIN, 7);
    private int row;
    private int column;

    IndexedTextField(int row, int column, NonogramDataLocation location, KeyListener keyListener){
        super("");
        initField(row,column,"",location,keyListener);
    }

    IndexedTextField(String data, NonogramDataLocation location, KeyListener keyListener){
        String[] numbers = data.split(" ");
        int row = Integer.parseInt(numbers[0]);
        int column = Integer.parseInt(numbers[1]);
        int value = Integer.parseInt(numbers[2]);
        String text = value > 0 ? value+"" : "";
        initField(row,column,text,location,keyListener);
    }

    private void initField(int row, int column, String text, NonogramDataLocation location, KeyListener keyListener){
        addKeyListener(keyListener);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(staticFont);
        Border currentBorder = staticBorder;
        if(location.equals(NonogramDataLocation.LEFT)) {
            if (row % 5 == 0)
                currentBorder = upBorder;
            if (row % 5 == 4)
                currentBorder = downBorder;
        }
        if(location.equals(NonogramDataLocation.UP)) {
            if (row % 5 == 0)
                currentBorder = leftBorder;
            if (row % 5 == 4)
                currentBorder = rightBorder;
        }
        setBorder(currentBorder);
        this.row = row;
        this.column = column;
        setText(text);
    }

    @Override
    public void processKeyEvent(KeyEvent ev) {
        if(ev.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
                ev.getKeyCode() == KeyEvent.VK_DELETE ||
                ev.getKeyCode() == KeyEvent.VK_UP ||
                ev.getKeyCode() == KeyEvent.VK_DOWN ||
                ev.getKeyCode() == KeyEvent.VK_LEFT ||
                ev.getKeyCode() == KeyEvent.VK_RIGHT
                ){
            try {
                super.processKeyEvent(ev);
            } catch (NullPointerException ex) {
                log.error("processKeyEvent: " + ex.getClass());
            }
        }
        if (Character.isDigit(ev.getKeyChar()) && getText().length() < 2) {
            super.processKeyEvent(ev);
        }
        ev.consume();
    }

    int getNumber() {
        String text = getText();
        if (text != null && !text.isEmpty()) {
            return Integer.valueOf(text);
        }
        return 0;
    }

    @Override
    public String toString(){
        return String.format("%d %d %d",row, column, getNumber());
    }
}
