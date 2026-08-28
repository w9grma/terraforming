package com.grabe.terra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class TerraPanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = -1085629251417172686L;

	// Datenmodell
	final ArrayList<Vertex> vertices = new ArrayList<>();
	final ArrayList<Triangle> triangles = new ArrayList<>();
	final ArrayList<Edge> kanten = new ArrayList<>();
	double magnifier = 1000;
	double alpha = 0; // Drehung um x
	double beta = 0; // Drehung um y
	double gamma = 0; // Drehung um z
	final double default_alpha = Math.toRadians(45);
	final double rotation_step = Math.toRadians(2);
	final double pi = Math.PI;
	final double twopi = 2 * Math.PI;
	double persp_eye = 1;
	double persp_model = 1.5;
	String string2draw;
	boolean showhelp = false;
	boolean showaxis = true;
	boolean drawvertexcoordinates = false;
	boolean showvertexnames = true;

	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten

		// 1. Initial triangle ABC
		Vertex va = new Vertex(-Math.cos(pi / 6) / 2, 0, -0.25, 'A'); // (cos pi/6) / 2 = 0,433...
		vertices.add(va);
		Vertex vb = new Vertex(Math.cos(pi / 6) / 2, 0, -0.25, 'B');
		vertices.add(vb);
		Vertex vc = new Vertex(0, 0, 0.5, 'C');
		vertices.add(vc);

		Edge eab = new Edge(va, vb);
		kanten.add(eab);
		Edge ebc = new Edge(vb, vc);
		kanten.add(ebc);
		Edge eca = new Edge(vc, va);
		kanten.add(eca);

		Triangle tabc = new Triangle(eab, ebc, eca);
		triangles.add(tabc);

		// Add special vertices, edges and triangle for local xyz axes
		Vertex v0 = new Vertex(0, 0, 0, '0');
		vertices.add(v0);
		Vertex vx = new Vertex(0.1, 0, 0, 'x');
		vertices.add(vx);
		Vertex vy = new Vertex(0, 0.1, 0, 'y');
		vertices.add(vy);
		Vertex vz = new Vertex(0, 0, 0.1, 'z');
		vertices.add(vz);
		Edge e0x = new Edge(vx, v0);
		kanten.add(e0x);
		Edge e0y = new Edge(vy, v0);
		kanten.add(e0y);
		Edge e0z = new Edge(vz, v0);
		kanten.add(e0z);

		setBackground(Color.WHITE);
		setFont(new Font("Monospaced", Font.PLAIN, 16));

		alpha = default_alpha;
	}

	// KeyListener-Methoden (nur keyPressed relevant)
	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getExtendedKeyCode()) {

		// x-axis rotation
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_S:
			alpha -= rotation_step;
			if (alpha < 0)
				alpha += twopi;
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_NUMPAD8:
			alpha += rotation_step;
			if (alpha > twopi)
				alpha -= twopi;
			break;

		// y-axis rotation
		case KeyEvent.VK_A:
		case KeyEvent.VK_NUMPAD4:
			beta -= rotation_step;
			if (beta < 0)
				beta += twopi;
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_NUMPAD6:
			beta += rotation_step;
			if (beta > twopi)
				beta -= twopi;
			break;

		// z-axis rotation
		case KeyEvent.VK_Q:
		case KeyEvent.VK_NUMPAD7:
			gamma -= rotation_step;
			if (gamma < 0)
				gamma += twopi;
			break;
		case KeyEvent.VK_E:
		case KeyEvent.VK_NUMPAD9:
			gamma += rotation_step;
			if (gamma > twopi)
				gamma -= twopi;
			break;

		// magnification
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_PAGE_UP:
			magnifier += 50;
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_PAGE_DOWN:
			magnifier -= 50;
			break;

		// reset rotation
		case KeyEvent.VK_X:
		case KeyEvent.VK_NUMPAD5:
			alpha = default_alpha;
			beta = gamma = 0;
			break;

		// show help
		case KeyEvent.VK_H:
			showhelp = !showhelp;
			break;

		// show local coordinate system axes
		case KeyEvent.VK_C:
			showaxis = !showaxis;
			break;

		// show vertex coordinates
		case KeyEvent.VK_V:
			drawvertexcoordinates = !drawvertexcoordinates;
			break;

		// subdivide
		case KeyEvent.VK_SPACE:
			doSubdivideTriangles();
			break;

		// show vertex names
		case KeyEvent.VK_N:
			showvertexnames = !showvertexnames;
			break;

		// Exit application
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}

		repaint(); // Neu zeichnen
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

		// Draw helper lines and circle on the screen
//		g.drawLine(0, 0, x, y);
//		g.drawLine(0, yoff, x, yoff);
//		g.drawLine(xoff, 0, xoff, y);

		// Draw global x, y and z axes
		g.drawLine(10, y - 10, 110, y - 10);
		g.drawString("X", 110, y - 10);
		g.drawLine(10, y - 10, 10, y - 110);
		g.drawString("Y", 10, y - 110);
		g.drawLine(10, y - 10, 36, y - 36);
		g.drawString("Z", 36, y - 36);

		// Draw lines - don't care about triangles, just draw all edges
		g.setColor(Color.BLUE);
		for (Edge edge : kanten) {
			// Do rotation and apply perspective
			Vertex va = edge.p1;
			Vertex vb = edge.p2;
			Vertex rva = rotate(va);
			Vertex rvb = rotate(vb);

			// Apply perspective adjustment Vertex a and b
			double prvax = rva.x * persp_eye / (persp_eye + persp_model + rva.z);
			double prvay = rva.y * persp_eye / (persp_eye + persp_model + rva.z);
			double prvbx = rvb.x * persp_eye / (persp_eye + persp_model + rvb.z);
			double prvby = rvb.y * persp_eye / (persp_eye + persp_model + rvb.z);

			// Adjust coordinates for screen presentation (zoom, center)
			int bax = (int) (prvax * magnifier + xoff);
			int bay = (int) (-prvay * magnifier + yoff);
			int bbx = (int) (prvbx * magnifier + xoff);
			int bby = (int) (-prvby * magnifier + yoff);

			// Actual drawing
			if (showaxis || (va.label != 'x' && va.label != 'y' && va.label != 'z')) {
				g.drawLine(bax, bay, bbx, bby);
				if (showvertexnames)
					string2draw = Character.toString(edge.p1.label);
				else
					string2draw = "";
				if (drawvertexcoordinates) {
					string2draw += " (" + Math.round(rva.x * 100);
					string2draw += "/" + Math.round(rva.y * 100);
					string2draw += "/" + Math.round(rva.z * 100) + ")";
				}
				if (!string2draw.isEmpty())
					g.drawString(string2draw, bax - 3, bay - 5);
			}
		}

		// Draw help info and rotation angles
		string2draw = "Rotation: x/y/z: ";
		string2draw += Math.round(Math.toDegrees(alpha)) + " / ";
		string2draw += Math.round(Math.toDegrees(beta)) + " / ";
		string2draw += Math.round(Math.toDegrees(gamma));
		g.drawString(string2draw, 10, 150);
		g.drawString("Press h for help", 10, 180);

		// Draw help info on screen showing key combinations
		if (showhelp) {
			g.drawString("W,S: Rotate x-axis", 10, 210);
			g.drawString("A,D: Rotate y-axis", 10, 240);
			g.drawString("Q,E: Rotate z-axis", 10, 270);
			g.drawString("+,-: Zoom in and out", 10, 300);
			g.drawString(" X : Reset rotation", 10, 330);
			g.drawString(" C : Display local x,y,z axis: " + showaxis, 10, 360);
			g.drawString(" V : Display vertex coordinates: " + drawvertexcoordinates, 10, 390);
			g.drawString(" N : Display vertex names: " + showvertexnames, 10, 420);
			g.drawString("ESC: Exit", 10, 450);
		}

	}

	private Vertex rotate(Vertex vx) {
		// Get coordinates
		double ax = vx.x;
		double ay = vx.y;
		double az = vx.z;

		// Isometric transformation of vertex coordinates for rotation of x-axis
		// (alpha), y-axis (beta) and z-axis (gamma)
		// -- Rotate Alpha first
		double nax = ax;
		double nay = Math.cos(alpha) * ay + Math.sin(alpha) * az;
		double naz = Math.cos(alpha) * az - Math.sin(alpha) * ay;

		// -- Rotate Beta secondly on top of alpha rotation
		ax = Math.cos(beta) * nax + Math.sin(beta) * naz;
		ay = nay;
		az = Math.cos(beta) * naz - Math.sin(beta) * nax;

		// -- Rotate Gamma last on top of beta rotation
		nax = Math.cos(gamma) * ax - Math.sin(gamma) * ay;
		nay = Math.cos(gamma) * ay + Math.sin(gamma) * ax;
		naz = az;

		return new Vertex(nax, nay, naz, vx.label);
	}

	private void doSubdivideTriangles() {
		// first of all we need the existing edges to be split in two edges of same length. middle (shared) vertex gets adjusted in height
		for (Edge edge : kanten) {
			// new vertex gets average of all coordinates
			edge.pm.x = (edge.p1.x + edge.p2.x) / 2;
			edge.pm.y = (edge.p1.y + edge.p2.y) / 2;
			edge.pm.z = (edge.p1.z + edge.p2.z) / 2;
			
			// new vertex coordinates are adjusted randomly in height
			//  random() * 2 - 1
			double deltaz = Math.random() * 0.1 * edge.pm.z;
			
		
		}
	}
}
