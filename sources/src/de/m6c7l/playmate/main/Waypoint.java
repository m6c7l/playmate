/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public class Waypoint implements Drawable, Selectable {

	private boolean achieved = false;
	private Position pos = null;
	private Route route = null;
	
	private int serial = 0;
	
	protected Waypoint(Route route, Position position) {
		this.route = route;
		this.pos = position;
		this.serial = route.lastWaypoint()!=null ? route.lastWaypoint().serial+1 : 1;
	}
	
	public void free() {
		this.pos = null;
		this.route = null;
	}
	
	public void setAchieved(boolean achieved) {
		this.achieved = achieved;	
		if ((route.isCircular()) && (route.isFinished())) {
			route.reset();
		}
	}
	
	public boolean isAchieved() {
		return this.achieved;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Integer getID() {
		return serial;
	}
	
	public Waypoint next() {
		int idx = route.indexOf(this)+1;
		if (idx==route.size()) {
			if (route.isCircular()) {
				return route.firstWaypoint();
			}
			return null;
		}
		return route.getWaypoint(idx);
	}
	
	@Override
	public Point getPoint(Canvasable canvas) {
		return canvas.getPoint(pos);
	}
	
	@Override
	public boolean isSelectable() {
	    if ((route==null) || (pos==null)) return false;
		return (route.getAsset().isSelected()) && (!isAchieved());
	}
	
	@Override
	public void setSelected(boolean selected) {
		if ((selected) && (this.isSelectable())) {			
			this.route.setSelected(this);
		} else {
			this.route.setSelected(null);
		}
	}

	@Override
	public boolean isSelected() {
        if ((route==null) || (pos==null)) return false;
		return (this==route.getSelected()) && (route.getAsset().isSelected());
	}

	@Override
	public Point draw(Canvasable canvas) {
		
		if (!isDrawable()) return null;
		
		Graphics2D g2d = canvas.getPaintbox();
		int size = getDrawSize();
		
		route.getAsset().initialize(g2d);
		
		g2d.setColor(Asset.COLOR_ENABLED);
		if (isAchieved()) { g2d.setColor(Asset.COLOR_DISABLED); }
		
		Point wp = this.getPoint(canvas);

		g2d.drawOval(
				wp.x-(size/2),
				wp.y-(size/2),
				size,
				size);
		
		g2d.setColor(Asset.COLOR_ENABLED);
		if (this.isAchieved()) g2d.setColor(Asset.COLOR_DISABLED);
		
		g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
		
		g2d.drawString(
					this.getID()+"",
					wp.x + size*2,
					wp.y + size*4);

		if (this.isSelected()) {
			size = size + 8;
			g2d.setColor(Asset.COLOR_MARK);
			g2d.drawOval(
					wp.x-(size/2),
					wp.y-(size/2),
					size,
					size);
		}
		
		return wp;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public int getDrawSize() {
		return 5;
	}

	@Override
	public Position getPosition() {
		return pos;
	}
	
	@Override
	public boolean setPosition(Position position) {
		if (isAchieved()) return false;
		pos.setPosition(position);
		return true;
	}
	
	public boolean equals(Object o) {
		if ((o!=null) && (o instanceof Waypoint)) {
			Waypoint wayp = (Waypoint)o;			
			return (( wayp.getID().equals(this.getID()) ) &&
			        ( ((wayp.getRoute()==null) && (this.getRoute()==null)) || wayp.getRoute().equals(this.getRoute()) ));
		}
		return false;
	}
	
//	public int hashCode() {
//	    int hc = 13;
//	    int hashMultiplier = 31;
//	    hc = hc * hashMultiplier + this.getRoute().hashCode();
//	    hc = hc * hashMultiplier + this.getID();
//	    return hc; 
//	}
	
} 

