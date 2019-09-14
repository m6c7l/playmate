/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.util.ArrayList;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.text.Plain;

public class Track {

	private ArrayList<Long> time = null;
	private ArrayList<Position> position = null;
	private ArrayList<Double> dist = null;
	private double lastCOG = 0;
	private double lastSOG = 0;
	
	private Track(long initialTime,Position initialPosition) {
		this.time = new ArrayList<Long>();
		this.position = new ArrayList<Position>();
		this.dist = new ArrayList<Double>();
		this.time.add(initialTime);
		this.position.add(initialPosition);
		this.dist.add(0.0);
	}
	
	public Track(
			long initialTime,
			Position initialPosition,
			double cog,
			double sog) {
		this(initialTime,initialPosition);
		this.lastCOG = (cog+360)%360;
		this.lastSOG = sog;
	}

	public Track(
			long initialTime,
			Position initialPosition,
			long firstWaypointTime,
			Position firstWaypoint) throws Exception {
		this(initialTime,initialPosition);
		if (initialTime>=firstWaypointTime) throw new Exception("waypoint time must be greater than initial time");
		if (initialPosition.equals(firstWaypoint)) throw new Exception("initial position must be different to waypoint");
		this.setPosition(firstWaypointTime,firstWaypoint);
	}

	/*
	 * times
	 */

	public long firstTime() {
		return this.time.get(0);
	}
	
	public long lastTime() {
		return this.time.get(this.time.size()-1);
	}
	
	public long getTime(int i) {
		return this.time.get(i);
	}
	
	/*
	 * position
	 */

	public Position lastPosition() {
		return this.position.get(this.time.size()-1);
	}
	
	private Position penultimatePosition() {
		return this.position.get(this.time.size()-2);
	}
	
	public Position getPosition(long time) {
		if (this.time.contains(time)) {
			return this.position.get(this.time.indexOf(time));
		} else if ((time>this.firstTime()) && (time<this.lastTime())) {	
			int[] index = getTimeIndices(time);
			long t1 = this.time.get(index[0]);
			long t2 = time;
			long t3 = this.time.get(index[1]);
			double x = 1.0*(t2-t1)/(t3-t1);
			Position p1 = this.position.get(index[0]);
			Position p3 = this.position.get(index[1]);
			double d = p1.getRhumbLine(p3).getDistance();
			if (d!=0) {
				Double b = p1.getRhumbLine(p3).getBearing();
				return p1.getPositionRhumbLine(b,d*x,
						p1.getAltitude()+((p3.getAltitude()-p1.getAltitude())*x));
			} else {
				return new Position(p1.getLatitude(),p1.getLongitude(),
						p1.getAltitude()+((p3.getAltitude()-p1.getAltitude())*x));
			}
		} else if (time>this.lastTime()) {	
			return this.lastPosition().getPositionRhumbLine(
				this.lastCOG,this.lastSOG*(time-this.lastTime()),this.lastPosition().getAltitude());
		} else {
			 return this.getPosition(this.firstTime());
		}
	}	
	
	/*
	 * misc
	 */
	
	public Double getCourseOverGround(long time) {
		if (time>=this.lastTime()) return this.lastCOG;
		Position p1 = getPosition(time);
		Position p2 = getPosition(time+1);
		if ((p1!=null) && (p2!=null)) {
			return p1.getRhumbLine(p2).getBearing();
		}
		return null;	
	}
	
	public double getSpeedOverGround(long time) {
		if (time>=this.lastTime()) return this.lastSOG;
		Position p1 = getPosition(time);
		Position p2 = getPosition(time+1); // 1 s ~ 1 m/s
		if ((p1!=null) && (p2!=null)) {
			return p1.getRhumbLine(p2).getDistance();
		}
		return 0;	
	}
	
	public double getDistanceTravelled(long time) {
	
		if (this.time.contains(time)) {
			return this.dist.get(this.time.lastIndexOf(time));

		} else 	if ((time>this.firstTime()) && (time<this.lastTime())) {	
			int i = 0; while (time>this.time.get(i)) { i++; }
			return this.dist.get(i-1)+
					(this.dist.get(i)-this.dist.get(i-1))*
						(time-this.time.get(i-1))/(this.time.get(i)-this.time.get(i-1));

		} else { // if (time>=this.lastTime())
			return this.dist.get(this.dist.size()-1)
				+getPosition(time).getRhumbLine(lastPosition()).getDistance();
		}

	}
	
	/*
	 * position
	 */

	public boolean setPosition(long time) {
		return this.put(time, null, null, null, null);
	}
	
	public boolean setPosition(long time, double cog) {
		return this.put(time, null, cog, null, null);
	}
	
	public boolean setPosition(long time, double cog, double sog) {
		return this.put(time, null, cog, sog, null);
	}
	
	public boolean setPosition(long time, double cog, double sog, double alt) {
		return this.put(time, null, cog, sog, alt);
	}
	
	public boolean setPosition(long time, Position position) {
		return this.put(time, position, null, null, null);
	}
	
	public boolean setPosition(long time, Position position, double cog) {
		return this.put(time, position, cog, null, null);
	}
	
	public boolean setPosition(long time, Position position, double cog, double sog) {
		return this.put(time, position, cog, sog, null);
	}
	
	public boolean setPosition(long time, Position position, double cog, double sog, double alt) {
		return this.put(time, position, cog, sog, alt);
	}

	/*
	 * common
	 */
	
	public int size() {
		return this.time.size();
	}

	private int[] getTimeIndices(long t) {
		int prevOrNothing = -1;
		int nextOrCurrent = -1;
		int idxs = 0;
		int idxe = this.time.size()-1;
		int idx = (idxs+idxe)/2; 
		while (idxs+1!=idxe) {			
			if (this.time.get(idx)<t) {
				idxs = idx;
			} else {
				idxe = idx;
			}
			idx = (idxs+idxe)/2; 
		}
		prevOrNothing = idxs;
		nextOrCurrent = idxe;
		return new int[] {prevOrNothing,nextOrCurrent};
	}
	
	private synchronized boolean put(long time, Position position, Double cog, Double sog, Double alt) {
		long lt = this.lastTime();
		Position lp = this.lastPosition();
		Position p = position;
		boolean res = false;
		Position.RhumbLine lpp = lp.getRhumbLine(p);
		if (time>lt) {
			if (p==null)	p = lp.getPositionRhumbLine(this.lastCOG,this.lastSOG*(time-lt),lp.getAltitude());
			if (alt!=null) 	p.setAltitude(alt);
			if (cog!=null) 	this.lastCOG = (cog+360)%360;
			else 			this.lastCOG = lpp.getBearing()!=null ? lpp.getBearing() : this.lastCOG;
			if (sog!=null) 	this.lastSOG = sog;
			else 			this.lastSOG = lpp.getDistance()/(time-lt);
			boolean a = this.dist.add(this.dist.get(this.dist.size()-1)+lpp.getDistance());
			boolean b = this.position.add(p);
			boolean c = this.time.add(time);
			res = a && b && c;
		} else if (time==lt) {
			if (p==null)	p = lp;
			if (alt!=null) 	p.setAltitude(alt);
			if (cog!=null) 	this.lastCOG = (cog+360)%360;
			if (sog!=null) 	this.lastSOG = sog;
			boolean a = true;
			if (this.dist.get(this.dist.size()-1)>0.0) {
				a = this.dist.set(
						this.dist.size()-1,
						this.dist.get(this.dist.size()-2)+this.penultimatePosition().getRhumbLine(p).getDistance())!=null;
			}
			boolean b = this.position.set(this.position.size()-1,p)!=null;
			res = a && b;
		}
		return res;
	}
	
	public String toString() {
		String[][] tab = new String[100+1][6];
		tab[0][0] = "";
		tab[0][1] = "time";
		tab[0][2] = "position";
		tab[0][3] = "cog";
		tab[0][4] = "sog";		
		tab[0][5] = "distance";
		int x = time.size();
		int m = (x/100)+1;
		int n = 1;
		for (int i=1; i<=x; i=i+m) {
			long t = time.get(i-1);
			Double cog = getCourseOverGround(t);
			tab[n][0] = n +"";
			tab[n][1] = VALUE.TIME(t) +"";
			tab[n][2] = position.get(i-1) +"";
			tab[n][3] = (cog!=null ? cog.intValue() : "") +"";
			tab[n][4] = (Math.round(getSpeedOverGround(t)*10))/10.0 +"";	
			tab[n][5] = dist.get(i-1).intValue() +"";
			n++;
		}
		return Plain.toTable(tab,new int[] {+1,+1,-1,+1,+1,+1}, true);
	}

}