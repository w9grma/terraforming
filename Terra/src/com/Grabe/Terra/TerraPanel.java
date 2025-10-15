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
	final ArrayList<Triangle> triangles = new ArrayList<>();
	final double magnifier = 0.9;
	
	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten

		nodes.add(new Node(-Math.cos(Math.PI/6)/2, -0.25, 0));
		nodes.add(new Node(Math.cos(Math.PI/6)/2, -0.25, 0));
		nodes.add(new Node(0, 0.5, 0));
		triangles.add(new Triangle(0, 1, 2, 1));

		nodes.add(new Node(Math.cos(Math.PI/6), 0.5, 0));
		triangles.add(new Triangle(1, 3, 2, 2));

		nodes.add(new Node(-Math.cos(Math.PI/6), 0.5, 0));
		triangles.add(new Triangle(0, 2, 4, 3));

		nodes.add(new Node(0, -1, 0));
		triangles.add(new Triangle(5, 1, 0, 4));

		setBackground(Color.WHITE);
	}

	// KeyListener-Methoden (nur keyPressed relevant)
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			// Berechnungsroutine für Dreiecksdaten ausführen
			doSubdivideTriangles();
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

		// Offset-Ermittlung für Mitte des Panels
		int y = this.getHeight();
		int x = this.getWidth();
		int yoff = (int) y / 2;
		int xoff = (int) x / 2;
		
		g.drawLine(0, 0, x, y);
		g.drawLine(0, yoff, x, yoff);
		g.drawLine(xoff, 0, xoff, y);
		g.drawOval( (int) (x * (1-magnifier)/2), (int) (y * (1-magnifier)/2), (int) (x * magnifier), (int) (y * magnifier));
		
		// Linien zeichnen
		g.setColor(Color.BLUE);
		for (Triangle triangle : triangles) {
			int ndA = triangle.vertexes[0];
			int ndB = triangle.vertexes[1];
			int ndC = triangle.vertexes[2];
			int ax = (int) (nodes.get(ndA).x/2*x*magnifier);
			int ay = (int) (nodes.get(ndA).y/2*y*magnifier);
			int bx = (int) (nodes.get(ndB).x/2*x*magnifier);
			int by = (int) (nodes.get(ndB).y/2*y*magnifier);
			int cx = (int) (nodes.get(ndC).x/2*x*magnifier);
			int cy = (int) (nodes.get(ndC).y/2*y*magnifier);
			g.drawLine(ax+xoff, -ay+yoff, bx+xoff, -by+yoff);
			g.drawLine(bx+xoff, -by+yoff, cx+xoff, -cy+yoff);
			g.drawLine(cx+xoff, -cy+yoff, ax+xoff, -ay+yoff);
		}
	}

	private void doSubdivideTriangles() {
		for (Triangle tri: triangles) {
			int ndA = tri.vertexes[0];
			int ndB = tri.vertexes[1];
			int ndC = tri.vertexes[2];			
		}
	}
}
