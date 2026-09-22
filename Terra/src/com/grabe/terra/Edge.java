package com.grabe.terra;

import java.util.ArrayList;

public class Edge {
	final Vertex p1;
	final Vertex p2;
	Vertex pm;
	ArrayList<Edge> Newedges;

	public Edge(Vertex p1, Vertex p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.pm = null;
	}
}
