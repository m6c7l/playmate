/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public interface Canvasable {

	public Position getPosition(Point point);
	public Point getPoint(Position position);
	
	public Graphics2D getPaintbox();
	public boolean isMagnified();
	
}
