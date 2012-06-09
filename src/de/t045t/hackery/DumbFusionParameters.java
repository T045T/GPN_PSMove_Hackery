package de.t045t.hackery;

import java.util.Arrays;

import io.thp.psmove.PSMove;

public class DumbFusionParameters {

	public PSMove move;
	public int currentSample;
	public int samples;
	public float gyro[][];
	public float mag[][];
	public float accel[][];

	public float alpha;
	public float beta;
	public float gamma;

	public DumbFusionParameters(PSMove move, int samples) {
		this.move = move;
		this.samples = samples;
		currentSample = 0;

		// store 3 values for each sample, plus another triplet for the average: x, y, z 
		gyro = new float[samples+1][3];
		mag = new float[samples+1][3];
		accel = new float[samples+1][3];

		for (int i = 0; i < samples+1; i++) {
			Arrays.fill(gyro[i], 0);
			Arrays.fill(mag[i], 0);
			Arrays.fill(accel[i], 0);
		}
	}
}
