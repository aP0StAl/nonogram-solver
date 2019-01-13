package com.apostal.nonogramsolver.ui.controller;

import com.apostal.nonogramsolver.service.solver.NonogramSolverService;
import com.apostal.nonogramsolver.ui.shared.AbstractFrameController;
import com.apostal.nonogramsolver.ui.view.ImageProcessingFrame;
import com.apostal.nonogramsolver.ui.view.MainControlPanel;
import com.apostal.nonogramsolver.ui.view.MainFrame;
import com.apostal.nonogramsolver.util.Convert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.apostal.nonogramsolver.util.Const.Default.CELL_SIZE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainFrameController extends AbstractFrameController{
    private final MainFrame mainFrame;
    private final ImageProcessingFrame imageProcessingFrame;
    private final ImageProcessingController imageProcessingController;
    private final NonogramSolverService nonogramSolverService;
    private BufferedImage nonogramImage;
    private Timer timer;

    @PostConstruct
    private void prepareListeners() {
        MainControlPanel mainControlPanel = mainFrame.getControlPanel();
        JButton imageProcessingFinishButton = imageProcessingFrame.getFinishButton();

        registerAction(mainControlPanel.getLoadPhotoButton(), (e) -> openImageProcessingFrame());
        registerAction(mainControlPanel.getSolveButton(), (e) -> solveNonogram());
        registerAction(imageProcessingFinishButton, (e) -> hideIPFrameAndUpdateMetaData());
    }

    private void solveNonogram() {
        List<List<Integer>> leftVolumes = mainFrame.getLeftMetaDataPanel().getVolumes();
        List<List<Integer>> upVolumes = mainFrame.getUpMetaDataPanel().getVolumes();
        int height = mainFrame.getLeftMetaDataPanel().getRowsCount();
        int width = mainFrame.getUpMetaDataPanel().getRowsCount();
        nonogramSolverService.start(leftVolumes,upVolumes,width,height);
        updateImageStart();
    }

    private void updateImageStart() {
        nonogramImage = new BufferedImage(
                CELL_SIZE * mainFrame.getUpMetaDataPanel().getRowsCount(),
                CELL_SIZE * mainFrame.getLeftMetaDataPanel().getRowsCount(),
                BufferedImage.TYPE_INT_RGB);
        mainFrame.getNonogramImageLabel().setIcon(new ImageIcon(nonogramImage));
        int[] colorRGB = new int[4];
        colorRGB[0] = new Color(222, 222, 222).getRGB();
        colorRGB[1] = new Color(222, 219, 185).getRGB();
        colorRGB[2] = new Color(238, 187, 160).getRGB();
        colorRGB[3] = new Color(22, 22, 22).getRGB();
        timer = new Timer(10, actionEvent -> {
            if (nonogramSolverService.isSolved())
                timer.stop();
            int[][] state = nonogramSolverService.getResult();
            for (int i = 0; i < state.length; i++) {
                for (int j = 0; j < state[i].length; j++) {
                    int index = state[i][j];
                    if (index == -1) index = 1;
                    fillCell(j, i, colorRGB[index]);
                }
            }
            mainFrame.getNonogramImageLabel().repaint();
        });
        timer.start();
    }

    private void fillCell(int x, int y, int colorRGB){
        for(int i=0;i<CELL_SIZE;i++){
            for(int j=0;j<CELL_SIZE;j++){
                nonogramImage.setRGB(j+x*CELL_SIZE,i+y*CELL_SIZE, colorRGB);
            }
        }
    }


    private void hideIPFrameAndUpdateMetaData() {
        imageProcessingFrame.setVisible(false);
        int data[][] = imageProcessingController.getData();
        mainFrame.getLeftMetaDataPanel().updateFromResource(Convert.arrayToLeftMetadataResource(data));
        mainFrame.getUpMetaDataPanel().updateFromResource(Convert.arrayToUpMetadataResource(data));
    }

    private void openImageProcessingFrame() {
        imageProcessingController.prepareAndOpenFrame();
    }

    @Override
    public void prepareAndOpenFrame() {
        mainFrame.setVisible(true);
    }
}
