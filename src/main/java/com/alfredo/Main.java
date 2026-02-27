package com.alfredo;

import com.alfredo.ui.Principal;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    static void main() {

        try {
            UIManager.setLookAndFeel( new FlatLightLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }


        new Principal();

    }
}
