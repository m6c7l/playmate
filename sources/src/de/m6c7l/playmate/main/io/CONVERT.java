/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.awt.Color;
import java.awt.Point;
import java.util.Locale;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.xml.XMLElement;

public class CONVERT {

	/*
	 * simple
	 */
	
	public static Double getDouble(XMLElement element, String name, boolean isAttribute) {
		if (!isAttribute) {
			if (element.hasElement(name))
				return element.firstElement(name).getDouble();
		} else {
			if (element.hasAttribute(name))
				return element.getAttribute(name).getDouble();
		}
		return null;
	}
	
	public static String getString(XMLElement element, String name, boolean isAttribute) {
		if (!isAttribute) {
			if (element.hasElement(name))
				return element.firstElement(name).getText();		
		} else {
			if (element.hasAttribute(name))
				return element.getAttribute(name).getValue();
		}
		return null;
	}
	
	public static Boolean getBoolean(XMLElement element, String name, boolean isAttribute) {
		if (!isAttribute) {
			if (element.hasElement(name))
				return element.firstElement(name).getBoolean();		
		} else {
			if (element.hasAttribute(name))
				return new Boolean(element.getAttribute(name).getValue());
		}
		return null;
	}
	
	/*
	 * complex
	 */
	
	public static Locale getLocale(XMLElement element) {
		return new Locale("",element.getText());
	}
	
	public static Color getRGBColor(XMLElement element) {
		if (element==null) return null;
		return new Color(
				element.getAttribute("r").getInteger(),
				element.getAttribute("g").getInteger(),
				element.getAttribute("b").getInteger());
	}
	
	public static Point getPoint(XMLElement element) {
		if ((element==null) || (!element.hasAttribute("x")) || (!element.hasAttribute("y"))) return null;
		return new Point(
				element.getAttribute("x").getInteger(),
				element.getAttribute("y").getInteger());
	}
	
	public static Position getPosition(XMLElement element) {
		if (element==null) return null;
		return new Position(
				element.firstElement("lat").getDouble(),
				element.firstElement("lon").getDouble());
	}
	
}
