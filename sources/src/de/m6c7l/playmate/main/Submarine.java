/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public class Submarine extends Watercraft {

	private double maxSpeedSurfaced = 0;
	private double maxSpeedDived = 0;

	public Submarine(
			World world,
			Object id,
			Position position,
			double maxSpeedSurfaced,
			double maxSpeedDived,
			double maxDepth,
			double draft,
			boolean military,
			double length,
			double beam) {
		
		super(	world,
				id,
				position,
				Math.max(maxSpeedSurfaced,maxSpeedDived),
				maxDepth,
				draft,
				military,
				false,
				length,
				beam);

		this.maxSpeedSurfaced = maxSpeedSurfaced;
		this.maxSpeedDived = maxSpeedDived;
	}

	public Position getOutlook() {
		if (isDived() || isVarying()) {
			Position p = getPosition();
			return new Position(p.getLatitude(),p.getLongitude(),p.getAltitude()+getHeight()+2.5);			
		}
		return super.getOutlook();
	}
	
	public double getHeight() {
		return super.getLength()/(14.0/3.0);
	}

	public void setDepth(double depth) {
		super.setAltitude(-Math.abs(depth));
	}

	public double getDepth() {
		return Math.abs(super.getAltitude());
	}

	public boolean isVarying() {
		return (!isDived()) && (getDepth()>getDraft());		
	}
	
	public boolean isDived() {
		return getDepth()>getDraft()*2;
	}
	
	public double getSpeedSurfacedMaximum() {
		return this.maxSpeedSurfaced; 
	}

	public double getSpeedDivedMaximum() {
		return this.maxSpeedDived; 
	}
	
	@Override
	public double getSpeedMaximum() {
		if (super.isDived()) {
			return this.getSpeedDivedMaximum();
		} else {
			return this.getSpeedSurfacedMaximum();
		} 
	}
	
	protected double getSpeedYaw() {
		long lt = super.getTrack().lastTime();
		if (isDived(lt)) return super.getSpeedYaw()*1.25;
		return super.getSpeedYaw();
	}
	
	protected double getSpeedPitchPositive() {
		return Math.abs(this.getSpeedMaximum()/8.0);
	}
	
	protected double getSpeedPitchNegative() {
		return getSpeedPitchPositive();
	}
	
	@Override
	public Point draw(Canvasable canvas) {
		
		if (!isDrawable()) return null;
		
		Point point = super.draw(canvas);
		
		return point;
		
	}

}
