/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.main.asset;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.slider.SliderPane;
import de.m6c7l.playmate.main.Asset;

public class AssetControlElements extends SliderPane implements ChangeListener {
	
	public static final TYPE HEADING 	= TYPE.HEADING;
	public static final TYPE SPEED 		= TYPE.SPEED;
	public static final TYPE DEPTH 		= TYPE.DEPTH;
	public static final TYPE HEIGHT 	= TYPE.HEIGHT;
	
	private Asset asset = null;
	
	private double min = 0;
	private double max = 0;
	
	private TYPE type = null;
	
	public AssetControlElements(TYPE type) {
		super(false,5,true,(type==HEADING?true:false));
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
			double d = parseLazy(value);
			switch (this.type) {
				case HEADING: 
					if ((d>=0) && (d<=360)) {
						if (d<180) return d+360;
						return d;
					}
					break;
				case SPEED:
					if ((d>=this.min) && (d<=this.max)) return d;
					break;
				case DEPTH:				
					if ((d>=Math.abs(this.max)) && (d<=Math.abs(this.min))) return d;
					break;
				case HEIGHT:
					if ((d>=this.min) && (d<=this.max)) return d;
					break;
			}
		}
		throw new NumberFormatException(value);
	}
	
	private static double parseLazy(String doubleStrIn) {
		doubleStrIn = doubleStrIn.replaceAll("[^\\d,\\.]++", "");
		if (doubleStrIn.matches(".+\\.\\d+,\\d+$"))
			return Double.parseDouble(doubleStrIn.replaceAll("\\.", "").replaceAll(",", "."));
		if (doubleStrIn.matches(".+,\\d+\\.\\d+$"))
			return Double.parseDouble(doubleStrIn.replaceAll(",", ""));
		return Double.parseDouble(doubleStrIn.replaceAll(",", "."));
	}
	
	@Override
	public String convertSliderToValue(double value) {
		if (this.asset!=null) {
			switch (this.type) {
				case HEADING: 
					return toString((value+360)%360,3,0);
				case SPEED:
					return toString(value,asset.isAircraft() ? 0 : 1);
				case DEPTH:
					return toString(value,1);
				case HEIGHT:
					return toString(value,0);
			}
		}
		return "";
	}

	@Override
	public String getColumnsString() {
		if (this.asset!=null) {
			switch (this.type) {
				case HEADING: 
					return toString(0,3,0);
				case SPEED:
					int dec = asset.isAircraft() ? 0 : 1;
					return toString(asset.getSpeedMaximum(),dec);
				case DEPTH:
					return toString(Math.abs(asset.getAltitudeMinimum()),1);
				case HEIGHT:
					return toString(Math.abs(asset.getAltitudeMaximum()),0);
			}
		}
		return "";
	}

	@Override
	public void applyValue() {
		if ((this.asset!=null) && (this.isVisible())) {
			switch (this.type) {
				case HEADING: 
					asset.setHeading(this.getValue());
					break;
				case SPEED:
					asset.setSpeed(this.getValue());
					break;
				case DEPTH:
					asset.setAltitude(-this.getValue());
					break;
				case HEIGHT:
					asset.setAltitude(this.getValue());
					break;
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (this.asset!=null) { 
			switch (this.type) {
				case SPEED:
					if ((this.min != this.asset.getSpeedMinimum()) || (this.max != this.asset.getSpeedMaximum())) {
						applyValues();
						if (this.asset.getSpeedFinal()>this.max) { 
							this.setValue(this.max);
							applyValue();
						} else if (this.asset.getSpeedFinal()<this.min) {
							this.setValue(this.min);
							applyValue();
						}
					}
					break;
				case DEPTH:
					if ((this.min != this.asset.getAltitudeMinimum()) || (this.max != this.asset.getAltitudeMaximum())) {
						applyValues();
						if (this.asset.getAltitude()>this.max) {
							this.setValue(this.max);
							applyValue();
						} else if (this.asset.getAltitude()<this.min) {
							this.setValue(this.min);
							applyValue();
						}
					}
					break;
				case HEIGHT:
					if ((this.min != this.asset.getAltitudeMinimum()) || (this.max != this.asset.getAltitudeMaximum())) {
						applyValues();
						if (this.asset.getAltitude()>this.max) {
							this.setValue(this.max);
							applyValue();
						} else if (this.asset.getAltitude()<this.min) {
							this.setValue(this.min);
							applyValue();
						}
					}
					break;
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
			if (this.type==HEADING) {
				if (!this.asset.isHeadingAltering()) {
					this.asset.setRotation(0);
				}
			}
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
				case HEADING: 
					this.setMaximum(360+180);
					this.setMinimum(180);
					this.setSliderStep(10.0);
					this.setSpinnerStep(1.0);
					this.setValue(this.asset.getHeadingFinal()<180 ? this.asset.getHeadingFinal()+360 : this.asset.getHeadingFinal());
					break;
				case SPEED:
					this.min = this.asset.getSpeedMinimum();
					this.max = this.asset.getSpeedMaximum();
					this.setMinimum(this.asset.getSpeedMinimum());
					this.setMaximum(this.asset.getSpeedMaximum());
					if (this.asset.isAircraft()) {
						this.setSliderStep(10.0);
						this.setSpinnerStep(1.0);
					} else {
						this.setSliderStep(1.0);
						this.setSpinnerStep(0.1);
					}
					this.setValue(this.asset.getSpeedFinal());
					break;
				case DEPTH:
					this.min = this.asset.getAltitudeMinimum();
					this.max = this.asset.getAltitudeMaximum();
					this.setMaximum(Math.abs(this.asset.getAltitudeMinimum()));
					this.setMinimum(Math.abs(this.asset.getAltitudeMaximum()));
					this.setSliderStep(10.0);
					this.setSpinnerStep(1.0);
					this.setFractionalDigits(1);
					this.setValue(Math.abs(this.asset.getAltitudeFinal()));
					break;
				case HEIGHT:
					this.min = this.asset.getAltitudeMinimum();
					this.max = this.asset.getAltitudeMaximum();
					this.setMinimum(this.asset.getAltitudeMinimum());
					this.setMaximum(this.asset.getAltitudeMaximum());
					this.setSliderStep(100.0);
					this.setSpinnerStep(10.0);
					this.setValue(this.asset.getAltitudeFinal());
					break;
				}	
			}
	}
	
	private enum TYPE {
		
		HEADING,
		SPEED,
		DEPTH,
		HEIGHT;
		
		TYPE() {}
				
	}

}

