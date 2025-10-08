package com.Grabe.Terra;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class TerraPanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = -1085629251417172686L;

	// Datenmodell
	final ArrayList<Node> nodes = new ArrayList<>();
	final ArrayList<Edge> edges = new ArrayList<>();
	final ArrayList<Triangle> triangles = new ArrayList<>();

	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten
//		requestFocusInWindow(); // Automatisch Fokus geben

		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(500, 0, 0));
		nodes.add(new Node(250, 500, 0));

		edges.add(new Edge(0, 1));
		edges.add(new Edge(1, 2));
		edges.add(new Edge(2, 0));
		triangles.add(new Triangle(0, 1, 2));

		setBackground(Color.WHITE);
	}

	// KeyListener-Methoden (nur keyPressed relevant)
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			// Berechnungsroutine für Dreiecksdaten ausführen
			performTriangleCalculation();
			repaint(); // Neu zeichnen
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // Hintergrund und Rahmen zeichnen

		// Linien zeichnen
		g.setColor(Color.BLUE);
		for (Triangle triangle : triangles) {
			drawEdge(g, triangle.a);
			drawEdge(g, triangle.b);
			drawEdge(g, triangle.c);
		}
	}

	private void drawEdge(Graphics g, int e) {
		Edge edge = edges.get(e);
		Node nodefrom = nodes.get(edge.from);
		Node nodeto = nodes.get(edge.to);
		g.drawLine((int) nodefrom.x, (int) nodefrom.y, (int) nodeto.x, (int) nodeto.y);
	}

	private void performTriangleCalculation() {
		nodes.get(edges.get(triangles.get(0).a).to).y += 20;
	}
}
