package com.grabe.terra;

import java.util.ArrayList;

public class Triangle {
	final Edge e1;
	final Edge e2;
	final Edge e3;

	// Konstruktor
	Triangle(Edge e1, Edge e2, Edge e3) {
		this.e1 = e1;
		this.e2 = e2;
		this.e3 = e3;
	}
	
	public ArrayList<Vertex> getVertices(Triangle tri) {
		ArrayList<Vertex> trivertices = new ArrayList<>();
		trivertices.add(tri.e1.p1);
		trivertices.add(tri.e1.p2);
		if (!trivertices.contains(tri.e2.p1)) trivertices.add(tri.e2.p1);
		if (!trivertices.contains(tri.e2.p2)) trivertices.add(tri.e2.p2);
		if (!trivertices.contains(tri.e3.p1)) trivertices.add(tri.e3.p1);
		if (!trivertices.contains(tri.e3.p2)) trivertices.add(tri.e3.p2);
		return trivertices;
	}
	
}
