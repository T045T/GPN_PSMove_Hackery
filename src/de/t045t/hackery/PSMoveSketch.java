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
	private int segments = 30;
	private int numberOfRings;
	private boolean endScreen;
	private boolean gameOver;
	private PFont helv;

	private int score;

	private int gameTimeSec = 90;
	private long startTime;

	int lastSeenButton;


	public void setup() {
		// Initialize MAGIC WANDS

		startTime = System.currentTimeMillis();
		helv = createFont("Helvetica", 16);
		endScreen = false;
		numberOfRings = 3;
		lastSeenButton = -1;
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
			dParamList.add(new DumbFusionParameters(moves.get(i), 3));
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
		//rings.add(new RingInfo(90, testButtonPairs, (float) Math.PI / 60));
		rings.add(new RingInfo(((this.height / 2) - 100)/numberOfRings, (float) (Math.random() * 2f * Math.PI)));
		rings.add(new RingInfo(((this.height / 2) - 100)/numberOfRings, (float) (Math.random() * 2f * Math.PI)));
		rings.add(new RingInfo(((this.height / 2) - 100)/numberOfRings, (float) (Math.random() * 2f * Math.PI)));
		//rings.add(new RingInfo(90, testButtonPairs, (float) Math.PI / 150));
		activeRing = rings.size()-1;
	}

	int activeRing;
	boolean btnPressed;
	float startAngle;

	public void draw() {
		if (gameOver) {
			drawGameOver();
		} else {
			if (!endScreen) {
				drawGame();
			} else {
				drawEndScreen();
			}
		}
	}

	private void drawGameOver() {
		background(125,125,125);
		PFont font = createFont("Helvetica", 50);
		textFont(font);
		text("Game Over!\nPress X Button to restart game!", 20, 50);
		text("Final Score: " + score, 20, this.height/2);

		if (moves.size() > 0 && moves.get(0).poll() > 0) {
			if ((moves.get(0).get_buttons() & Button.Btn_CROSS.swigValue() )> 0) {
				endScreen = false;
				restartNew();
			}
		}
	}
	private void drawEndScreen() {
		background(125,125,125);
		PFont font = createFont("Helvetica", 50);
		textFont(font);
		text("Congratulations!\nPress X button to continue!", 50, 50);
		if (moves.size() > 0 && moves.get(0).poll() > 0) {
			if ((moves.get(0).get_buttons() & Button.Btn_CROSS.swigValue() )> 0) {
				endScreen = false;
				restartHarder();
			}
		}
	}

	private void drawGame() {
		DumbFusionParameters tmpPar = null;
		background(20,20,20);
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
						rings.get(activeRing).setAngle(rings.get(activeRing).getAngle() - (tmpPar.alpha - startAngle));
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

		int lastWidth = 50;
		drawCenterIcon(lastWidth);
		for (int i = 0; i < rings.size(); i++) {
			RingInfo r = rings.get(i);
			int increment = 0;
			if (i == activeRing) {
				if (r.hasQTE()) {
					drawCenteredQTERing(lastWidth, r.getWidth(), r.getAngle(), r.getButtonPos(), 125, 125, 0);
					increment = r.getWidth();
					pushMatrix();
					translate(this.width/2, this.height/2);
					fill(255f,0f,0f,0.6f);
					rect(-10, -1*(lastWidth + r.getWidth() +2), 20, r.getWidth() +4);
					popMatrix();
					r.setAngle(r.getAngle() + r.getRotSpeed());

					// hit detection
					float angleDiff;
					if (moves.get(0).poll() > 0) {
						int button = moves.get(0).get_buttons();
						int j = 0;
						double correctedAngle = r.getAngle() - Math.PI;
						if (correctedAngle < 0) correctedAngle += 2*Math.PI;

						if (r.getLastSeen() != -1 
								&& r.getButtonPos().get(r.getLastSeen()).getAngle() < correctedAngle + (r.getRotSpeed() * this.frameRate / 5) 
								&& r.getButtonPos().get(r.getLastSeen()).getState() == 0) {
							r.getButtonPos().get(r.getLastSeen()).missed();
						}

						for (int k = 0; k < r.getButtonPos().size(); k++) {
							ButtonLocation l = r.getButtonPos().get(k);
							if (Math.abs(l.getAngle() - correctedAngle) < (r.getRotSpeed() * this.frameRate / 5)) {
								r.setLastSeen(k);
							}
						}
						//						while (j < r.getButtonPos().size() && correctedAngle > (r.getButtonPos().get(j).getAngle() + (r.getRotSpeed() * this.frameRate / 10))) {
						//							if (r.getButtonPos().get(j).getState() == 0) {
						//								if ((j > 0 && r.getButtonPos().get(j-1).getState() != 0) || j == 0)
						//								r.getButtonPos().get(j).missed();
						//							}
						//							j++;
						//						}

						for (ButtonLocation l : r.getButtonPos()) {
							if (l.getState() == 0) {
								angleDiff = Math.abs(l.getAngle() - (float) correctedAngle);
								if (angleDiff < r.getRotSpeed() * this.frameRate / 5) {
									switch(l.getType()) {
									case 0: //CROSS
										if ((button & Button.Btn_CROSS.swigValue()) > 0) {
											l.hit();
										}
										break;
									case 1: //CIRCLE
										if ((button & Button.Btn_CIRCLE.swigValue()) > 0) {
											l.hit();
										}
										break;
									case 2: //SQUARE
										if ((button & Button.Btn_SQUARE.swigValue()) > 0) {
											l.hit();
										}
										break;
									case 3: //TRIANGLE
										if ((button & Button.Btn_TRIANGLE.swigValue()) > 0) {
											l.hit();
										}
										break;
									}
									if (l.getState() == 0 && button > 0) {
										l.missed();
									}
								}
							}
						}
					}
				} else {
					int tmpRumble;
					float angleDiff;
					if (btnPressed && tmpPar != null) {
						drawCenteredRing(lastWidth, r.getWidth(), r.getAngle() - (tmpPar.alpha - startAngle), 255, 0, 0);
						increment = r.getWidth();
						float currentActiveAngle = r.getAngle() - (tmpPar.alpha - startAngle);
						currentActiveAngle %= (Math.PI * 2);

						if ( currentActiveAngle < r.getSweetSpot()) {
							angleDiff = (float) ((r.getSweetSpot() - currentActiveAngle) % (Math.PI * 2));

						} else {
							angleDiff = (float) ((currentActiveAngle - r.getSweetSpot()) % (Math.PI * 2));
						}
						if (angleDiff > Math.PI) angleDiff = (float) ((Math.PI * 2) - angleDiff);
						tmpRumble = (int) (Math.abs(Math.round(255 * (1- angleDiff / (Math.PI)))));
						//System.out.println(angleDiff + "\t" + currentActiveAngle + "\t" + r.getSweetSpot() + "\t" + tmpRumble);
					}else {
						drawCenteredRing(lastWidth, r.getWidth(), r.getAngle(), 255,0, 0);
						increment = r.getWidth();
						if (r.getAngle()% (Math.PI * 2) < r.getSweetSpot()) {
							angleDiff = (float) ((r.getSweetSpot() - r.getAngle()) % (Math.PI * 2));
						} else {
							angleDiff = (float) ((r.getAngle() - r.getSweetSpot()) % (Math.PI * 2));
						}
						if (angleDiff > Math.PI) angleDiff = (float) ((Math.PI * 2) - angleDiff);
						tmpRumble = (int) (Math.abs(Math.round(255 * (1- angleDiff / (Math.PI)))));
					}
					//System.out.println(tmpPar != null ? ((Math.abs(tmpPar.accel[tmpPar.samples][0]) + Math.abs(tmpPar.accel[tmpPar.samples][1])) + "\t" + Math.abs(tmpPar.accel[tmpPar.samples][2])): "");
					//System.out.println(tmpRumble);
					if (tmpPar != null
							&& Math.abs(tmpPar.accel[tmpPar.samples][0]) + Math.abs(tmpPar.accel[tmpPar.samples][1]) > 2
							&& Math.abs(tmpPar.accel[tmpPar.samples][2]) > 1.4
							&& tmpRumble > 220) {
						r.hit();
						score += 100 * numberOfRings;
						activeRing--;
						moves.get(0).set_rumble(0);
					} else {
						moves.get(0).set_rumble(calcVibration(tmpRumble));
					}
					moves.get(0).update_leds();
				}
			} else {
				if (r.hasQTE()) {
					if (r.isHit()) {
						drawCenteredQTERing(lastWidth, 2*r.getWidth()/3, r.getAngle(), r.getButtonPos(), 0, 0, 255);
						increment = 2*r.getWidth()/3;
					} else {
						drawCenteredQTERing(lastWidth, r.getWidth(), r.getAngle(), r.getButtonPos(), 125, 125, 0);
						increment = r.getWidth();
					}

				} else {
					if (r.isHit()) {
						drawCenteredRing(lastWidth, 2*r.getWidth() /3, r.getAngle(), 0, 0, 255);
						increment = 2*r.getWidth()/3;
					} else {
						drawCenteredRing(lastWidth, r.getWidth(), r.getAngle(), 255,0, 0);
						increment = r.getWidth();
					}
				}
			}
			if (activeRing < 0) activeRing = 0;
			lastWidth += increment+5;
			boolean win = true;
			for(RingInfo t : rings) {
				win &= t.allHit();
			}
			if (win) {
				endScreen = true;
			}
		}
		textFont(helv);
		text(score, 15, 30);

		int passedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
		float foo = passedTime / (float) gameTimeSec;
		if (foo < 1) {
			if (moves.size() > 0) {
				moves.get(0).set_leds((int)(255 * foo), (int) (255 * (1-foo)), 0);
			}
		} else {
			endScreen = false;
			gameOver = true;
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

	private void restartHarder() {
		if (numberOfRings < 8) {
			numberOfRings++;
			rings = new ArrayList<RingInfo>();
			for (int i = 0; i < numberOfRings; i++) {
				rings.add(new RingInfo(((this.height / 2) - 100)/numberOfRings, (float) (Math.random() * 2f * Math.PI)));
			}
			activeRing = numberOfRings -1;
		}
	}

	private void restartNew() {
		numberOfRings = 3;
		rings = new ArrayList<RingInfo>();
		for (int i = 0; i < numberOfRings; i++) {
			rings.add(new RingInfo(((this.height / 2) - 100)/numberOfRings, (float) (Math.random() * 2f * Math.PI)));
		}
		activeRing = numberOfRings -1;
		score = 0;
		startTime = System.currentTimeMillis();
		gameOver = false;
		endScreen = false;
	}

	private int calcVibration(int input) {
		double tmp = (double) input;
		return (int) Math.round(255-Math.sqrt(1-(tmp/255)*(tmp/255))*255);
	}

	private void updateRadialDial(int x, int y) {

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
		drawSegment(this.width / 2, this.height / 2, innerRadius + width, segments, innerRadius, rotation, (float) (rotation+ (Math.PI * 2)));
	}

	private void drawCenteredQTERing(int innerRadius, int width, float rotation, ArrayList<ButtonLocation> buttonPositions, int r, int g, int b) {
		drawCenteredRing(innerRadius, width, rotation, r, g, b);
		for (ButtonLocation e : buttonPositions) {
			pushMatrix();
			translate(this.width / 2, this.height / 2);
			float foo = rotation - e.getAngle();
			rotate(rotation - e.getAngle());
			translate(0, innerRadius + (width / 2));
			ellipseMode(CENTER);
			if (e.getState() == 1) {
				stroke(0,255,0);
			} else if (e.getState() == 2) {
				stroke(255,0,0);
			} else {
				stroke(r,g,b);
			}
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

	private void drawCenterIcon(int radius) {
		/*
		PSMove move = null;
		if (moves.size() > 0) move = moves.get(0);
		if (moves == null) return;
		if (move.poll() > 0) {
			int buttons = move.get_buttons(); 


			pushMatrix();
			translate(this.width/2, this.height/2);

			int width = radius *2;
			float circleWidth = width - width /5f;
			float circleCoord = sin((float) Math.PI / 4f) * circleWidth/2f;
			circleCoord -= circleCoord / 5f;
			ellipse(0, 0, circleWidth, circleWidth);

			if ((buttons & Button.Btn_CIRCLE.swigValue()) > 0) {
				ellipse(0, 0, circleWidth - circleWidth / 2.5f, circleWidth - circleWidth / 2.5f);

			} else if ((buttons & Button.Btn_TRIANGLE.swigValue()) > 0) {
				float tmpX = cos((float) Math.PI / 6f) * circleWidth/-2f;
				tmpX -= tmpX / 5f;
				float tmpY = sin((float) Math.PI / 6f) * circleWidth/-2f;
				tmpY -= tmpY / 5f;
				line(0, (circleWidth/2f) - (circleWidth/10f), tmpX, tmpY);
				line(tmpX, tmpY, -1*tmpX, tmpY);
				line(-1*tmpX, tmpY, 0, (circleWidth/2f) - (circleWidth/10f));

			} else if ((buttons & Button.Btn_CROSS.swigValue()) > 0) {
				line(-1 * circleCoord, -1*circleCoord, circleCoord, circleCoord);
				line(-1 * circleCoord, circleCoord, circleCoord, -1*circleCoord);

			} else if ((buttons & Button.Btn_SQUARE.swigValue()) > 0) {
				rect(-1 * circleCoord, -1 * circleCoord, 2*circleCoord, 2*circleCoord);

			} 
			popMatrix();
		}
		 */
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
