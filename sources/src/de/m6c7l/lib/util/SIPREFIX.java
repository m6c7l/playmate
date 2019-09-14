/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util;

public enum SIPREFIX {
	
	YOTTA	( 24,	"Y"),
	ZETTA	( 21,	"Z"),
	EXA		( 18,	"E"),
	PETA	( 15,	"P"),
	TERA	( 12,	"T"),
	GIGA	(  9,	"G"),
	MEGA	(  6,	"M"),
	KILO	(  3,	"k"),
	NONE	(  0,	"" ),
	MILLI	( -3,	"m"),
	MICRO	( -6,	"u"),
	NANO	( -9,	"n"),
	PICO	(-12,	"p"),
	FEMTO	(-15,	"f"),
	ATTO	(-18,	"a"),
	ZEPTO	(-21,	"z"),
	YOCTO	(-24,	"y");
	
	private int exponent = 0;
	private String plain = null;
	
	private SIPREFIX(int exponent, String plain) {
		this.exponent = exponent;
		this.plain = plain;
	}
	
	public SIPREFIX next() {
		SIPREFIX[] prefixes = values();
		for (int i=0; i<values().length; i++) {
			if (prefixes[i].exponent-3==exponent) {
				return prefixes[i];
			}
		}
		return null;
	}
	
	public SIPREFIX previous() {
		SIPREFIX[] prefixes = values();
		for (int i=0; i<values().length; i++) {
			if (prefixes[i].exponent+3==exponent) {
				return prefixes[i];
			}
		}
		return null;
	}

	public double convert(SIPREFIX prefix, double value) {
		return (value/Math.pow(10,exponent))*Math.pow(10,prefix.exponent);
	}
	
	public String toString() {
		return plain;
	}
	
	public static String format(double value, String suffix) {
		return format(SIPREFIX.NONE,value,suffix);
	}
	
	public static String format(SIPREFIX prefix, double value, String suffix) {
		SIPREFIX p = SIPREFIX.TERA;
		while ((p.previous()!=null) && (p.convert(prefix,value)<10)) {
			p = p.previous();
		}
		return ((int)p.convert(prefix,value)) + " " + p.toString() + suffix; 
	}
	
}
