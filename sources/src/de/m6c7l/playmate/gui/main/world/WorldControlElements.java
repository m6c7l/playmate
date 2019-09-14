/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.main.world;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.slider.SliderPane;
import de.m6c7l.playmate.main.VALUE;
import de.m6c7l.playmate.main.World;

public class WorldControlElements extends SliderPane implements ChangeListener {

	public static final TYPE TIMELINE 		= TYPE.TIMELINE;
	public static final TYPE QUICKMOTION	= TYPE.QUICK_MOTION;
	
	private World world = null;
	
	private TYPE type = null;
	
	public WorldControlElements(TYPE type, World world) {
		super(true,5,true,false);
		this.type = type;
		this.world = world;
	}
	
	public void free() {
		this.world = null;
	}

	@Override
	public double convertValueToSlider(String value) throws NumberFormatException {
		String s = null;
		Matcher m = null;
		switch (this.type) {
			case QUICK_MOTION:
				s = "^[0-9]{1,}(?=([xX])$)";
				m = Pattern.compile(s).matcher(value);
				if (m.find()) {
					return new Double(m.group(0));
				}
				break;
			case TIMELINE:
				s = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$";
				m = Pattern.compile(s).matcher(value);
				if (m.find()) {
					String hh = m.group(0).substring(0,2);
					String mm = m.group(0).substring(3,5);
					String ss = m.group(0).substring(6,8);
					return (new Integer(hh)*60*60)+(new Integer(mm)*60)+new Integer(ss);
				}
				break;
		}
		throw new NumberFormatException(value);
	}
	
	@Override
	public String convertSliderToValue(double value) {
		switch (this.type) {
			case QUICK_MOTION: 
				return toString(value,0) + "x";
			case TIMELINE:
				return VALUE.TIME((long)value);
		}
		return "";
	}

	@Override
	public String getColumnsString() {
		if (this.world!=null) {
			switch (this.type) {
			case QUICK_MOTION: 
				return (toString(world.getMaximumTimeSpeedup(),0) + "x");
			case TIMELINE:
				return VALUE.TIME(0);
			}
		}
		return "";
	}

	@Override
	public void applyValue() {
		if ((this.world!=null) && (this.isVisible())) {
			switch (this.type) {
				case QUICK_MOTION: 
					world.setTimeSpeedup((int)getValue());
					break;
				case TIMELINE:
					world.setTime((long)getValue());
					break;
			}
			
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (this.world!=null) {
			switch (this.type) {
				case QUICK_MOTION:
					this.setMaximum(world.getMaximumTimeSpeedup());
					break;
				case TIMELINE:
					this.setMaximum(world.getTimeLast());
					break;
			}
		}
	}

	private enum TYPE {
		
		TIMELINE,
		QUICK_MOTION;
		
		TYPE() {}
				
	}
	
}
