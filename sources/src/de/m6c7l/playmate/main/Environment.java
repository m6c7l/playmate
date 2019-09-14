/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

public interface Environment extends Surface {

	public long getTimeFirst();
	public long getTime();
	public long getTimeLast();
	public int getTimeSpeedup();
	
	public boolean isStopped();
	public boolean isStarted();
	
	public double getCurrentSet();
	public double getCurrentDrift();
	
	public double getAcousticRange();
	public double getVisualRange();
	
	public Asset getObserver();
	
	public Selectable getSelected();
	public Hookable getHooked();
	
	public Asset getAsset(int index);
	public int getAssetCount();
	
	public boolean isExercising();
	
}
