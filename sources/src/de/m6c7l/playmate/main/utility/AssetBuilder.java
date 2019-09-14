/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.utility;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.dnd.Createable;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.Helicopter;
import de.m6c7l.playmate.main.Plane;
import de.m6c7l.playmate.main.Submarine;
import de.m6c7l.playmate.main.Vessel;
import de.m6c7l.playmate.main.World;
import de.m6c7l.playmate.main.io.XMLAsset;

public class AssetBuilder implements Createable {
	
	private XMLAsset asset = null;
	
	public AssetBuilder(XMLAsset asset) {
		this.asset = asset;
	}
	
	public String toString() {
		return this.asset.toString();
	}
	
	public Asset create(World world, Position pos) throws Exception {
		
		Asset u = null;
			
		if (isUseable()) {
			
			if (asset.isVessel()) {
				
				u = new Vessel(
						world,
						asset.getID(),
						pos,
						asset.getMaxSpeed(),
						asset.getDraft(),
						asset.isMilitary(),
						asset.hasSonar(),
						asset.getLOA(),
						asset.getBOA());
				
			} else if (asset.isSubmarine()) {
				u = new Submarine(
						world,
						asset.getID(),
						pos,
						asset.getMaxSpeedSurfaced(),
						asset.getMaxSpeedDived(),
						asset.getMOD(),
						asset.getDraft(),
						asset.isMilitary(),
						asset.getLOA(),
						asset.getBOA());
				
			} else if (asset.isPlane()) {
				u = new Plane(
						world,
						asset.getID(),
						pos,
						asset.getCruiseSpeed(),
						asset.getMaxSpeed(),
						asset.getCeiling(),
						asset.isMilitary(),
						asset.getLength(),
						asset.getWingspan());
				
			} else if (asset.isHelicopter()) {
				u = new Helicopter(
						world,
						asset.getID(),
						pos,
						asset.getMaxSpeed(),
						asset.getCeiling(),
						asset.isMilitary(),
						asset.getLength(),
						asset.getRotorDiameter());
				
			}
			
			return u;
			
		}
		
		throw new Exception("xml is invalid: " + toString());

	}
	
	private boolean isUseable() {

		if (asset.isVessel()) {
			return (asset.getMaxSpeed()!=null) && (asset.getDraft()!=null) &&
					(asset.getLOA()!=null) && (asset.getBOA()!=null);

		} else if (asset.isSubmarine()) {
			return (asset.getMaxSpeedSurfaced()!=null) && (asset.getMaxSpeedDived()!=null) && (asset.getMOD()!=null) &&
					(asset.getDraft()!=null) && (asset.getLOA()!=null) &&
					(asset.getBOA()!=null);

		} else if (asset.isPlane()) {
			return (asset.getCruiseSpeed()!=null) && (asset.getMaxSpeed()!=null) && (asset.getCeiling()!=null) &&
					(asset.getLength()!=null) && (asset.getWingspan()!=null);

		} else if (asset.isHelicopter()) {
			return (asset.getMaxSpeed()!=null) && (asset.getCeiling()!=null) &&
					(asset.getLength()!=null) && (asset.getRotorDiameter()!=null);

		}
		
		return false;
		
	}
	
}