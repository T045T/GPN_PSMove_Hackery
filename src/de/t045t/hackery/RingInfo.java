package de.t045t.hackery;

import java.util.ArrayList;

public class RingInfo {
	private boolean hasQTE;
	private float angle;
	private ArrayList<ButtonLocation> buttonPos;
	private float sweetSpot;
	private float rotSpeed;
	private int width;
	
	public RingInfo(int width, float sweetSpot) {
		this.width = width;
		this.angle = 0f;
		this.hasQTE = false;
		this.buttonPos = null;
		this.sweetSpot = sweetSpot;
		this.rotSpeed = 0f;
	}
	
	public RingInfo(int width, ArrayList<ButtonLocation> buttonPos, float rotSpeed) {
		this.width = width;
		this.angle = 0f;
		this.hasQTE = true;
		this.buttonPos = buttonPos;
		this.rotSpeed = rotSpeed;
		this.sweetSpot = 0f;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public boolean hasQTE() {
		return this.hasQTE;
	}
	
	public float getAngle() {
		return this.angle;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public ArrayList<ButtonLocation> getButtonPos() {
		return this.buttonPos;
	}
	public float getSweetSpot() {
		return this.sweetSpot;
	}
	public float getRotSpeed() {
		return this.rotSpeed;
	}
}
