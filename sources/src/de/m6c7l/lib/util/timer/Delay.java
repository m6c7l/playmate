/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.timer;

import java.util.Timer;
import java.util.TimerTask;

public class Delay {
	
	private int millis = 0;
	
	public static void sleep(int millis) {
		new Delay(millis).execute();
	}
	
	public Delay(int period) {
		this.millis = period;
	}
	
	public void execute() {
		new DelayTask().execute(this.millis);
	}

	private static class DelayTask extends TimerTask {
		
		private int periods = 1;
		private boolean alive = true;
		private Timer timer = null;
		
		public DelayTask() {
			timer = new Timer();
		}
		
		public void run() {
			if (this.periods<=0) this.free();
			this.periods--;
		}

		public void execute(int millis) {
			timer.scheduleAtFixedRate(this,0,millis);
			do {
	            if (Thread.currentThread().isInterrupted()) this.free();
			} while (this.isAlive());
		}

		public boolean isAlive() {
			return this.alive;
		}

		private void free() {
			super.cancel();			
			timer.cancel();
			timer.purge();
			this.alive = false;
		}
		
	}

}
