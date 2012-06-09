package de.t045t.hackery;

import java.util.ArrayList;

public class RingInfo {
	private boolean hasQTE;
	private boolean hit;
	private float angle;
	private ArrayList<ButtonLocation> buttonPos;
	private float sweetSpot;
	private float rotSpeed;
	private int width;
	private int lastSeen;
	
	public RingInfo(int width, float sweetSpot) {
		this.lastSeen = -1;
		this.hit = false;
		this.width = width;
		this.angle = 0f;
		this.hasQTE = false;
		this.buttonPos = null;
		this.sweetSpot = (float) (sweetSpot % (Math.PI * 2f));
		this.rotSpeed = 0f;
	}
	
	public RingInfo(int width, ArrayList<ButtonLocation> buttonPos, float rotSpeed) {
		this.lastSeen = -1;
		this.hit = false;
		this.width = width;
		this.angle = 0f;
		this.hasQTE = true;
		this.buttonPos = buttonPos;
		this.rotSpeed = rotSpeed;
		this.sweetSpot = 0f;
	}
	
	public int getLastSeen() {
		return this.lastSeen;
	}
	public void setLastSeen(int lastSeen) {
		this.lastSeen = lastSeen;
	}
	
	public boolean isHit() {
		return this.hit;
	}
	
	public void hit() {
		this.hit = true;
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
		this.angle = (float) (angle % (Math.PI * 2));
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
	
	public boolean allHit() {
		if (!this.hasQTE) {
			return this.hit;
		} else {
			boolean retVal = true;
			for (ButtonLocation l : buttonPos) {
				retVal &= l.getState() == 1;
			}
			return retVal;
		}
	}
}
