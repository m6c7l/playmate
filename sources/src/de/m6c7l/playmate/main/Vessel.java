/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public class Vessel extends Watercraft {

	public Vessel(
			World world,
			Object id,
			Position position,
			double maxSpeed,
			double draft,
			boolean military,
			boolean sonar,
			double length,
			double beam) {
		
		super(	world,
				id,
				position,
				maxSpeed,
				draft,
				draft, 
				military,
				sonar,
				length,
				beam);
	}

	public double getHeight() {
		return Math.sqrt(super.getLength())+getDraft();
	}
	
	protected double getSpeedPitchPositive() {
		return 0.0;
	}
	
	protected double getSpeedPitchNegative() {
		return 0.0;
	}
	
	public int getRPMBySpeed(double speed) {
		int rpm = 0;
		if (speed!=0) {
			rpm = (int)(10.0*speed);
		}
		return rpm;
	}
	
	public double getSpeedByRPM(double rpm) {
		double s = 0;
		if (rpm!=0) {
			s = 1.0/10.0*rpm;
			if (s>super.getSpeedMaximum()) return super.getSpeedMaximum();
			if (s<super.getSpeedMinimum()) return super.getSpeedMinimum();
			return s;
		} else {
			return 0;
		}
	}
	
	public Point draw(Canvasable canvas) {
		
		if (!isDrawable()) return null;
		
		Point point = super.draw(canvas);
		
		return point;
		
	}

}
