/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import de.m6c7l.lib.util.geo.Position;

public interface Surface {

	public boolean isLand(Position pos);
	public boolean isWater(Position pos);
	public boolean isCoast(Position pos);
	public boolean isCovert(Position pos1, Position pos2);	
	
}
