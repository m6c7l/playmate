/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.awt.Color;

import java.awt.Point;

import java.io.File;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.xml.XMLElement;

public class XMLChart {
	
	private static final String NAME 			= "name";	
	private static final String FILE 			= "file";
	
	private static final String REFERENCES 		= "references";
	
	private static final String COLOR 			= "color";
	private static final String COLOR_LAND 		= "land";
	private static final String COLOR_WATER 	= "water";
	
	private static final String MAGNIFIER 		= "magnifier";
	private static final String MAGNIFIER_MIN 	= "min";
	private static final String MAGNIFIER_MAX 	= "max";
	
	private File imageDir = null;
	private XMLElement data = null;

	private static int nextSerial = 0;
	private int serial = 0;
	
	protected XMLChart(XMLElement data, File imageDirectory) {
		this.data = data;
		this.imageDir = imageDirectory;
		this.serial = nextSerial++;
	}
	
	public File getImageDirectory() {
		return this.imageDir;
	}
	
	public String getFile() {
		return CONVERT.getString(data,FILE,false);
	}
	
	public String getName() {
		return CONVERT.getString(data,NAME,false);
	}
	
	public Color getColorLand() {
		if (data.hasElement(COLOR)) {
			return CONVERT.getRGBColor(data.firstElement(COLOR).firstElement(COLOR_LAND));
		}
		return null;
	}
	
	public Color getColorWater() {
		if (data.hasElement(COLOR)) {
			return CONVERT.getRGBColor(data.firstElement(COLOR).firstElement(COLOR_WATER));
		}
		return null;
	}
	
	public Double getScaleUp() {
		if (data.hasElement(MAGNIFIER)) {
			return CONVERT.getDouble(data.firstElement(MAGNIFIER),MAGNIFIER_MAX,false);
		}
		return null;
	}
	
	public Double getScaleDown() {
		if (data.hasElement(MAGNIFIER)) {
			return CONVERT.getDouble(data.firstElement(MAGNIFIER),MAGNIFIER_MIN,false);
		}
		return null;
	}
	
	public Point getReferencePoint(int index) {
		if (data.hasElement(REFERENCES)) {
			return CONVERT.getPoint(data.firstElement(REFERENCES).getElement(index));
		}
		return null;
	}
	
	public Position getReferencePosition(int index) {
		if (data.hasElement(REFERENCES)) {
			return CONVERT.getPosition(data.firstElement(REFERENCES).getElement(index));
		}
		return null;
	}
	
	public int getReferenceCount() {
		if (data.hasElement(REFERENCES)) {
			return data.firstElement(REFERENCES).getElementCount();
		}
		return 0;
	}
	
	public boolean equals(Object o) {
		if ((o!=null) && (o instanceof XMLChart)) {
			XMLChart temp = ((XMLChart)o);
			return temp.serial==this.serial;
		}
		return false;
	}
	
	public int hashCode() {
	    int hc = 11;
	    int hashMultiplier = 31;
	    hc = hc * hashMultiplier + this.serial;
	    return hc; 
	}
	
	public String toString() {
		return getName();
	}

}