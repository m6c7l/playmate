/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Point;

public interface Drawable {
	
	public Point draw(Canvasable canvasable);
	public boolean isDrawable();
	public int getDrawSize();
	
}
