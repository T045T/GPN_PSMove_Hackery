package de.t045t.hackery;

public class ButtonLocation {
	private float angle;
	private int type;
	private int state;
	
	public ButtonLocation(float angle, int type) {
		this.state = 0;
		this.angle = angle;
		this.type = type;
	}
	
	public float getAngle() {
		return this.angle;
	}
	public int getType() {
		return this.type;
	}
	public int getState() {
		return this.state;
	}
	public void missed() {
		this.state = 2;
	}
	public void hit() {
		this.state = 1;
	}
}
