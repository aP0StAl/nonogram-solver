package com.apostal.nonogramsolver.ui.controller;

import com.apostal.nonogramsolver.service.ImageProcessingService;
import com.apostal.nonogramsolver.ui.shared.AbstractFrameController;
import com.apostal.nonogramsolver.ui.view.ImageProcessingFrame;
import com.apostal.nonogramsolver.util.Const;
import com.apostal.nonogramsolver.util.Notifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageProcessingController extends AbstractFrameController{
    private final ImageProcessingFrame imageProcessingFrame;
    private final ImageProcessingService imageProcessingService;

    private JLabel imageLabel;

    @Override
    public void prepareAndOpenFrame() {
        imageLabel = imageProcessingFrame.getImageLabel();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Const.Paths.IMAGES));
        int ret = fileChooser.showDialog(null, Const.Titles.FILE_OPEN);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                activate(ImageIO.read(file));
                imageProcessingFrame.setVisible(true);
            } catch (IOException | NullPointerException e) {
                Notifications.commonAlert(Const.Messages.WRONG_IMAGE);
                log.error("{} : {}", Const.Messages.WRONG_IMAGE, e.getMessage());
            }
        }
    }

    @PostConstruct
    private void prepareListeners() {
        JButton prevButton = imageProcessingFrame.getPrevButton();
        JButton nextButton = imageProcessingFrame.getNextButton();

        registerAction(prevButton, (e) -> prevImage());
        registerAction(nextButton, (e) -> nextImage());
    }

    private void updateImage(JLabel label, BufferedImage image){
        int maxSize = 800;
        int width = image.getWidth();
        int height = image.getHeight();
        int max = Math.max(width, height);
        double coef = (1.0 * maxSize) / max;
        try {
            image = Thumbnails.of(image)
                    .scale(coef)
                    .asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        label.setIcon(new ImageIcon(image));
        label.repaint();
    }

    private void activate(BufferedImage image){
        imageProcessingService.setImage(image);
        JLabel imageLabel = imageProcessingFrame.getImageLabel();
        updateImage(imageLabel, image);
    }

    private void nextImage(){
        BufferedImage image = getNextImage();
        updateImage(imageLabel, image);
    }

    private void prevImage(){
        BufferedImage image = getPrevImage();
        updateImage(imageLabel, image);
    }

    private BufferedImage getPrevImage(){
        return imageProcessingService.prev();
    }

    private BufferedImage getNextImage(){
        return imageProcessingService.next();
    }

    public int[][] getData(){
        return imageProcessingService.getData();
    }
}
