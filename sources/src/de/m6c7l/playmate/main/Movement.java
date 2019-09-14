/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.util.ArrayList;

import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.lib.util.text.Plain;

public class Movement {

	private ArrayList<Long> time = null;
	private ArrayList<Double> heading = null;
	private ArrayList<Double> speed = null;

	protected Movement(
			long initialTime,
			double heading,
			double speed) {
		
		this.time = new ArrayList<Long>();
		this.heading = new ArrayList<Double>();
		this.speed = new ArrayList<Double>();
		
		this.time.add(initialTime);
		this.heading.add((heading+360)%360);
		this.speed.add(speed);
	}

	/*
	 * time
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
	 * heading
	 */

	public boolean setHeading(long time, double heading) {
		return this.put(time, heading, this.lastSpeed());
	}

	public double lastHeading() {
		return this.heading.get(this.time.size()-1);
	}
	
	public double getHeading(long time) {
		if (this.time.contains(time)) {
			return this.heading.get(this.time.indexOf(time));
		} else if ((time>this.firstTime()) && (time<this.lastTime())) {	
			int[] index = getTimeIndices(time);
			return this.heading.get(index[0]);
		} else  if (time>this.lastTime()) {	
			return this.lastHeading();
		} else {
			return this.getHeading(this.firstTime());
		}
	}	

	/*
	 * speed
	 */

	public boolean setSpeed(long time, double speed) {
		return this.put(time, this.lastHeading(), speed);		
	}
	
	public double lastSpeed() {
		return this.speed.get(this.time.size()-1);
	}
	
	public double getSpeed(long time) {
		if (this.time.contains(time)) {
			return this.speed.get(this.time.indexOf(time));

		} else if ((time>this.firstTime()) && (time<this.lastTime())) {	
			int[] index = getTimeIndices(time);
			return this.speed.get(index[0]);
		} else if (time>this.lastTime()) {	
			return this.lastSpeed();
		} else {
			return this.getSpeed(this.firstTime());
		}
	}
	
	/*
	 * rate of turn 
	 */
	
	public double getRateOfTurn(long time) {
		if ((time>this.firstTime()) && (time<=this.lastTime())) {	
			return NAUTIC.getAngleDifference(
					this.getHeading(time-1),
					this.getHeading(time),0)*60.0;
		}
		return 0.0;
	}
	
	/*
	 * acceleration
	 */
	
	public double getAcceleration(long time) {
		if ((time>this.firstTime()) && (time<=this.lastTime())) {	
			return this.getSpeed(time)-this.getSpeed(time-1);
		}
		return 0.0;
	}

	/*
	 * common
	 */
	
	public int size() {
		return this.time.size();
	}
		
	private synchronized boolean put(long time, double heading, double speed) {
		long lt = this.lastTime();
		boolean res = false;
		if (time>lt) {
			boolean a = this.heading.add((heading+360)%360);
			boolean b = this.speed.add(speed);
			boolean c = this.time.add(time);
			res = a && b && c;
		} else if (time==lt) {
			boolean a = this.heading.set(this.heading.size()-1,(heading+360)%360)!=null;
			boolean b = this.speed.set(this.speed.size()-1,speed)!=null;
			res = a && b;
		}
		return res;
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
	
	public String toString() {
		String[][] tab = new String[100+1][6];
		tab[0][0] = "";
		tab[0][1] = "time";
		tab[0][2] = "hdg";
		tab[0][3] = "spd";
		tab[0][4] = "rot";		
		tab[0][5] = "acc";
		int x = time.size();
		int m = (x/100)+1;
		int n = 1;
		for (int i=1; i<=x; i=i+m) {
			long t = time.get(i-1);
			tab[n][0] = n +"";
			tab[n][1] = VALUE.TIME(t) +"";
			tab[n][2] = heading.get(i-1).intValue() +"";
			tab[n][3] = Math.round(speed.get(i-1)*10)/10.0 +"";
			tab[n][4] = Math.round(getRateOfTurn(t)*10)/10.0 +"";	
			tab[n][5] = Math.round(getAcceleration(t)*10)/10.0 +"";
			n++;
		}
		return Plain.toTable(tab,new int[] {+1,+1,+1,+1,+1,+1},true);
	}

}