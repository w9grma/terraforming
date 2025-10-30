package com.Grabe.Terra;

public class Edge {
	String id;
	int node1;
	int node2;
	int node3;
	
	public Edge(String id, int node1, int node2, int node3) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
		this.node3 = node3;
	}

	public Edge(String id, int node1, int node2) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
	}
}
