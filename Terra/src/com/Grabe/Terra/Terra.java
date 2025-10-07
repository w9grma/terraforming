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
        frame.setSize(800, 600);

        // Panel zum Zeichnen erstellen
        JPanel panel = new JPanel() {
			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Hintergrund und Rahmen zeichnen

                // Datenmodell Änderung MacBook
                ArrayList<Node> nodes = new ArrayList<>();
                nodes.add(new Node(0,0,0));
                nodes.add(new Node(500,0,0));
                nodes.add(new Node(250,500,0));
                
                ArrayList<Edge> edges = new ArrayList<>();
                edges.add(new Edge(0,1));
                edges.add(new Edge(1,2));
                edges.add(new Edge(2,0));
                
                ArrayList<Triangle> triangles = new ArrayList<>();
                triangles.add(new Triangle(0,1,2));
                
                // Linien zeichnen
                g.setColor(Color.BLUE);
                for (Triangle triangle : triangles ) {
                	Edge edge = edges.get(triangle.a);
                	Node nodefrom = nodes.get(edge.from);
                	Node nodeto = nodes.get(edge.to);
                	g.drawLine((int) nodefrom.x, (int) nodefrom.y, (int) nodeto.x, (int) nodeto.y);
                	edge = edges.get(triangle.b);
                	nodefrom = nodes.get(edge.from);
                	nodeto = nodes.get(edge.to);
                	g.drawLine((int) nodefrom.x, (int) nodefrom.y, (int) nodeto.x, (int) nodeto.y);
                	edge = edges.get(triangle.c);
                	nodefrom = nodes.get(edge.from);
                	nodeto = nodes.get(edge.to);
                	g.drawLine((int) nodefrom.x, (int) nodefrom.y, (int) nodeto.x, (int) nodeto.y);
                }
                
            }
        };

        frame.add(panel);  // Panel zum Fenster hinzufügen
        frame.setVisible(true); // Fenster sichtbar machen
        
        
        
    }
    
}

