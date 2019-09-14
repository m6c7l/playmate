/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.Vector;

public class MATH {
	
	private MATH() {}

	public static double getGaussian(double mean, double stddev) {
		return new Random().nextGaussian()*stddev+mean;
	}
	
	/*
	 * Vergleichen
	 */
	
	public static int compare(double value1, double value2, int precision) {
		int result = 0;
		int a = (int)value1;
		int b = (int)value2;
		if (a==b) {
			a = (int)((value1-a)*Math.pow(10,precision));
			b = (int)((value2-b)*Math.pow(10,precision));	
		}
		if (a<b) {
			result = -1;
		} else if (a>b) {
			result = +1;
		}
		return result;
	}
	
	/*
	 * Konvertierung
	 */

	public static Double[] convertArray(Object anArray) {
		Vector<Double> bdArray = new Vector<Double>();
		for (int i=0; i<Array.getLength(anArray); i++)
			bdArray.add(convert(Array.get(anArray,i)));
		return (Double[])bdArray.toArray(new Double[bdArray.size()]);
	}
	
	public static Double convert(Object anObject) {
		Double bd = null;
		if (anObject!=null) {
			try {
				bd = new Double(anObject.toString());
			} catch (NumberFormatException e) {
			}
		}
		return bd;
	}
	
}
