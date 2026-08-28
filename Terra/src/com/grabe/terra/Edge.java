package com.grabe.terra;

public class Edge {
	final Vertex p1;
	final Vertex p2;
	final Vertex pm;

	public Edge(Vertex p1, Vertex p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.pm = null;
	}
}
