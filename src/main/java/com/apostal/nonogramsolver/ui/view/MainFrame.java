package com.apostal.nonogramsolver.ui.view;

import com.apostal.nonogramsolver.ui.layouts.VerticalLayout;
import com.apostal.nonogramsolver.ui.view.datapanel.LeftNonogramDataPanel;
import com.apostal.nonogramsolver.ui.view.datapanel.UpNonogramDataPanel;
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
public class MainFrame extends JFrame {
    private final MainControlPanel controlPanel;
    private final LeftNonogramDataPanel leftMetaDataPanel;
    private final UpNonogramDataPanel upMetaDataPanel;
    private JLabel nonogramImageLabel;


    @PostConstruct
    private void initUI(){
        initComponents();
        setFrameUp();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new VerticalLayout());
        mainPanel.add(controlPanel);
        JPanel nonogramPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        nonogramImageLabel = new JLabel();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        nonogramPanel.add(leftMetaDataPanel, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        nonogramPanel.add(upMetaDataPanel, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        nonogramPanel.add(nonogramImageLabel, constraints);

        mainPanel.add(nonogramPanel);
        mainPanel.setPreferredSize(new Dimension(700,400));
        add(mainPanel);
    }

    private void setFrameUp() {
        setTitle(Const.Titles.MAIN);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
