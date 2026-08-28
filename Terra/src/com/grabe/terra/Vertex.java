package com.grabe.terra;

public class Vertex {
	double x;
	double y;
	double z;
	char label;

	public Vertex(double x, double y, double z, char label) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.label = label;
	}

	public Vertex(double x, double y, double z) {
		this(x,y,z,'\0');
	}
}
