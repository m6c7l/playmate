/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public interface Locateable {

	public boolean setPosition(Position position);
	public Position getPosition();
	public Point getPoint(Canvasable canvas);
	
}
