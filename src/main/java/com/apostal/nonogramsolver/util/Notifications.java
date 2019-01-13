package com.apostal.nonogramsolver.util;

import javax.swing.*;

public class Notifications {
    public static void commonAlert(String message){
        JOptionPane.showMessageDialog(null,
                message,
                Const.Titles.ALERT,
                JOptionPane.ERROR_MESSAGE);
    }
}
