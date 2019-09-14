/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.text.DecimalFormat;

import de.m6c7l.lib.util.geo.Position;

public class VALUE {
	
	public static double AF = 1.0;
	public static String AU = "m";
	
	public static double LF = 1.0;
	public static String LU = "m";

	public static double SF = 1.0;
	public static String SU = "m/s";

	/*
	 * angles
	 */
	
	public static String[] HEADING(Double degrees) {
		if (degrees==null) return new String[] {"",null};
		return new String[] {ANGLE(degrees,"000"),null};
	}

	public static String[] TRUEBEARING(Double degrees) {
		if (degrees==null) return new String[] {"",null};
		return new String[] {ANGLE(degrees,"000.0"),null};
	}
	
	public static String[] BEARING(Double degrees) {
		if (degrees==null) return new String[] {"","\u00b0"};
		return new String[] {ANGLE(degrees,"0.0"),"\u00b0"};
	}
	
	public static String[] ANGLEBOW(Double degrees) {
		if (degrees==null) return new String[] {"","\u00b0"};
		String temp = ANGLE(Math.abs(degrees),"0.0");
		if (degrees!=180) {
			if (degrees<0) {
				temp = "L " + temp; 
			} else if (degrees>0) {
				temp = "R " + temp; 			
			}			
		}
		return new String[] {temp,"\u00b0"};
	}
	
	private static String ANGLE(Double degrees, String format) {
		if ((degrees==null) || (degrees.isNaN())) return new String("");
		double deg = (degrees+360)%360;
		DecimalFormat dfA = new DecimalFormat(format);
		return new String(dfA.format(deg));	
	}
	
	/*
	 * speed
	 */
	
	public static String[] SPEED(Double speed) {
		String format = "0.0";
		if ((speed==null) || (speed.isNaN())) return new String[]{"",SU};
		if (speed*SF>100) format = "0"; 
		DecimalFormat dfA = new DecimalFormat(format);
		return new String[] {dfA.format(speed*SF),SU};		
	}

	/*
	 * bearing rate
	 */
	
	public static String[] BEARINGRATE(Double rate) {
		String format = "0.0";
		if ((rate==null) || (rate.isNaN())) return new String[]{"","\u00b0/min"};
		DecimalFormat dfA = new DecimalFormat(format);
		return new String[] {dfA.format(rate),"\u00b0/min"};		
	}
	
	/*
	 * position
	 */
	
	public static String[] POSITION(Position position) {
		if (position==null) return new String[]{"",null,"",null};
		String[] lat = position.toStringLatitude().split(" ");	
		String[] lon = position.toStringLongitude().split(" ");
		return new String[] {lat[0],lat[1],lon[0],lon[1]};
	}

	/*
	 * common
	 */
	
	public static String DOUBLE(Double value) {
		String format = "0";
		if ((value==null) || (value.isNaN())) return new String("");
		if (value<10) format = format + ".0";
		if (value<1) format = format + "0";		
		DecimalFormat dfA = new DecimalFormat(format);
		return new String(dfA.format(value));		
	}

	public static String INTEGER(Double value) {
		String format = "0";
		if ((value==null) || (value.isNaN())) return new String("");
		DecimalFormat dfA = new DecimalFormat(format);
		return new String(dfA.format(value));		
	}

	/*
	 * time
	 */
	
	public static String[] SECONDS(Integer seconds) {
		return SECONDS(seconds!=null ? seconds.longValue() : null);
	}
	
	public static String[] SECONDS(Long seconds) {
		String format = "0";
		if (seconds==null) return new String[] {"",null};
		double v = seconds;
		String[] a = new String[] {"s","min","h"};
		int[] ax = new int[] {60,60,1};		
		int i = 0;
		while ((i<ax.length-1) && (v>=ax[i])) {
			v = v/(ax[i]*1.0);
			i++;
		}
		DecimalFormat dfA = new DecimalFormat(format + (i>1 ? ".0" : ""));
		return new String[] {dfA.format(v),a[i]};		
	}

	
	public static String TIME(int secs) {
		return TIME(new Integer(secs).longValue());
	}
	
	public static String TIME(Long secs) {
		if (secs==null) return "";
		long sec = Math.abs(secs);
		if (secs<0) sec = (60*60*24)-sec;
		long hh = (sec/3600)%96; // eigentlich 24
		String shh = (hh<10 ? "0" : "") + hh;
		long mm = (sec%3600)/60;
		String smm = (mm<10 ? "0" : "") + mm;
		long ss = (sec%3600)%60;
		String sss = (ss<10 ? "0" : "") + ss;
		return shh + ":" + smm + ":" + sss;
	}

	/*
	 * length
	 */

	public static String[] RANGE(Double value) {
		String format = "0";
		if ((value==null) || (value.isNaN())) return new String[] {"",LU};
		double v = value*LF;
		String[] a = new String[] {"","k"};
		int i = 0;
		String f = "";
		while ((i<a.length-1) && (v>=10000)) {
			v = v/1000.0;
			i++;
			f = f + "0";
		}
		if (f.length()>0) {
			f = "." + f;
		}
		DecimalFormat dfA = new DecimalFormat(format + f);
		return new String[] {dfA.format(v),a[i] + LU};		
	}
	
	public static String[] NAUTICAL_RANGE(Double value) {
		return new String[] {"",""};		
	}
	
	public static String[] DRAFT(Double value) {
		String format = "0.0";
		if ((value==null) || (value.isNaN())) return new String[] {"","m"};
		DecimalFormat dfA = new DecimalFormat(format);
		return new String[] {dfA.format(value),"m"};		
	}
	
	public static String[] ALTITUDE(Double value) {
		String format = "0";
		if ((value==null) || (value.isNaN())) return new String[] {"",AU};
		DecimalFormat dfA = new DecimalFormat(format);
		return new String[] {dfA.format(value),AU};		
	}
	
}
