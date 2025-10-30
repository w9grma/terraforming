package com.Grabe.Terra;

public class Node {
	double x;
	double y;
	double z;
	String id;

	public Node(double x, double y, double z, String id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	
	public Node(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
