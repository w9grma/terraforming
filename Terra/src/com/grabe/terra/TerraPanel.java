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
	final ArrayList<Node> nodes = new ArrayList<>();
	final ArrayList<Triangle> triangles = new ArrayList<>();
	double magnifier = 1000;
	double alpha = Math.PI/8;   // Drehung um x
	double beta = 0;  			// Drehung um y
	double gamma = 0;			// Drehung um z
	double persp_eye = 1;
	double persp_model = 1.5;
	boolean showhelp = false;
	
	// Konstruktor
	public TerraPanel() {

		addKeyListener(this); // KeyListener aktivieren
		setFocusable(true); // Panel kann Fokus erhalten

		nodes.add(new Node(	-Math.cos(Math.PI/6)/2, 		0, 				-0.25, 	"A"));
		nodes.add(new Node(	 Math.cos(Math.PI/6)/2, 		0, 				-0.25, 	"B"));
		nodes.add(new Node(	0, 								0, 				0.5, 	"C"));
		triangles.add(new Triangle(0, 1, 2, "ABC"));

		nodes.add(new Node(	 Math.cos(Math.PI/6), 			0, 				0.5, 	"D"));
		triangles.add(new Triangle(1, 3, 2, "BDC"));

		nodes.add(new Node(	-Math.cos(Math.PI/6), 			0, 				0.5, 	"E"));
		triangles.add(new Triangle(0, 2, 4, "ACE"));

		nodes.add(new Node(	0, 								0, 				-1, 	"F"));
		triangles.add(new Triangle(5, 1, 0, "FBA"));

		// Add special nodes and triangle for local xyz axes
		nodes.add(new Node(1,0,0,"x"));
		nodes.add(new Node(0,1,0,"y"));
		nodes.add(new Node(0,0,1,"z"));
		triangles.add(new Triangle(6,7,8,"-1"));
		
		setBackground(Color.WHITE);
	}

	// KeyListener-Methoden (nur keyPressed relevant)
	@Override
	public void keyPressed(KeyEvent e) {
		
		doSubdivideTriangles();
		
		switch (e.getExtendedKeyCode()) {
			// y-axis rotation
			case KeyEvent.VK_A:
			case KeyEvent.VK_NUMPAD4: 	beta -= Math.PI/100; break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_NUMPAD6: 	beta += Math.PI/100; break;
			// x-axis rotation
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_S: 		alpha -= Math.PI/100; break;
			case KeyEvent.VK_W:
			case KeyEvent.VK_NUMPAD8: 	alpha += Math.PI/100; break;
			// magnification
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_PAGE_UP: magnifier += 50; break;
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_PAGE_DOWN: magnifier -= 50; break;
			// z-axis rotation
			case KeyEvent.VK_E:
			case KeyEvent.VK_NUMPAD9: gamma += Math.PI/100; break;
			case KeyEvent.VK_Q:
			case KeyEvent.VK_NUMPAD7: gamma -= Math.PI/100; break;
			// reset rotation
			case KeyEvent.VK_X:
			case KeyEvent.VK_NUMPAD5: alpha = beta = gamma = 0; break;
			// show help
			case KeyEvent.VK_H: showhelp = !showhelp; break;
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
		g.drawLine(10, y-10, 110, y-10); g.drawString("X", 110, y-10);
		g.drawLine(10, y-10, 10, y-110); g.drawString("Y", 10, y-110);
		g.drawLine(10, y-10, 36, y-36); g.drawString("Z", 36, y-36);
		
		
		// Linien zeichnen
		g.setColor(Color.BLUE);
		for (Triangle triangle : triangles) {
			// Eckpunkte holen
			int ndA = triangle.vertexes[0];
			int ndB = triangle.vertexes[1];
			int ndC = triangle.vertexes[2];
			// A Eckpunkt des Dreiecks
			double ax = nodes.get(ndA).x;
			double ay = nodes.get(ndA).y;
			double az = nodes.get(ndA).z;
			// B Eckpunkt des Dreiecks
			double bx = nodes.get(ndB).x;
			double by = nodes.get(ndB).y;
			double bz = nodes.get(ndB).z;
			// C Eckpunkt des Dreiecks
			double cx = nodes.get(ndC).x;
			double cy = nodes.get(ndC).y;
			double cz = nodes.get(ndC).z;
			
			// Isometric transformation of vertex coordinates for rotation of x-axis (alpha),
			// y-axis (beta) and z-axis (gamma)
			// -- Rotate Alpha first
			// -- -- Vertex A
			double nax = ax; 
			double nay = Math.cos(alpha) * ay - Math.sin(alpha) * az;
			double naz = Math.cos(alpha) * az + Math.sin(alpha) * ay;
			// -- -- Vertex B
			double nbx = bx; 
			double nby = Math.cos(alpha) * by - Math.sin(alpha) * bz;
			double nbz = Math.cos(alpha) * bz + Math.sin(alpha) * by;
			// -- -- Vertex c
			double ncx = cx; 
			double ncy = Math.cos(alpha) * cy - Math.sin(alpha) * cz;
			double ncz = Math.cos(alpha) * cz + Math.sin(alpha) * cy;

			// -- Rotate Beta secondly on top of alpha rotation
			// -- Vertex A
			ax = Math.cos(beta) * nax + Math.sin(beta) * naz;
			ay = nay; 
			az = Math.cos(beta) * naz - Math.sin(beta) * nax;
			// -- Vertex B
			bx = Math.cos(beta) * nbx + Math.sin(beta) * nbz;
			by = nby; 
			bz = Math.cos(beta) * nbz - Math.sin(beta) * nbx;
			// -- Vertex C
			cx = Math.cos(beta) * ncx + Math.sin(beta) * ncz;
			cy = ncy; 
			cz = Math.cos(beta) * ncz - Math.sin(beta) * ncx;
			
			// -- Rotate Gamma last on top of beta rotation
			// -- Vertex A
			nax = Math.cos(gamma) * ax - Math.sin(gamma) * ay;
			nay = Math.cos(gamma) * ay + Math.sin(gamma) * ax; 
			naz = az;
			// -- Vertex B
			nbx = Math.cos(gamma) * bx - Math.sin(gamma) * by;
			nby = Math.cos(gamma) * by + Math.sin(gamma) * bx; 
			nbz = bz;
			// -- Vertex C
			ncx = Math.cos(gamma) * cx - Math.sin(gamma) * cy;
			ncy = Math.cos(gamma) * cy + Math.sin(gamma) * cx; 
			ncz = cz;
			
			
			// Apply perspective adjustment
			nax = nax * persp_eye / (persp_eye + persp_model + naz);  
			nay = nay * persp_eye / (persp_eye + persp_model + naz);  
			nbx = nbx * persp_eye / (persp_eye + persp_model + nbz);  
			nby = nby * persp_eye / (persp_eye + persp_model + nbz);  
			ncx = ncx * persp_eye / (persp_eye + persp_model + ncz);  
			ncy = ncy * persp_eye / (persp_eye + persp_model + ncz);  
			
						
			// Acutal drawing
			// if triangle id is "-1" this marks the local x,y and z axes (coordinate system)...
			if (triangle.id == "-1") {
				// Transform coordinctes to screen presentation, for axes without magnification
				int bax = (int) (nax * 100 ); 
				int bay = (int) (- nay * 100 ); 
				int bbx = (int) (nbx * 100 ); 
				int bby = (int) (- nby * 100 ); 
				int bcx = (int) (ncx * 100 ); 
				int bcy = (int) (- ncy * 100 ); 
				g.drawLine(60  , 60  , bax + 60  , bay + 60  );
				g.drawString(nodes.get(ndA).id, bax + 60   - 3, bay + 60   - 5);
				g.drawLine(60  , 60  , bbx + 60  , bby + 60  );
				g.drawString(nodes.get(ndB).id, bbx + 60   - 3, bby + 60   - 5);
				g.drawLine(60  , 60  , bcx + 60  , bcy + 60  );
				g.drawString(nodes.get(ndC).id, bcx + 60   - 3, bcy + 60   - 5);
			} else {
			// ... all other triangles are real triangles
				// Transform coordinctes to screen presentation
			int bax = (int) (nax * magnifier + xoff); 
			int bay = (int) (- nay * magnifier + yoff); 
			int bbx = (int) (nbx * magnifier + xoff); 
			int bby = (int) (- nby * magnifier + yoff); 
			int bcx = (int) (ncx * magnifier + xoff); 
			int bcy = (int) (- ncy * magnifier + yoff); 
			g.drawLine(bax, bay, bbx, bby);
			g.drawString(nodes.get(ndA).id, bax-3, bay-5);
			g.drawLine(bbx, bby, bcx, bcy);
			g.drawString(nodes.get(ndB).id, bbx-3, bby-5);
			g.drawLine(bcx, bcy, bax, bay);
			g.drawString(nodes.get(ndC).id, bcx-3, bcy-5);
			}
		}
		// Draw help info on scree showing key combinations
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
		for (Triangle tri: triangles) {
			// get Node indexes
			int ndA = tri.vertexes[0];
			int ndB = tri.vertexes[1];
			int ndC = tri.vertexes[2];
			
			// Determine edges
			String fromID = nodes.get(ndA).id;
			String toID = nodes.get(ndB).id;
			if (fromID.compareTo(toID) < 0) {
				edgeID = fromID + "|" + toID;
			} else {
				edgeID = toID + "|" + fromID;
			};
			if (edges.contains(new Edge( edgeID )));
		}
	}
}
