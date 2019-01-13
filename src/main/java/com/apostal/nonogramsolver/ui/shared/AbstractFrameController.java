package com.apostal.nonogramsolver.ui.shared;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class AbstractFrameController {
    public abstract void prepareAndOpenFrame();

    protected void registerAction(JButton button, ActionListener listener) {
        button.addActionListener(listener);
    }
}
