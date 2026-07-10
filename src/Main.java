package co.edu.unal.poo.actividad5;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactManagerGUI gui = new ContactManagerGUI();
            gui.setVisible(true);
        });
    }
}
