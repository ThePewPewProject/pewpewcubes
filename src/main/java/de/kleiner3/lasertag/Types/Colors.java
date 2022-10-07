package de.kleiner3.lasertag.Types;

public enum Colors {
	RED(255, 0, 0),
	GREEN(0, 255, 0),
	BLUE(0, 0, 255),
	ORANGE(255, 128, 0),
	TEAL(0, 128, 255),
	PINK(255, 0, 255);
	
	Colors(int r, int g, int b)
	{
		this.r = (r & 0xFF);
		this.g = (g & 0xFF);
		this.b = (b & 0xFF);
		
		intValue = this.r << 16 | this.g << 8 | this.b;
	}
	
	private int intValue;
	private int r;
	private int g;
	private int b;
	
	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
	
	public int getValue() {
		return intValue;
	}
	
	public float[] getFloatArray() {
		return null;
	}
}
