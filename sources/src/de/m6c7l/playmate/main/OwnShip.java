/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import de.m6c7l.lib.util.geo.Position;

public class OwnShip extends Submarine {

	public OwnShip(World world, Position position) {
		super(world,null,position,6.0,8.5,100,6.5,true,56,7);
	}
	
}
