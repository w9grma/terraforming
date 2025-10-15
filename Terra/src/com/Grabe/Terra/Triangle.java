package com.Grabe.Terra;

public class Triangle {
	int[] vertexes = new int[3];
	int id;

	// Konstruktor
	Triangle(int a,int b, int c, int id) {
		vertexes[0] = a;
		vertexes[1] = b;
		vertexes[2] = c;
		this.id = id;
	}
	
}
