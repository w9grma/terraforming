package com.Grabe.Terra;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

public class Terra {

    public static void main(String[] args) {
        // Fenster erstellen
        JFrame frame = new JFrame("Terra-Forming");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Panel zum Zeichnen erstellen
        JPanel panel = new JPanel() {
			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Hintergrund und Rahmen zeichnen

                // Linien zeichnen
                g.setColor(Color.RED);
                g.drawLine(50, 50, 350, 50); // horizontale Linie

                g.setColor(Color.BLUE);
                g.drawLine(50, 100, 350, 200); // diagonale Linie

                g.setColor(Color.GREEN);
                g.drawLine(50, 250, 350, 250); // weitere horizontale Linie
            }
        };

        frame.add(panel);  // Panel zum Fenster hinzufügen
        frame.setVisible(true); // Fenster sichtbar machen

        // Datenmodell ccsmjd
        ArrayList<Node> knotenliste = new ArrayList<>();
        knotenliste.add(new Node(-50,-50,0));
        knotenliste.add(new Node(50,-50,0));
        knotenliste.add(new Node(0,50,0));
        
        ArrayList<Edge> kanten = new ArrayList<>();
        kanten.add(new Edge(0,1));
        kanten.add(new Edge(1,2));
        kanten.add(new Edge(2,3));
        
    }
    
}

