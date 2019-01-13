package com.apostal.nonogramsolver.util;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class LookAndFeelUtils {
    public static void setWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    Const.Messages.WINDOWS_STYLE_LOADING_ERROR,
                    Const.Titles.ALERT,
                    JOptionPane.ERROR_MESSAGE);
            log.error(Const.Pattern.LOG, Const.Messages.WINDOWS_STYLE_LOADING_ERROR, e.getMessage());
        }
    }
}
