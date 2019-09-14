/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.text.Plain;

public class Route implements Drawable {

	public static final BasicStroke STROKE = new BasicStroke(1.0f);
	
	private ArrayList<Waypoint> waypoints = null;
	private Asset asset = null;
	private boolean isCircular = false;
	private Waypoint selected = null;
	private boolean isAcross = false;
	private Position origin = null;
	
	protected Route(Asset asset) {
		this.asset = asset;
		this.waypoints = new ArrayList<Waypoint>();
	}
	
	public void free() {
		for (int i=0; i<waypoints.size(); i++) {
			waypoints.get(i).free();
		}
		this.waypoints.clear();
		this.asset = null;
	}
	
	protected void setOrigin(Position position) {
		this.origin = position;
	}
	
	protected Position getOrigin() {
		return this.origin;
	}
	
	protected Waypoint getSelected() {
		return this.selected;
	}
	
	protected void setSelected(Waypoint value) {
		this.selected = value;
	}
	
	protected void reset() {
		for (int i=waypoints.size()-1; i>=0; i--) {
			waypoints.get(i).setAchieved(false);
		}
	}
	
	protected int indexOf(Waypoint wp) {
		return this.waypoints.indexOf(wp);
	}
	
	public Waypoint next() {
		Waypoint wp = firstWaypoint();
		while (wp.isAchieved()) wp = wp.next();
		return wp;
	}
	
	public void setCircular(boolean value) {
		if ((value) && (isFinished())) reset();
		this.isCircular = value;
	}
	
	public boolean isCircular() {
		return this.isCircular;
	}
	
	public void setAcross(boolean value) {
		this.isAcross = value;
	}
	
	public boolean isAcross() {
		return this.isAcross;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public int size() {
		return this.waypoints.size();
	}
	
	public boolean addWaypoint(Position position) {
		if (this.waypoints.size()==0) setOrigin(getAsset().getPosition());
		return this.waypoints.add(new Waypoint(this,position));
	}

	public Waypoint getWaypoint(int index) {
		return this.waypoints.get(index);
	}
	
	public Waypoint firstWaypoint() {
		if (this.size()>0) {
			return this.getWaypoint(0);
		}
		return null;
	}
	
	public Waypoint lastWaypoint() {
		if (this.size()>0) {
			return this.getWaypoint(this.size()-1);
		}
		return null;
	}
	
	@Override
	public boolean isDrawable() {
		if ((this.size()==0) || (!asset.isExisting())) return false;
		boolean b = (asset.getEnvironment().isStopped() && asset.isSelected()) || (asset.isHooked());
		return b;
	}

	@Override
	public int getDrawSize() {
		return 1;
	}
	
	public boolean isFinished() {
		if (size()>0) {
			return lastWaypoint().isAchieved();			
		}
		return true;
	}
	
	@Override
	public Point draw(Canvasable canvas) {
		if (!isDrawable()) return null;

		Position a = getOrigin();
		
		Graphics2D g2d = canvas.getPaintbox();

		asset.initialize(g2d);
		g2d.setColor(Asset.COLOR_DISABLED);
		if (asset.isNew()) g2d.setColor(Asset.COLOR_ENABLED);		
		g2d.setStroke(STROKE);
		
		for (int i=0; i<this.size(); i++) {
			Position b = this.waypoints.get(i).getPosition();

			Point ap = canvas.getPoint(a);
			Point bp = canvas.getPoint(b);
			TOOLBOX.drawDashedLine(g2d,ap.x,ap.y,bp.x,bp.y,getDrawSize()*1.5,getDrawSize()*6);										

			a = b;	
			this.waypoints.get(i).draw(canvas);
		}

		return canvas.getPoint(asset.getPosition());
	}
	
	public boolean equals(Object o) {
		if ((o!=null) && (o instanceof Route)) {
			Route r = (Route)o;
			return r.getAsset().equals(this.getAsset());
		}
		return false;
	}
	
	public int hashCode() {
	    int hc = 23;
	    int hashMultiplier = 31;
	    hc = hc * hashMultiplier + this.getAsset().hashCode();
	    return hc; 
	}
	
	public String toString() {
		String[][] tab = new String[100+1][4];
		tab[0][0] = "";
		tab[0][1] = "serial";
		tab[0][2] = "position";
		tab[0][3] = "achieved";
		int x = waypoints.size();
		int m = (x/100)+1;
		int n = 1;
		for (int i=1; i<=x; i=i+m) {
			Waypoint w = waypoints.get(i-1);
			tab[n][0] = n +"";
			tab[n][1] = w.getID() +"";
			tab[n][2] = w.getPosition() +"";
			tab[n][3] = w.isAchieved() +"";
			n++;
		}
		return Plain.toTable(tab,new int[] {+1,+1,-1,-1}, true);
	}
	
}