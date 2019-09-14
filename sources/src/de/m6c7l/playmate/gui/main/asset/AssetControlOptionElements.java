/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.main.asset;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.slider.SliderPane;
import de.m6c7l.playmate.main.Asset;

public class AssetControlOptionElements extends SliderPane implements ChangeListener {

	// TODO: sonar emission and noise level
	
	public static final TYPE SONAR 		= TYPE.SONAR;
	public static final TYPE NOISE 		= TYPE.NOISE;

	private Asset asset = null;
	
	private TYPE type = null;
	
	public AssetControlOptionElements(TYPE type) {
		super(false,5,true,false);
		this.type = type;
		setAsset(null);
	}

	public void free() {
		this.asset = null;
	}
	
	public Asset getAsset() {
		return this.asset;
	}
	
	@Override
	public double convertValueToSlider(String value) throws NumberFormatException {
		if (this.asset!=null) {
            switch (this.type) {
                default:
                    break;
            }   
		}
		throw new NumberFormatException(value);
	}
	
	@Override
	public String convertSliderToValue(double value) {
		if (this.asset!=null) {
            switch (this.type) {
                default:
                    break;
            }   
		}
		return "";
	}

	@Override
	public String getColumnsString() {
		if (this.asset!=null) {
            switch (this.type) {
                default:
                    break;
            }   
		}
		return "";
	}

	@Override
	public void applyValue() {
		if ((this.asset!=null) && (this.isVisible())) {
            switch (this.type) {
                default:
                    break;
            }   
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (this.asset!=null) { 
			switch (this.type) {
				default:
					break;
			}	
		}
	}
	
	public void setAsset(Asset asset) {
		this.asset = asset;
		if (this.asset!=null) {
			this.setEnabled(true);
			applyValues();
		} else {
			this.setEnabled(false);
			this.setMaximum(0);
			this.setMinimum(0);
			this.setValue(0);
		}
	}
	
	private void applyValues() {
		if (this.asset!=null) {
            switch (this.type) {
                default:
                    break;
            }   
		}
	}
	
	private enum TYPE {
		
		SONAR,
		NOISE;
		
		TYPE() {}
				
	}

}

