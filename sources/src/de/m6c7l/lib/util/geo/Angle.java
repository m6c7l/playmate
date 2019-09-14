/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.geo;

import java.text.DecimalFormat;

public class Angle {

	public static final ORIENTATION NORTH	= ORIENTATION.NORTH;
	public static final ORIENTATION EAST	= ORIENTATION.EAST;
	public static final ORIENTATION SOUTH	= ORIENTATION.SOUTH;
	public static final ORIENTATION WEST	= ORIENTATION.WEST;
	
	private double angle = 0;
	private ORIENTATION orientation = null;
	
	public static String toString(double angle) {
		
		double a = angle;
		if (a > 180) a = a - 360; else if (a < -180) a = a + 360;
		
		int g = (int)a;
		int s = (int)Math.rint(((a - g) * 3600));		
		int m = (s / 60);
		if (Math.abs(m)==60) {
			g = g + (m/Math.abs(m));
			m = m % 60;
		}
		s = s % 60;
		
		return (s != 0 ? s + "''" : (m != 0 ? m + "'" : g + "°"));
		
	}
	
	public Angle(ORIENTATION orientation, double angle) {
		this.angle = Math.abs(angle) % 360;
		this.orientation = orientation;
		if (this.orientation.type==ORIENTATION.TYPE.LONGITUDE) {
			if (this.angle>180) {
				this.angle = 180-(this.angle-180);
				this.orientation = this.orientation.getOpposite();
			}			
		} else if (this.orientation.type==ORIENTATION.TYPE.LATITUDE) {
			if (this.angle>180) {
				this.angle = this.angle-180;
				this.orientation = this.orientation.getOpposite();
			} else if (this.angle>90) {
				this.angle = 90-(this.angle-90);
			}
		}
	}

	public Angle(ORIENTATION orientation, int degrees, double minutes) {
		this(orientation,degrees+(minutes/60.0));
	}

	public Angle(ORIENTATION orientation, int degrees, int minutes, double seconds) {
		this(orientation,degrees,minutes+(seconds/60.0));
	}

	public double getValue() {
		return this.orientation.value*this.angle;
	}
	
	public ORIENTATION getOrientation() {
		return this.orientation;
	}
	
	public String toString() {
		DecimalFormat dfDeg = (this.orientation.type==ORIENTATION.TYPE.LATITUDE ? new DecimalFormat("00") : new DecimalFormat("000"));
		DecimalFormat dfMin = new DecimalFormat("00.000");
		double value = this.angle;
		int deg = (int)value;
		double min = (value-(int)value)*60;
		if ((int)(Math.round(min*1000))==60000) {
			deg++;
			min = 0;
		}
		return dfDeg.format(deg) + "-" + dfMin.format(min) + " " + this.orientation.token;
	}

	private enum ORIENTATION {

		NORTH	(+1,"N",TYPE.LATITUDE),
		SOUTH	(-1,"S",TYPE.LATITUDE),
		EAST	(+1,"E",TYPE.LONGITUDE),
		WEST	(-1,"W",TYPE.LONGITUDE);
		
		int value = 0;
		String token = null;
		TYPE type = null;
		
		ORIENTATION(int value, String token, TYPE type) {
			this.value = value;
			this.token = token;
			this.type = type;
		}
		
		public ORIENTATION getOpposite() {
			if (this.type == TYPE.LATITUDE) {
				if (this==NORTH) return SOUTH; else return NORTH;
			} else {
				if (this==EAST) return WEST; else return EAST;
			}
		}
		
		private enum TYPE {
			LATITUDE(),
			LONGITUDE()
		}

	}
	
}