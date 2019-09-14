/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public abstract class Aircraft extends Asset {

	public Aircraft(
			World world,
			Object id,
			Position position,
			double minSpeed,
			double maxSpeed,
			double ceiling,
			boolean military,
			double length,
			double width) {
		
		super(	world,
				id,
				position,
				minSpeed,
				maxSpeed,
				(width+10)-((width+10)%10), // mindesthöhe = länge
				ceiling,
				military,
				false,
				length,
				width);

	}
	
	public Position getOutlook(long time) {
		return getPosition(time);
	}
	
	public Position getOutlook() {
		return getOutlook(getTime());
	}
	
	public boolean isHookable() {
		return false;
	}

	public double getCeiling() {
		return super.getAltitudeMaximum();
	}
	
	public boolean canHover() {
		return this.getSpeedMinimum()==0;
	}
	
	public Point draw(Canvasable canvas) {
		
		Point point = super.draw(canvas);
		
		Graphics2D g2d = canvas.getPaintbox();
		int size = getDrawSize();
		initialize(g2d);
		
		if ((point!=null) && (!canvas.isMagnified())) {

			if (this.isMilitary()) {
				g2d.drawLine(point.x - size, point.y, point.x, point.y - size);
				g2d.drawLine(point.x, point.y - size, point.x + size, point.y);
			} else {
				size = (int)((size/1.41)+0.5);
				g2d.drawLine(point.x - size, point.y - size, point.x - size, point.y); // left
				g2d.drawLine(point.x - size, point.y - size, point.x + size, point.y - size); // top
				g2d.drawLine(point.x + size, point.y - size, point.x + size, point.y); // right
			}
			
		}
		
		if (!canvas.isMagnified()) {
			
			g2d.setStroke(new BasicStroke(2.0f));
			
			if (this.isSelected()) {

				long t = (this.getTime()/10)*10;
				while ((t>=this.getTrack().firstTime()) && (this.getTime()<t+(10*6))) {
					Point p = canvas.getPoint(this.getPosition(t));
					g2d.drawLine(p.x, p.y, p.x, p.y);
					t = t - 10;		
				}
								
			}
			
		}

		return point;
		
	}

}
