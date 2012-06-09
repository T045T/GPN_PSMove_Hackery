package de.t045t.hackery;

import processing.core.*;
import io.thp.psmove.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class PSMoveSketch extends PApplet{

	private ArrayList<PSMove> moves;
	private ArrayList<DumbFusionParameters> dParamList;
	private ArrayList<float[]> angleList;
	private ArrayList<ButtonLocation> testButtonPairs;
	private ArrayList<RingInfo> rings;
	
	private float currentAngle;

	public void setup() {
		// Initialize MAGIC WANDS

		currentAngle = 0;
		angleList = new ArrayList<float[]>();
		moves = new ArrayList<PSMove>();
		dParamList = new ArrayList<DumbFusionParameters>();
		for (int i = 0; i < psmoveapi.psmove_count_connected(); i++) {
			moves.add(new PSMove(i));
			int foo = moves.get(i).getConnection_type();
			if (foo == ConnectionType.Conn_Bluetooth.ordinal()) {
				System.out.println("Bluetooth");
			} else if (foo == ConnectionType.Conn_USB.ordinal()) {
				System.out.println("USB");
			} else {
				System.out.println("Unknown");
			}
			dParamList.add(new DumbFusionParameters(moves.get(i), 2));
			angleList.add(new float[3]);
			angleList.add(new float[3]);
			Arrays.fill(angleList.get(2*i), 0);
			Arrays.fill(angleList.get(2*i+1), 0);
		}

		// Set up Processing canvas
		frameRate(60);
		size(800, 600, P3D);
		noStroke();
		colorMode(RGB, 1);
		fill(0.4f);
		angleList.add(new float[3]);
		Arrays.fill(angleList.get(0), 0);

		testButtonPairs = new ArrayList<ButtonLocation>();
		testButtonPairs.add(new ButtonLocation(0, 0));
		testButtonPairs.add(new ButtonLocation((float) (Math.PI /2f), 1));
		testButtonPairs.add(new ButtonLocation((float) (Math.PI), 2));
		testButtonPairs.add(new ButtonLocation((float) (3* Math.PI /2f), 3));
		
		rings = new ArrayList<RingInfo>();
		rings.add(new RingInfo(90, testButtonPairs, (float) Math.PI / 30));
		rings.add(new RingInfo(70, (float) (Math.random() * 2f * Math.PI)));
		rings.add(new RingInfo(70, (float) (Math.random() * 2f * Math.PI)));
	}

	boolean btnPressed;
	float startAngle;
	public void draw() {
		DumbFusionParameters tmpPar = null;
		background(125,125,125);
		stroke(255,0,0);
		for (int i = 0; i < moves.size(); i++) {
			PSMove move = moves.get(i);
			if (move.poll() > 0) {


				dumbFusion(dParamList.get(i), 
						(float) (move.getGx() * Math.PI / 2000.0f), (float) (move.getGy() * Math.PI / 2000.0f), (float) (move.getGz() * Math.PI / 2000.0f), 
						move.getAx() / 4200f, move.getAy() / 4200f, move.getAz() / 4200f,
						move.getMx() / 225f, move.getMy() / 225f, move.getMz() / 225f
						);

				tmpPar = dParamList.get(0);

				if ((move.get_buttons() & Button.Btn_MOVE.swigValue()) > 0) {
					if (!btnPressed) {
						startAngle = tmpPar.alpha;
						btnPressed = true;
					}
				} else {
					if (btnPressed) {
						currentAngle = currentAngle - (tmpPar.alpha - startAngle);
						btnPressed = false;
					}
				}
				/*
				drawSegment(400, 300, 50, 5, 30, tmpPar.alpha, tmpPar.alpha + 0.65f);
				drawSegment(400, 300, 75, 5, 55, tmpPar.beta, tmpPar.beta + 0.65f);
				drawSegment(400, 300, 100, 5, 80, tmpPar.gamma, tmpPar.gamma + 0.65f);
				 */
			}
		}
		
		int lastWidth = 20;
		for (int i = 0; i < rings.size(); i++) {
			RingInfo r = rings.get(i);
			if (r.hasQTE()) {
				drawCenteredQTERing(lastWidth, r.getWidth(), r.getAngle(), r.getButtonPos(), 0, 255, 0);
			} else {
				drawCenteredRing(lastWidth, r.getWidth(), r.getAngle(), 255,0, 0);
			}
			lastWidth += r.getWidth();
		}

		//		if (moves.size() > 0) {
		//			int trigger = moves.get(0).get_trigger();
		//			currentAngle = (float) (trigger / 255.0) * TWO_PI;
		//		} else {
		//			currentAngle = (float) (mouseX / 800.0) * TWO_PI;
		//		}
		
//		if (btnPressed && tmpPar != null) {
//			float tmpAngle = currentAngle - (tmpPar.alpha - startAngle);
//			System.out.println(tmpAngle);
//			drawCenteredQTERing(100, 50, tmpAngle, testButtonPairs, 255, 0, 0);
//			//drawSegment(400, 300, 220, 50/*(int) (currentAngle / (PI / 36))*/, 280, tmpAngle, (float) (tmpAngle + (Math.PI * 2)));
//		} else {
//			drawCenteredQTERing(100, 50, currentAngle, testButtonPairs, 255, 0, 0);
//			//drawSegment(400, 300, 220, 50/*(int) (currentAngle / (PI / 36))*/, 280, currentAngle, (float) (currentAngle+ (Math.PI * 2)));
//		}

	}

	void updateRadialDial(int x, int y) {

	}

	private void drawSegment(int x, int y, int segHeight, int segments, int innerRadius, int startAngle, int endAngle) {
		drawSegment(x, y, segHeight, segments, innerRadius, (float) (startAngle / 360.0) * TWO_PI, (float) (endAngle / 360.0) * TWO_PI);
	}
	private void drawSegment(int x, int y, int segHeight, int segments, int innerRadius, float startAngle, float endAngle) {
		fill(0,0,0);
		beginShape(QUAD_STRIP);
		float tmp = (endAngle-startAngle) / segments;
		for(int i = 0; i < segments+1; i++) {
			vertex(x+segHeight*cos(startAngle+(tmp*i)), y+segHeight*sin(startAngle+(tmp*i)));
			vertex(x+innerRadius*cos(startAngle+(tmp*i)), y+innerRadius*sin(startAngle+(tmp*i)));
		}
		endShape(CLOSE);
	}

	private void drawCenteredRing(int innerRadius, int width, float rotation, int r, int g, int b) {
		stroke(r, g, b);
		drawSegment(this.width / 2, this.height / 2, innerRadius + width, 50, innerRadius, rotation, (float) (rotation+ (Math.PI * 2)));
	}

	private void drawCenteredQTERing(int innerRadius, int width, float rotation, ArrayList<ButtonLocation> buttonPositions, int r, int g, int b) {
		drawCenteredRing(innerRadius, width, rotation, r, g, b);
		for (ButtonLocation e : buttonPositions) {
			pushMatrix();
			translate(this.width / 2, this.height / 2);
			float foo = rotation + e.getAngle();
			rotate(rotation + e.getAngle());
			translate(0, innerRadius + (width / 2));
			ellipseMode(CENTER);
			float circleWidth = width - width /5f;
			float circleCoord = sin((float) Math.PI / 4f) * circleWidth/2f;
			circleCoord -= circleCoord / 5f;
			ellipse(0, 0, circleWidth, circleWidth);
			switch(e.getType()) {
			case 0: // CROSS
				line(-1 * circleCoord, -1*circleCoord, circleCoord, circleCoord);
				line(-1 * circleCoord, circleCoord, circleCoord, -1*circleCoord);
				break;
			case 1: // CIRCLE
				ellipse(0, 0, circleWidth - circleWidth / 2.5f, circleWidth - circleWidth / 2.5f);
				break;
			case 2: // SQUARE
				rect(-1 * circleCoord, -1 * circleCoord, 2*circleCoord, 2*circleCoord);
				break;
			case 3: // TRIANGLE
				float tmpX = cos((float) Math.PI / 6f) * circleWidth/-2f;
				tmpX -= tmpX / 5f;
				float tmpY = sin((float) Math.PI / 6f) * circleWidth/-2f;
				tmpY -= tmpY / 5f;
				line(0, (circleWidth/2f) - (circleWidth/10f), tmpX, tmpY);
				line(tmpX, tmpY, -1*tmpX, tmpY);
				line(-1*tmpX, tmpY, 0, (circleWidth/2f) - (circleWidth/10f));
				break;
			}
			popMatrix();
		}
	}

	private void dumbFusion(DumbFusionParameters param, float gx, float gy, float gz, float ax, float ay, float az, float mx, float my, float mz) {
		param.gyro[param.currentSample][0] = gx;
		param.gyro[param.currentSample][1] = gy;
		param.gyro[param.currentSample][2] = gz;

		param.accel[param.currentSample][0] = ax;
		param.accel[param.currentSample][1] = ay;
		param.accel[param.currentSample][2] = az;

		param.mag[param.currentSample][0] = mx;
		param.mag[param.currentSample][1] = my;
		param.mag[param.currentSample][2] = mz;

		param.currentSample = ((param.currentSample == param.samples - 1) ? 0 : param.currentSample + 1);

		for (int i = 0; i < param.samples; i++) {
			param.gyro[param.samples][0] += param.gyro[i][0];
			param.gyro[param.samples][1] += param.gyro[i][1];
			param.gyro[param.samples][2] += param.gyro[i][2];

			param.accel[param.samples][0] += param.accel[i][0];
			param.accel[param.samples][1] += param.accel[i][1];
			param.accel[param.samples][2] += param.accel[i][2];

			param.mag[param.samples][0] += param.mag[i][0];
			param.mag[param.samples][1] += param.mag[i][1];
			param.mag[param.samples][2] += param.mag[i][2];
		}
		param.gyro[param.samples][0] /= (float) param.samples;
		param.gyro[param.samples][1] /= (float) param.samples;
		param.gyro[param.samples][2] /= (float) param.samples;

		param.accel[param.samples][0] /= (float) param.samples;
		param.accel[param.samples][1] /= (float) param.samples;
		param.accel[param.samples][2] /= (float) param.samples;

		param.mag[param.samples][0] /= (float) param.samples;
		param.mag[param.samples][1] /= (float) param.samples;
		param.mag[param.samples][2] /= (float) param.samples;

		param.alpha = (atan2(param.accel[param.samples][0], param.accel[param.samples][1]));
		//+ atan2(param.mag[param.samples][0], param.mag[param.samples][1])) / 2f;
		param.beta = (atan2(param.accel[param.samples][0], param.accel[param.samples][2]));
		//+ atan2(param.mag[param.samples][0], param.mag[param.samples][2])) / 2f;
		//System.out.println((atan2(param.accel[param.samples][1], param.accel[param.samples][2]) *180f /Math.PI) 
		//		+ "\t" + (atan2(param.mag[param.samples][1], param.mag[param.samples][2]) * 180f /Math.PI));
		param.gamma = (atan2(param.accel[param.samples][1], param.accel[param.samples][2]));
		//+ atan2(param.mag[param.samples][1], param.mag[param.samples][2])) / 2f;
	}


	private void Mahoney(MahoneyParameters params, float gx, float gy, float gz, float ax, float ay, float az, float mx, float my, float mz) {

		float recipNorm;
		float q0q0, q0q1, q0q2, q0q3, q1q1, q1q2, q1q3, q2q2, q2q3, q3q3;  
		float hx, hy, bx, bz;
		float halfvx, halfvy, halfvz, halfwx, halfwy, halfwz;
		float halfex, halfey, halfez;
		float qa, qb, qc;

		// Use IMU algorithm if magnetometer measurement invalid (avoids NaN in magnetometer normalisation)
		if((mx == 0.0f) && (my == 0.0f) && (mz == 0.0f)) {
			MahonyAHRSupdateIMU(params, gx, gy, gz, ax, ay, az);
			return;
		}

		// Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
		if(!((ax == 0.0f) && (ay == 0.0f) && (az == 0.0f))) {

			// Normalise accelerometer measurement
			recipNorm = invSqrt(ax * ax + ay * ay + az * az);
			ax *= recipNorm;
			ay *= recipNorm;
			az *= recipNorm;     

			// Normalise magnetometer measurement
			recipNorm = invSqrt(mx * mx + my * my + mz * mz);
			mx *= recipNorm;
			my *= recipNorm;
			mz *= recipNorm;

			/*
			// Normalise gyroscope measurement
			recipNorm = invSqrt(gx * gx + gy * gy + gz * gz);
			gx *= recipNorm;
			gy *= recipNorm;
			gz *= recipNorm;
			 */

			//System.out.println(gx + "\t" + gy + "\t" + gz + "\t" + ax + "\t" + ay + "\t" + az + "\t" + mx + "\t" + my + "\t" + mz + "\t");


			// Auxiliary variables to avoid repeated arithmetic
			q0q0 = params.q[0] * params.q[0];
			q0q1 = params.q[0] * params.q[1];
			q0q2 = params.q[0] * params.q[2];
			q0q3 = params.q[0] * params.q[3];
			q1q1 = params.q[1] * params.q[1];
			q1q2 = params.q[1] * params.q[2];
			q1q3 = params.q[1] * params.q[3];
			q2q2 = params.q[2] * params.q[2];
			q2q3 = params.q[2] * params.q[3];
			q3q3 = params.q[3] * params.q[3];   

			// Reference direction of Earth's magnetic field
			hx = 2.0f * (mx * (0.5f - q2q2 - q3q3) + my * (q1q2 - q0q3) + mz * (q1q3 + q0q2));
			hy = 2.0f * (mx * (q1q2 + q0q3) + my * (0.5f - q1q1 - q3q3) + mz * (q2q3 - q0q1));
			bx = sqrt(hx * hx + hy * hy);
			bz = 2.0f * (mx * (q1q3 - q0q2) + my * (q2q3 + q0q1) + mz * (0.5f - q1q1 - q2q2));

			// Estimated direction of gravity and magnetic field
			halfvx = q1q3 - q0q2;
			halfvy = q0q1 + q2q3;
			halfvz = q0q0 - 0.5f + q3q3;
			halfwx = bx * (0.5f - q2q2 - q3q3) + bz * (q1q3 - q0q2);
			halfwy = bx * (q1q2 - q0q3) + bz * (q0q1 + q2q3);
			halfwz = bx * (q0q2 + q1q3) + bz * (0.5f - q1q1 - q2q2);  

			// Error is sum of cross product between estimated direction and measured direction of field vectors
			halfex = (ay * halfvz - az * halfvy) + (my * halfwz - mz * halfwy);
			halfey = (az * halfvx - ax * halfvz) + (mz * halfwx - mx * halfwz);
			halfez = (ax * halfvy - ay * halfvx) + (mx * halfwy - my * halfwx);

			// Compute and apply integral feedback if enabled
			if(params.twoKi > 0.0f) {
				params.integralFBx += params.twoKi * halfex * (1.0f / params.sampleFreq);	// integral error scaled by Ki
				params.integralFBy += params.twoKi * halfey * (1.0f / params.sampleFreq);
				params.integralFBz += params.twoKi * halfez * (1.0f / params.sampleFreq);
				gx += params.integralFBx;	// apply integral feedback
				gy += params.integralFBy;
				gz += params.integralFBz;
			}
			else {
				params.integralFBx = 0.0f;	// prevent integral windup
				params.integralFBy = 0.0f;
				params.integralFBz = 0.0f;
			}

			// Apply proportional feedback
			gx += params.twoKp * halfex;
			gy += params.twoKp * halfey;
			gz += params.twoKp * halfez;
		}

		// Integrate rate of change of quaternion
		gx *= (0.5f * (1.0f / params.sampleFreq));		// pre-multiply common factors
		gy *= (0.5f * (1.0f / params.sampleFreq));
		gz *= (0.5f * (1.0f / params.sampleFreq));
		qa = params.q[0];
		qb = params.q[1];
		qc = params.q[2];
		params.q[0] += (-qb * gx - qc * gy - params.q[3] * gz);
		params.q[1] += (qa * gx + qc * gz - params.q[3] * gy);
		params.q[2] += (qa * gy - qb * gz + params.q[3] * gx);
		params.q[3] += (qa * gz + qb * gy - qc * gx); 

		// Normalise quaternion
		recipNorm = invSqrt(params.q[0] * params.q[0] + params.q[1] * params.q[1] + params.q[2] * params.q[2] + params.q[3] * params.q[3]);
		params.q[0] *= recipNorm;
		params.q[1] *= recipNorm;
		params.q[2] *= recipNorm;
		params.q[3] *= recipNorm;
	}

	private void MahonyAHRSupdateIMU(MahoneyParameters params, float gx, float gy, float gz, float ax, float ay, float az) {
		float recipNorm;
		float halfvx, halfvy, halfvz;
		float halfex, halfey, halfez;
		float qa, qb, qc;

		// Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
		if(!((ax == 0.0f) && (ay == 0.0f) && (az == 0.0f))) {

			// Normalise accelerometer measurement
			recipNorm = invSqrt(ax * ax + ay * ay + az * az);
			ax *= recipNorm;
			ay *= recipNorm;
			az *= recipNorm;        

			/*
			// Normalise gyroscope measurement
			recipNorm = invSqrt(gx * gx + gy * gy + gz * gz);
			gx *= recipNorm;
			gy *= recipNorm;
			gz *= recipNorm;
			 */

			// Estimated direction of gravity and vector perpendicular to magnetic flux
			halfvx = params.q[1] * params.q[3] - params.q[0] * params.q[2];
			halfvy = params.q[0] * params.q[1] + params.q[2] * params.q[3];
			halfvz = params.q[0] * params.q[0] - 0.5f + params.q[3] * params.q[3];

			// Error is sum of cross product between estimated and measured direction of gravity
			halfex = (ay * halfvz - az * halfvy);
			halfey = (az * halfvx - ax * halfvz);
			halfez = (ax * halfvy - ay * halfvx);

			// Compute and apply integral feedback if enabled
			if(params.twoKi > 0.0f) {
				params.integralFBx += params.twoKi * halfex * (1.0f / params.sampleFreq);	// integral error scaled by Ki
				params.integralFBy += params.twoKi * halfey * (1.0f / params.sampleFreq);
				params.integralFBz += params.twoKi * halfez * (1.0f / params.sampleFreq);
				gx += params.integralFBx;	// apply integral feedback
				gy += params.integralFBy;
				gz += params.integralFBz;
			}
			else {
				params.integralFBx = 0.0f;	// prevent integral windup
				params.integralFBy = 0.0f;
				params.integralFBz = 0.0f;
			}

			// Apply proportional feedback
			gx += params.twoKp * halfex;
			gy += params.twoKp * halfey;
			gz += params.twoKp * halfez;
		}

		// Integrate rate of change of quaternion
		gx *= (0.5f * (1.0f / params.sampleFreq));		// pre-multiply common factors
		gy *= (0.5f * (1.0f / params.sampleFreq));
		gz *= (0.5f * (1.0f / params.sampleFreq));
		qa = params.q[0];
		qb = params.q[1];
		qc = params.q[2];
		params.q[0] += (-qb * gx - qc * gy - params.q[3] * gz);
		params.q[1] += (qa * gx + qc * gz - params.q[3] * gy);
		params.q[2] += (qa * gy - qb * gz + params.q[3] * gx);
		params.q[3] += (qa * gz + qb * gy - qc * gx); 

		// Normalise quaternion
		recipNorm = invSqrt(params.q[0] * params.q[0] + params.q[1] * params.q[1] + params.q[2] * params.q[2] + params.q[3] * params.q[3]);
		params.q[0] *= recipNorm;
		params.q[1] *= recipNorm;
		params.q[2] *= recipNorm;
		params.q[3] *= recipNorm;
	}

	private float invSqrt(float x) {
		return (float) (1.0 / Math.sqrt((double) x));
	}
}
