/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import de.m6c7l.lib.gui.slider.SliderPane;

public class ChartHeaderMagnifier extends SliderPane implements MouseWheelListener {
	
	private Chart chart = null;
	
	public ChartHeaderMagnifier(Chart chart) {
		super(true,5,false,false);
		this.chart = chart;
		this.chart.addMouseWheelListener(this);
		this.setMaximum(chart.getMagnifierMaximum());
		this.setMinimum(chart.getMagnifierMinimum());
		this.setSpinnerStep(0.1);
		this.setSliderStep(1.0);
		this.setValue(chart.getMagnifier());
	}

	public void free() {
		this.chart.removeMouseWheelListener(this);
		this.chart = null;
	}
	
	@Override
	public void applyValue() {
		this.chart.setMagnifier(this.getValue());
	}

	@Override
	public double convertValueToSlider(String value) throws NumberFormatException {
		return new Double(value);
	}
	
	@Override
	public String convertSliderToValue(double value) {
		return toString(value) + "x";		
	}

	@Override
	public String getColumnsString() {
		if (this.chart!=null) {
			return (toString(chart.getMagnifierMaximum()) + "x");
		}
		return "";
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		super.setValue(this.chart.getMagnifier());	
	}

}
