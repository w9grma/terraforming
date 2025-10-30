package com.Grabe.Terra;

import javax.swing.*;

public class Terra {

	public static void main(String[] args) {

		// Panel zum Zeichnen erstellen
		TerraPanel panel = new TerraPanel();

		SwingUtilities.invokeLater(() -> {
			// Fenster erstellen
			JFrame frame = new JFrame("Terra Forming");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 828);
			frame.add(panel); // Panel zum Fenster hinzufügen
			frame.setVisible(true);
		});

	}

}
