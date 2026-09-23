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
	int iterations = 0;
	ArrayList<Vertex> vertices = new ArrayList<>();
	ArrayList<Triangle> triangles = new ArrayList<>();
	ArrayList<Edge> kanten = new ArrayList<>();
	double magnifier = 5000;
	double alpha = 0; // Drehung um x
	double beta = 0; // Drehung um y
	double gamma = 0; // Drehung um z
	final double default_alpha = Math.toRadians(45);
	final double default_beta = Math.toRadians(45);
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
	char vertexLabel;

	public char getVertexLabelNext() {
		if (vertexLabel < 'w') {
			vertexLabel++;
			return vertexLabel;
		} else {
			return ' ';
		}
	}

	public void resetVertexLabel() {
		vertexLabel = 'A';
		vertexLabel--;
	}

	public void initGridData() {
		// Empty lists
		triangles.clear();
		kanten.clear();
		vertices.clear();

		resetVertexLabel();

		// Add special vertices, edges and triangle for local xyz axes
		Vertex v0 = new Vertex(0, 0, 0);
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

		// 1. Initial triangle ABC
		Vertex va = new Vertex(-Math.cos(pi / 6) / 2, 0, -0.25, getVertexLabelNext()); // (cos pi/6) / 2 = 0,433...
		vertices.add(va);
		Vertex vb = new Vertex(Math.cos(pi / 6) / 2, 0, -0.25, getVertexLabelNext());
		vertices.add(vb);
		Vertex vc = new Vertex(0, 0, 0.5, getVertexLabelNext());
		vertices.add(vc);

		Edge eab = new Edge(va, vb);
		kanten.add(eab);
		Edge ebc = new Edge(vb, vc);
		kanten.add(ebc);
		Edge eca = new Edge(vc, va);
		kanten.add(eca);

		Triangle tabc = new Triangle(eab, ebc, eca);
		triangles.add(tabc);

		iterations = 0;
	}

	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten

		setBackground(Color.WHITE);
		setFont(new Font("Monospaced", Font.PLAIN, 20));

		alpha = default_alpha;
		beta = default_beta;

		initGridData();
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
			beta = default_beta;
			gamma = 0;
			break;

		// reset iteration (complete grid reset to default)
		case KeyEvent.VK_R:
			initGridData();
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
		int drawline = 150;
		string2draw = "Rotation: x/y/z: ";
		string2draw += Math.round(Math.toDegrees(alpha)) + " / ";
		string2draw += Math.round(Math.toDegrees(beta)) + " / ";
		string2draw += Math.round(Math.toDegrees(gamma));
		g.drawString(string2draw, 10, drawline);

		drawline += 30;
		g.drawString("Iterationen: " + iterations, 10, drawline);

		drawline += 30;
		Integer notriangles = (int) Math.pow(4, iterations);
		g.drawString("Dreiecke: " + triangles.size() + " (erwartet " + notriangles + ")", 10, drawline);

		drawline += 30;
		Double noedges = Math.pow(2, iterations);
		noedges = (noedges * noedges / 2 + noedges / 2) * 3;
		g.drawString("Kanten: " + (kanten.size() - 3) + " (e " + noedges + ")", 10, drawline); // substract 3 from real value for the local coord. system

		drawline += 30;
		Double novert = Math.pow(2, iterations) + 1;
		novert = (novert * novert / 2 + novert / 2);
		g.drawString("Punkte: " + (vertices.size() - 4) + "(e " + novert + ")", 10, drawline); // substract 4 from real value for the local coord. system

		drawline += 50;
		g.drawString("Press h for help", 10, drawline);

		drawline += 30;

		// Draw help info on screen showing key combinations
		if (showhelp) {
			g.drawString("W,S: Rotate x-axis", 10, drawline);
			drawline += 30;
			g.drawString("A,D: Rotate y-axis", 10, drawline);
			drawline += 30;
			g.drawString("Q,E: Rotate z-axis", 10, drawline);
			drawline += 30;
			g.drawString("+,-: Zoom in and out", 10, drawline);
			drawline += 30;
			g.drawString(" X : Reset rotation", 10, drawline);
			drawline += 30;
			g.drawString(" C : Display local x,y,z axis: " + showaxis, 10, drawline);
			drawline += 30;
			g.drawString(" V : Display vertex coordinates: " + drawvertexcoordinates, 10, drawline);
			drawline += 30;
			g.drawString(" N : Display vertex names: " + showvertexnames, 10, drawline);
			drawline += 30;
			g.drawString("ESC: Exit", 10, drawline);
			drawline += 30;
			g.drawString("SPC: Do Subdivide!", 10, drawline);
			drawline += 30;
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
		iterations++;
		// first of all we need the existing edges to be split in two edges of same
		// length. middle (shared) vertex gets adjusted in height
		for (Edge edge : kanten) {

			// do not sub-divide local coordinate system edges
			if (edge.p1.label == 'x' || edge.p1.label == 'y' || edge.p1.label == 'z')
				continue;

			// check if middle vertex already processed for another triangle using the same edge
			if (edge.pm == null) {
				// new vertex gets average of all coordinates
				edge.pm = new Vertex(0, 0, 0, getVertexLabelNext());
				vertices.add(edge.pm);
				edge.pm.x = (edge.p1.x + edge.p2.x) / 2;
				edge.pm.y = (edge.p1.y + edge.p2.y) / 2;
				edge.pm.z = (edge.p1.z + edge.p2.z) / 2;

				// new vertex coordinates are adjusted randomly in height, i.e. y-dimension
				double deltah = (Math.random() - 0.5) * 0.2;
				deltah = deltah / iterations / iterations; // reduce change in height according to progress in iterations
				edge.pm.y += deltah;
			} else {
				System.out.println("Bereits geteilte Kante gefunden.");
			}
		}

		// with the help of the new middle vertices we can construct the new triangles
		// Loop over all existing triangles, for each construct the 4 new successors
		ArrayList<Triangle> newtriangles = new ArrayList<>();
		ArrayList<Edge> newedges = new ArrayList<>();
		ArrayList<Edge> eadjacent = new ArrayList<>();
		ArrayList<Edge> einnertriangle = new ArrayList<>();

		// at first add edges of local coordinate system to the temporary lists
		newedges.add(kanten.get(0));
		newedges.add(kanten.get(1));
		newedges.add(kanten.get(2));

		int i = 0;
		for (Triangle told : triangles) {
			i++;
			// get the 3 edges and tree vertices first, do not respect any order or orientation
			ArrayList<Vertex> toldvert = told.getVertices(told);
			ArrayList<Edge> toldedges = new ArrayList<>();
			toldedges.add(told.e1);
			toldedges.add(told.e2);
			toldedges.add(told.e3);

			String triname = "";
			for (Vertex v : toldvert)
				triname += v.label;
			System.out.println("Processing triangle " + i + ": " + triname);

			// Now that we have got the 3 vertices and 3 edges for the current triangle in temporary ArrayLists  
			// let's construct the 4 child triangles but do NOT pay attention to any sequence of vertices or edges  
			// or even orientation of the edges. Vertices and edges are placed in the Lists in a random order.
			// Idea: 1. Loop through the vertices 
			//       2. Find the two edges connected with current vertex
			//       3. Construct child triangle located next to the current vertexes corner of mother triangle
			//			a Loop over the two edges of 2) and create a new edges for both 
			// 			  with end points current vertex and middle point of old edge (check if edge already created for neighbor triangle)
			//			b Create a new edge with end points as the two middle points of the two old edges
			//			c Remember the new edge of 2b) for the inner new child triangle
			//       4. Construct child triangle located in the middle of the mother triangle with the help of 3c)

			einnertriangle.clear();
			for (Vertex v : toldvert) {
				// 2) Find the two adjacent edges (of the 3 existing) for the current vertex
				eadjacent.clear();
				for (Edge e : toldedges) {
					if (e.p1 == v || e.p2 == v) {
						eadjacent.add(e);
					}
				}
				if (eadjacent.size() != 2)
					System.out.println(
							"Fehler! Beim Subdivide konnten zu einer Ecke die zugehörigen anliegenden Kanten nicht gefunden werden.");

				// 3) Construct the three edges for the child triangle adjacent to the current corner (vertex) of the mother triangle
				Edge ena = new Edge(v, eadjacent.get(0).pm);
				Edge enb = new Edge(v, eadjacent.get(1).pm);
				Edge enc = new Edge(eadjacent.get(0).pm, eadjacent.get(1).pm);
				Triangle tnew = new Triangle(ena, enb, enc);

				// Save new edges and triangle to the temporary list
				newedges.add(ena);
				newedges.add(enb);
				newedges.add(enc);
				newtriangles.add(tnew);

				// 3c) remember edge for inner triangle
				einnertriangle.add(enc);
			}
			// 4) create inner triangle and add it
			Triangle tnew = new Triangle(einnertriangle.get(0), einnertriangle.get(1), einnertriangle.get(2));
			newtriangles.add(tnew);
		}

		System.out.println("----------------------------------------------");

		// Throw away old items and take over the new ones, vertices stay the same,no need to replace them
		triangles = newtriangles;
		kanten = newedges;
	}
}
