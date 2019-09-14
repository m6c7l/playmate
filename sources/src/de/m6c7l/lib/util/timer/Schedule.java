/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.timer;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Schedule extends TimerTask {

	private Timer timer = null;
	private boolean running = false;
	private int loops = 0;
	private long time = 0;
	private int period = 0;
	
	public Schedule(int period) {
		this.period = period;
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(this,0,period);
	}

	public abstract void refresh(int loops, long time);
	
	public final void reset() {
		this.time = 0;
		this.loops = 0;		
	}
	
	public final void start() {
		this.running = true;
	}

	public final void stop() {
		this.running = false;
	}

	public final void restart() {		
		stop();
		reset();
		start();
	}
	
	public final int getLoops() {
		return this.loops;
	}
	
	public final long getTime() {
		return this.time;
	}
	
	public final int getPeriod() {
		return this.period;
	}
	
	public boolean cancel() {
		boolean b = super.cancel();
		if (b) {
			stop();
			this.timer.cancel();
			this.timer.purge();
		}
		return b;
	}
	
	@Override
	public final void run() {
		if (this.isStarted()) {
			loops++;
			time = time + period;
			this.refresh(loops,time);					
		}
	}

	public final boolean isStarted() {
		return this.running;
	}

	public final boolean isStopped() {
		return !this.running;
	}

}