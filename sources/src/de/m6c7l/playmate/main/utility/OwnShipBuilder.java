/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.utility;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.dnd.Createable;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.OwnShip;
import de.m6c7l.playmate.main.World;

public class OwnShipBuilder implements Createable {

	public Asset create(World world, Position position) throws Exception {
		return new OwnShip(world,position);
	}
	
	public String toString() {
		return "Own ship";
	}
	
}
