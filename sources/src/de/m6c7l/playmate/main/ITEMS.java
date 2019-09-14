/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

public class ITEMS {

	public static enum BEARING {

		BEARING_000				("000",		  0,		   0),
	    BEARING_090				("090",		 90,		   1),
		BEARING_180				("180",		180,		   2),
		BEARING_270				("270",		270,		   3);
		
		private String plain = null;
		private int value = 0;
		private double factor = 0;
		
		private BEARING(String plain, int value, double factor) {
			this.plain = plain;
			this.value = value;
			this.factor = factor;	
		}
		
		public int value() {
			return value;
		}
		
		public double convert(double value, BEARING to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static BEARING get(String plain) {
			int idx = indexOf(values(),plain);
			if (idx!=-1) return values()[idx];
			return null;
		}
		
	}
	
	public static enum TIME {

		MINUTES_10				("10 min",		 10,		12.0),
	    MINUTES_25				("25 min",		 25,		 4.8),
		MINUTES_60				("60 min",		 60,		 2.0),
		MINUTES_120				("120 min",		120,		 1.0);
		
		private String plain = null;
		private int value = 0;
		private double factor = 0;
		
		private TIME(String plain, int value, double factor) {
			this.plain = plain;
			this.value = value;
			this.factor = factor;	
		}
		
		public int value() {
			return value;
		}
		
		public double convert(double value, TIME to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static TIME get(String plain) {
			int idx = indexOf(values(),plain);
			if (idx!=-1) return values()[idx];
			return null;
		}
		
	}
	
	public static enum MOTION_VECTOR {

		MINUTES_3				("3 min",	 3, 	20.0),
	    MINUTES_6				("6 min",	 6, 	10.0),
		MINUTES_12				("12 min",	12,	 	 5.0),
		MINUTES_30				("30 min",	30,	 	 2.0),
		MINUTES_60				("60 min",	60,	 	 1.0);
		
		private String plain = null;
		private int value = 0;
		private double factor = 0;
		
		private MOTION_VECTOR(String plain, int value, double factor) {
			this.plain = plain;
			this.value = value;
			this.factor = factor;	
		}
		
		public int value() {
			return value;
		}
		
		public double convert(double value, MOTION_VECTOR to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static MOTION_VECTOR get(String plain) {
			int idx = indexOf(MOTION_VECTOR.values(),plain);
			if (idx!=-1) return MOTION_VECTOR.values()[idx];
			return null;
		}	
		
	}
	
	public static enum SPEED {

		METER_PER_SECOND		("m/s",		1.0),
		KNOTS					("kn",		1.94384);

		private String plain = null;
		private double factor = 0;
		
		private SPEED(String plain, double factor) {
			this.plain = plain;
			this.factor = factor;
		}
		
		public double factor() {
			return factor;
		}
		
		public double convert(double value, SPEED to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static SPEED get(String plain) {
			int idx = indexOf(values(),plain);
			if (idx!=-1) return values()[idx];
			return null;
		}		
		
	}
	
	public static enum LENGTH {
 
		METER					("m",		1.0),
		YARD					("yd",		1.0936);

		private String plain = null;
		private double factor = 0;
		
		private LENGTH(String plain, double factor) {
			this.plain = plain;
			this.factor = factor;
		}
		
		public double factor() {
			return factor;
		}
		
		public double convert(double value, LENGTH to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static LENGTH get(String plain) {
			int idx = indexOf(values(),plain);
			if (idx!=-1) return values()[idx];
			return null;
		}		
		
	}

	public static enum HEIGHT {
		 
		METER					("m",		1.0),
		FEET					("ft",		3.2808);

		private String plain = null;
		private double factor = 0;
		
		private HEIGHT(String plain, double factor) {
			this.plain = plain;
			this.factor = factor;
		}
		
		public double factor() {
			return factor;
		}
		
		public double convert(double value, LENGTH to) {
			return (value/factor)*to.factor;
		}
		
		public static String typical() {
			return ITEMS.typical(values());
		}
		
		public String toString() {
			return this.plain;
		}
		
		public static HEIGHT get(String plain) {
			int idx = indexOf(values(),plain);
			if (idx!=-1) return values()[idx];
			return null;
		}		
		
	}

	private ITEMS() {}
	
	private static String typical(Object[] values) {
		String s = values[0].toString();
		for (int i=1; i<values.length; i++) {
			if (s.length()<values[i].toString().length()) {
				s = values[i].toString();				
			}
		}
		return s;
	}

	private static int indexOf(Object[] values, String plain) {
		for (int i=0; i<values.length; i++) {
			if (values[i].toString().equals(plain)) {	
				return i;				
			}
		}
		return -1;
	}

}
