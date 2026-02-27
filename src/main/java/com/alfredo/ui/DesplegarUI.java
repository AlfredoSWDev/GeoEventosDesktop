package com.alfredo.ui;

import javax.swing.*;
import java.awt.*;

public class DesplegarUI {
    public JFrame desplegarUI(JPanel panel, String titulo, int operacionCierre) {
        JFrame frame = new JFrame(titulo);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(operacionCierre);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setVisible(true);
        return frame;
    }
}
