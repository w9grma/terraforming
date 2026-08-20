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
	double alpha = Math.PI / 8; // Drehung um x
	double beta = 0; // Drehung um y
	double gamma = 0; // Drehung um z
	double persp_eye = 1;
	double persp_model = 1.5;
	boolean showhelp = false;

	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten

		// 1. Dreieck ABC, unten links
		Vertex va = new Vertex(-Math.cos(Math.PI / 6) / 2, 0, -0.25, 'A');
		vertices.add(va);
		Vertex vb = new Vertex(Math.cos(Math.PI / 6) / 2, 0, -0.25, 'B');
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

		// 2. Dreieck BDC, unten Mitte
		Vertex vd = new Vertex(Math.cos(Math.PI / 6), 0, 0.5, 'D');
		vertices.add(vd);

		Edge ebd = new Edge(vb, vd);
		kanten.add(ebd);
		Edge edc = new Edge(vd, vc);
		kanten.add(edc);

		Triangle tbdc = new Triangle(ebd, edc, ebc);
		triangles.add(tbdc);

		// 3. Dreieck BED, unten rechts
		Vertex ve = new Vertex(-Math.cos(Math.PI / 6), 0, 0.5, 'E');
		vertices.add(ve);
		Edge ebe = new Edge(vb, ve);
		kanten.add(ebe);
		Edge eed = new Edge(ve, vd);
		kanten.add(eed);
		Triangle tbed = new Triangle(ebe, eed, ebd);
		triangles.add(tbed);

		// 4. Dreieck CDF, oben
		Vertex vf = new Vertex(0, 0, -1, 'F');
		vertices.add(vf);
		Edge edf = new Edge(vd, vf);
		kanten.add(edf);
		Edge efc = new Edge(vf, vc);
		kanten.add(efc);
		Triangle tcdf = new Triangle(edc, edf, efc);
		triangles.add(tcdf);

		// Add special vertices, edges and triangle for local xyz axes
		Vertex v0 = new Vertex(0, 0, 0, '0');
		vertices.add(v0);
		Vertex vx = new Vertex(1, 0, 0, 'x');
		vertices.add(vx);
		Vertex vy = new Vertex(0, 1, 0, 'y');
		vertices.add(vy);
		Vertex vz = new Vertex(0, 0, 1, 'z');
		vertices.add(vz);
		Edge e0x = new Edge(v0, vx);
		kanten.add(e0x);
		Edge e0y = new Edge(v0, vy);
		kanten.add(e0y);
		Edge e0z = new Edge(v0, vz);
		kanten.add(e0z);
		triangles.add(new Triangle(e0x, e0y, e0z));

		setBackground(Color.WHITE);
	}

	// KeyListener-Methoden (nur keyPressed relevant)
	@Override
	public void keyPressed(KeyEvent e) {

		doSubdivideTriangles();

		switch (e.getExtendedKeyCode()) {
		// y-axis rotation
		case KeyEvent.VK_A:
		case KeyEvent.VK_NUMPAD4:
			beta -= Math.PI / 100;
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_NUMPAD6:
			beta += Math.PI / 100;
			break;
		// x-axis rotation
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_S:
			alpha -= Math.PI / 100;
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_NUMPAD8:
			alpha += Math.PI / 100;
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
		// z-axis rotation
		case KeyEvent.VK_E:
		case KeyEvent.VK_NUMPAD9:
			gamma += Math.PI / 100;
			break;
		case KeyEvent.VK_Q:
		case KeyEvent.VK_NUMPAD7:
			gamma -= Math.PI / 100;
			break;
		// reset rotation
		case KeyEvent.VK_X:
		case KeyEvent.VK_NUMPAD5:
			alpha = beta = gamma = 0;
			break;
		// show help
		case KeyEvent.VK_H:
			showhelp = !showhelp;
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
			// Get vertices of current edge
			Vertex va = edge.p1;
			Vertex vb = edge.p2;
			// Get coordinates for p1
			double ax = va.x;
			double ay = va.y;
			double az = va.z;
			// Get coordinates for p2
			double bx = vb.x;
			double by = vb.y;
			double bz = vb.z;

			// Isometric transformation of vertex coordinates for rotation of
			// x-axis (alpha), y-axis (beta) and z-axis (gamma)
			// -- Rotate Alpha first
			// -- -- Vertex a
			double nax = ax;
			double nay = Math.cos(alpha) * ay - Math.sin(alpha) * az;
			double naz = Math.cos(alpha) * az + Math.sin(alpha) * ay;
			// -- -- Vertex B
			double nbx = bx;
			double nby = Math.cos(alpha) * by - Math.sin(alpha) * bz;
			double nbz = Math.cos(alpha) * bz + Math.sin(alpha) * by;

			// -- Rotate Beta secondly on top of alpha rotation
			// -- Vertex A
			ax = Math.cos(beta) * nax + Math.sin(beta) * naz;
			ay = nay;
			az = Math.cos(beta) * naz - Math.sin(beta) * nax;
			// -- Vertex B
			bx = Math.cos(beta) * nbx + Math.sin(beta) * nbz;
			by = nby;
			bz = Math.cos(beta) * nbz - Math.sin(beta) * nbx;

			// -- Rotate Gamma last on top of beta rotation
			// -- Vertex A
			nax = Math.cos(gamma) * ax - Math.sin(gamma) * ay;
			nay = Math.cos(gamma) * ay + Math.sin(gamma) * ax;
			naz = az;
			// -- Vertex B
			nbx = Math.cos(gamma) * bx - Math.sin(gamma) * by;
			nby = Math.cos(gamma) * by + Math.sin(gamma) * bx;
			nbz = bz;

			// Apply perspective adjustment
			nax = nax * persp_eye / (persp_eye + persp_model + naz);
			nay = nay * persp_eye / (persp_eye + persp_model + naz);
			nbx = nbx * persp_eye / (persp_eye + persp_model + nbz);
			nby = nby * persp_eye / (persp_eye + persp_model + nbz);

			// Actual drawing
			// if vertex label is "x" this marks the local x,y and z axes (coordinate
			// system)...
			if (va.label == 'x') {
				// Transform coordinates to screen presentation, for axes without magnification
				int bax = (int) (nax * 100);
				int bay = (int) (-nay * 100);
				int bbx = (int) (nbx * 100);
				int bby = (int) (-nby * 100);
				g.drawLine(60, 60, bax + 60, bay + 60);
				g.drawString(Character.toString(vb.label), bax + 60 - 3, bay + 60 - 5);
//				g.drawLine(60, 60, bbx + 60, bby + 60);
//				g.drawString(nodes.get(ndB).id, bbx + 60 - 3, bby + 60 - 5);
//				g.drawLine(60, 60, bcx + 60, bcy + 60);
//				g.drawString(nodes.get(ndC).id, bcx + 60 - 3, bcy + 60 - 5);
			} else {
				// ... all other triangles are real triangles
				// Transform coordinates to screen presentation
				int bax = (int) (nax * magnifier + xoff);
				int bay = (int) (-nay * magnifier + yoff);
				int bbx = (int) (nbx * magnifier + xoff);
				int bby = (int) (-nby * magnifier + yoff);
				g.drawLine(bax, bay, bbx, bby);
				g.drawString(Character.toString(va.label), bax - 3, bay - 5);
//				G.DRAWLINE(BBX, BBY, BCX, BCY);
//				G.DRAWSTRING(NODES.GET(NDB).ID, BBX - 3, BBY - 5);
//				G.DRAWLINE(BCX, BCY, BAX, BAY);
//				G.DRAWSTRING(NODES.GET(NDC).ID, BCX - 3, BCY - 5);
			}
		}
		// Draw help info on screen showing key combinations
		if (showhelp) {
			Font currentfont = g.getFont();
			g.setFont(new Font("SansSerif", Font.PLAIN, 20));
			g.drawString("W,S: Rotate x-axis", 10, 150);
			g.drawString("A,D: Rotate y-axis", 10, 180);
			g.drawString("Q,E: Rotate z-axis", 10, 210);
			g.drawString("X: Reset rotation", 10, 240);
			g.drawString("H: Display this help on,off", 10, 270);
			g.setFont(currentfont);
		}

	}

	ArrayList<Edge> edges = new ArrayList<>();
	String edgeID;

	private void doSubdivideTriangles() {
		for (Triangle tri : triangles) {
			// get Node indexes
//			int ndA = tri.vertexes[0];
//			int ndB = tri.vertexes[1];
//			int ndC = tri.vertexes[2];
//
//			// Determine edges
//			String fromID = nodes.get(ndA).id;
//			String toID = nodes.get(ndB).id;
//			if (fromID.compareTo(toID) < 0) {
//				edgeID = fromID + "|" + toID;
//			} else {
//				edgeID = toID + "|" + fromID;
//			}
//			;
//			if (edges.contains(new Edge(edgeID)))
//				;
		}
	}
}
