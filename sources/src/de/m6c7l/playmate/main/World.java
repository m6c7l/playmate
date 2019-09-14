/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.timer.Schedule;

public class World implements Environment, ItemSelectable {

	private boolean exercise = false;
	
	private Asset hooked = null;
	private Asset selected = null;
	private Asset observer = null;
	
	private ArrayList<Asset> assets = null;
	private ArrayList<Surface> surface = null;
	
	private ClockTask ct = null;
	private long time = 0;
	private long startTime = 0;
	
	private CopyOnWriteArrayList<ChangeListener> changeListeners = null;
	private CopyOnWriteArrayList<ItemListener> itemListeners = null;
	private CopyOnWriteArrayList<ListDataListener> listDataListeners = null;
	
	private double rangeAcoustic = Double.MAX_VALUE;
	private double rangeVisual = Double.MAX_VALUE;
	private double currentDrift = 0; // mit m/s
	private double currentSet = 0; // setzt in
	
	public World() {
		this.assets = new ArrayList<Asset>();
		this.surface = new ArrayList<Surface>();
		this.changeListeners = new CopyOnWriteArrayList<ChangeListener>();
		this.itemListeners = new CopyOnWriteArrayList<ItemListener>();
		this.listDataListeners = new CopyOnWriteArrayList<ListDataListener>();
		this.ct = new ClockTask(this);
		
		rangeAcoustic = 32000;
		rangeVisual = 16000;
		
	}

	public void free() {
		this.changeListeners.clear();
		this.itemListeners.clear();
		this.listDataListeners.clear();
		this.selected = null;
		this.observer = null;
		this.hooked = null;
		for (int i=0; i<assets.size(); i++) { assets.get(i).free(); } this.assets.clear();
		this.surface.clear();
		this.ct.cancel();
	}
	
	/*
	 * Oberfläche
	 */
	
	public boolean put(Surface surface) {
		int index = this.surface.indexOf(surface);
		if (index==-1) {
			return this.surface.add(surface);	
		} else {
			return this.surface.set(index,surface)!=null;
		}
	}
	
	public Surface getSurface(int index) {
		return this.surface.get(index);
	}
	
	public int getSurfaceCount() {
		return this.surface.size();
	}
	
	public boolean isLand(Position pos) {
		for (int i=0; i<this.surface.size(); i++)
			if (this.surface.get(i).isLand(pos)) return true;
		return false;
	}
	
	public boolean isWater(Position pos) {
		for (int i=0; i<this.surface.size(); i++)
			if (this.surface.get(i).isWater(pos)) return true;
		return false;
	}
	
	public boolean isCoast(Position pos) {
		for (int i=0; i<this.surface.size(); i++)
			if (this.surface.get(i).isCoast(pos)) return true;
		return false;
	}
	
	public boolean isCovert(Position pos1, Position pos2) {
		for (int i=0; i<this.surface.size(); i++) 
			if (this.surface.get(i).isCovert(pos1,pos2)) return true;
		return false;
	}
		
	/*
	 * assets
	 */
	
	public boolean put(Asset asset) {
		if (isObserver(asset)) setObserver(asset);
		int index = this.assets.indexOf(asset);
		if (index==-1) {
			if (this.assets.add(asset)) {
				fireListDataEvent(+1);
				return true;
			}
		} else {
			if (this.assets.set(index,asset)!=null) {
				fireListDataEvent(0);
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(Asset asset) {
		return this.assets.indexOf(asset);
	}
	
	public Asset getAsset(int index) {
		return this.assets.get(index);
	}
	
	public int getAssetCount() {
		return this.assets.size();
	}
	
	@Override
	public Object[] getSelectedObjects() {
		return new Object[] {selected};
	}
	
	/*
	 * selected asset
	 */

	public boolean isSelected(Asset asset) {
		return asset == this.getSelected();
	}

	public Asset getSelected() {
		return selected;
	}
	
	public void setSelected(Asset asset) {
		selected = asset;
		fireItemEvent(selected!=null);
	}

	public boolean remove(Asset asset) {
		int index = this.assets.indexOf(asset);
		if (index>-1) {
			if (this.assets.remove(asset)) {
				if (isSelected(asset)) setSelected(null);
				if (isHooked(asset)) setHooked(null);
				if (isObserver(asset)) {
					setObserver(null);
					this.stop();
				}
				asset.free();
				fireListDataEvent(-1);
				return true;
			}
		}
		return false;
	}

	/*
	 * observed asset
	 */

	public boolean isHooked(Asset asset) {
		return asset == this.getHooked();
	}

	public Asset getHooked() {
		return hooked;
	}
	
	public void setHooked(Asset asset) {
		hooked = asset;
		fireItemEvent(selected!=null);
	}

	/*
	 * display
	 */
	
	public void setExercising(boolean exercising) {
		exercise = exercising;
		fireChangeEvent();
		fireItemEvent(true);
	}
	
	public boolean isExercising() {
		return this.exercise;
	}
	
	/*
	 * own ship
	 */

	public boolean isObserver(Asset asset) {
		return asset instanceof OwnShip;
	}
	
	public Asset getObserver() {
		return observer;
	}
	
	protected void setObserver(Asset asset) {
		observer = asset;
		fireItemEvent(observer!=null);
	}
	
	/*
	 * ranges
	 */

	public double getAcousticRange() {
		return rangeAcoustic;
	}

	public double getVisualRange() {
		return rangeVisual;
	}
	
	public void setAcousticRange(double value) {
		rangeAcoustic = value;
	}

	public void setVisualRange(double value) {
		rangeVisual = value;
	}
	
	
	/*
	 * current
	 */
	
	public double getCurrentSet() {
		return currentSet;
	}

	public double getCurrentDrift() {
		return currentDrift;
	}

	public void setCurrentSet(double value) {
		this.currentSet = value;
	}

	public void setCurrentDrift(double value) {
		this.currentDrift = value;
	}
	
	/*
	 * time
	 */
	
	public boolean isTimePast() {
		return this.getTime()<this.getTimeLast();		
	}
	
	public boolean isTimePresent() {
		return this.getTime()==this.getTimeLast();
	}
	
	public long getTimeFirst() {
		return this.startTime;
	}
	
	public long getTimeLast() {
		return this.ct.getTimeElapsed();
	}
	
	public int getTimeSpeedup() {
		return this.ct.getTimeSpeedup();
	}

	public boolean setTimeSpeedup(int factor) {
		return this.ct.setTimeSpeedup(factor);
	}
	
	public int getMaximumTimeSpeedup() {
		return this.ct.getMaximumTimeSpeedup();
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void setTime(long seconds) {
		this.time = seconds;
		fireChangeEvent();
	}

	public boolean start() {
		return this.setTimeSpeedup(1);
	}

	public boolean stop() {
		return this.setTimeSpeedup(0);
	}
	
	public boolean isStarted() {
		return this.getTimeSpeedup()>0;
	}

	public boolean isStopped() {
		return !this.isStarted();
	}
	
	/*
	 * item listener (selection of objects)
	 */
	
	public void addItemListener(ItemListener l) {
		int index = this.itemListeners.indexOf(l);
		if (index==-1) {
			this.itemListeners.add(l);
		} else {
			this.itemListeners.set(index,l);			
		}
	}

	public int getItemListenerCount() {
		return this.itemListeners.size();
	}
	
	public void removeItemListener(ItemListener l) {
		this.itemListeners.remove(l);
	}
		  
	private void fireItemEvent(boolean select) {
		ItemEvent e = null;
		if (select) {
			e = new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,null,ItemEvent.SELECTED);	
		} else {
			e = new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,null,ItemEvent.DESELECTED);
		}
		for (ItemListener l : itemListeners) l.itemStateChanged(e);
	}
	
	/*
	 * change listener (for time)
	 */
	
	public void addChangeListener(ChangeListener l) {
		int index = this.changeListeners.indexOf(l);
		if (index==-1) {
			this.changeListeners.add(l);			
		} else {
			this.changeListeners.set(index,l);						
		}
	}

	public int getChangeListenerCount() {
		return this.changeListeners.size();
	}
	
	public void removeChangeListener(ChangeListener l) {
		this.changeListeners.remove(l);
	}
		  
	private void fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : changeListeners) {
			l.stateChanged(e);	
		}
	}
	
	/*
	 * list data listener
	 */

	public void addListDataListener(ListDataListener l) {
		int index = this.listDataListeners.indexOf(l);
		if (index==-1) {
			this.listDataListeners.add(l);			
		} else {
			this.listDataListeners.set(index,l);						
		}
	}

	public int getListDataListenerCount() {
		return this.listDataListeners.size();
	}
	
	public void removeListDataListener(ListDataListener l) {
		this.listDataListeners.remove(l);
	}
		  
	private void fireListDataEvent(int type) {
		ListDataEvent e = null;
		if (type<0) {
			e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, this.getAssetCount());
		} else if (type>0) {
			e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, this.getAssetCount());			
		} else {
			e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.getAssetCount());			
		}
		for (ListDataListener l : listDataListeners) {
			if (type<0) {
				l.intervalRemoved(e);	
			} else if (type>0) {
				l.intervalAdded(e);	
			} else {
				l.contentsChanged(e);	
			}			
		}
	}
	
	private static class ClockTask extends Schedule {

		private static final int MAX_SPEEDUP = 50;

		private int timeSpeedup = 0;
		private long timeElapsed = 0;
		private int counter = 0;
		
		private World world = null;
		private boolean past = false;
		
		public ClockTask(World world) {
			super(1000/MAX_SPEEDUP);
			this.world = world;
			super.start();
		}
		
		public boolean cancel() {
			boolean b = super.cancel();
			if (b) this.world = null;
			return b;
		}
		
		public int getMaximumTimeSpeedup() {
			return MAX_SPEEDUP;
		}
		
		public boolean setTimeSpeedup(int factor) {
			if ((factor>=0) && (factor<=MAX_SPEEDUP)) {
				this.timeSpeedup = factor;
				return true;
			}
			return false;
		}
		
		public long getTimeElapsed() {
			return this.timeElapsed;
		}

		public int getTimeSpeedup() {
			return this.timeSpeedup;
		}
		
		@Override
		public void refresh(int loops, long time) {
			
			if (timeElapsed>=96*60*60) {
				this.world.stop();
			}
			
			if (this.world.isStopped()) {
				this.past = false;
				this.world.fireChangeEvent();
				return;
			}
	
			counter++;

			if (counter*super.getPeriod()*this.getTimeSpeedup()<1000) return;
						
			if (this.world.getTime()<this.world.getTimeLast()) {
				
				this.past = true;
				this.world.setTime(this.world.getTime()+1); 
				
			} else {
				
				if (!past) {

					this.timeElapsed++;
					this.world.setTime(timeElapsed); // changed!
					
				} else {

					this.world.stop();
					this.past = false;

				}
				
			}
			
			counter = 0;	

		}
		
	}
	
}