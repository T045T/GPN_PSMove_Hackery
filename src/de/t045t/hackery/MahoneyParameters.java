package de.t045t.hackery;

public class MahoneyParameters {
	
	public float twoKp;
	public float twoKi;
	public float integralFBx;
	public float integralFBy;
	public float integralFBz;
	public float sampleFreq;
	public float[] q;
	
	public MahoneyParameters() {
		sampleFreq = 512.0f;
		twoKp = 1.0f;
		twoKi = 0.0f;
		integralFBx = 0.0f;
		integralFBy = 0.0f;
		integralFBz = 0.0f;

		q = new float[4];
		q[0] = 1.0f;
		q[1] = 0.0f;
		q[2] = 0.0f;
		q[3] = 0.0f;
	}
}
