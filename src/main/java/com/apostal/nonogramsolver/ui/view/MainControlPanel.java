package com.apostal.nonogramsolver.ui.view;

import com.apostal.nonogramsolver.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Getter
@Component
@RequiredArgsConstructor
public class MainControlPanel extends JPanel {
    private final ImageProcessingFrame imageProcessingFrame;

    private JButton loadPhotoButton;
    private JButton solveButton;

    @PostConstruct
    private void initUI(){
        loadPhotoButton = new JButton(Const.Labels.LOAD_PHOTO);
        add(loadPhotoButton);
        solveButton = new JButton(Const.Labels.SOLVE);
        add(solveButton);
    }
}
