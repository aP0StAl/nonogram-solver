package com.apostal.nonogramsolver.ui.view;

import com.apostal.nonogramsolver.ui.layouts.VerticalLayout;
import com.apostal.nonogramsolver.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@Getter
@Component
@RequiredArgsConstructor
public class ImageProcessingFrame extends JFrame {
    private JLabel imageLabel;
    private JButton nextButton;
    private JButton prevButton;
    private JButton finishButton;

    @PostConstruct
    private void initUI(){
        JPanel mainPanel = new JPanel(new VerticalLayout());
        imageLabel = new JLabel();
        mainPanel.add(imageLabel);
        JPanel controlPanel = new JPanel();
        prevButton = new JButton(Const.Labels.PREV_IMAGE);
        nextButton = new JButton(Const.Labels.NEXT_IMAGE);
        finishButton = new JButton(Const.Labels.FINISH);
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        controlPanel.add(finishButton);
        mainPanel.add(controlPanel);
        mainPanel.setPreferredSize(new Dimension(830,900));
        add(mainPanel);
        setTitle(Const.Titles.IMAGE_PROCESSING);
        setSize(830, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
