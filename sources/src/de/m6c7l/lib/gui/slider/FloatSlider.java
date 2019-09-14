/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.slider;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JSlider;

public class FloatSlider extends JSlider {
	
	private static int expand(double value, int digits) {
		return (int)(value*Math.pow(10,Math.abs(digits)));
	}

	private int factor = 1;
	private double min = 0;
	private double max = 0;
	
	private Point p1 = null;
	private Point p2 = null;
	
	public FloatSlider() {
		super();
		init();
	}
	
	public FloatSlider(int fractionalDigits) {
		super();
		this.factor = expand(1,fractionalDigits);
		init();
	}
	
	public FloatSlider(int orientation, int fractionalDigits) {
		super(orientation);
		this.factor = expand(1,fractionalDigits);
		init();
	}

	public FloatSlider(double min, double max, int fractionalDigits) {
		super(expand(min,fractionalDigits),expand(max,fractionalDigits));
		this.min = min;
		this.max = max;
		this.factor = expand(1,fractionalDigits);
		init();
	}

	public FloatSlider(double min, double max, double value, int fractionalDigits) {
		super(expand(min,fractionalDigits),expand(max,fractionalDigits),expand(value,fractionalDigits));
		this.min = min;
		this.max = max;
		this.factor = expand(1,fractionalDigits);
		init();
	}

	public FloatSlider(int orientation, double min, double max, double value, int fractionalDigits) {
		super(orientation,expand(min,fractionalDigits),expand(max,fractionalDigits),expand(value,fractionalDigits));
		this.min = min;
		this.max = max;
		this.factor = expand(1,fractionalDigits);
		init();
	}
	
	private void init() {
		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {	
				p1 = e.getPoint();
				p2 = e.getPoint();
			}
			public void mouseReleased(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseClicked(MouseEvent arg0) {}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				p1 = p2;
				p2 = e.getPoint();
			}
			public void mouseMoved(MouseEvent arg0) {}
		});
		p1 = new Point();
		p2 = new Point();
	}
	
	public boolean isDragIncreasing() {
		if (this.getOrientation()==JSlider.HORIZONTAL) {
			return ((p2.x>p1.x) && (!this.getInverted())) || ((p2.x<p1.x) && (this.getInverted()));
		} else if (this.getOrientation()==JSlider.VERTICAL) {
			return ((p2.y<p1.y) && (!this.getInverted())) || ((p2.y>p1.y) && (this.getInverted()));
		}
		return false;
	}
	
	public boolean isDragDecreasing() {
		if (this.getOrientation()==JSlider.HORIZONTAL) {
			return ((p2.x<p1.x) && (!this.getInverted())) || ((p2.x>p1.x) && (this.getInverted()));
		} else if (this.getOrientation()==JSlider.VERTICAL) {
			return ((p2.y>p1.y) && (!this.getInverted())) || ((p2.y<p1.y) && (this.getInverted()));
		}
		return false;
	}
	
	public int getFractionalDigits() {
		return (int)Math.log10(this.factor);
	}
	
	public void setFractionalDigits(int fractionalDigits) {
		int n = expand(1,fractionalDigits);
		int min = (int)Math.rint(this.getMinimumFloat()*n);
		int max = (int)Math.rint(this.getMaximumFloat()*n);
		int val = (int)Math.rint(this.getValueFloat()*n);
		if (min<super.getMaximum()) {
			super.setMinimum(min);
			super.setMaximum(max);
		} else {
			super.setMaximum(max);
			super.setMinimum(min);
		}
		super.setValue(val);
		this.factor = n;
	}
	
	public void setValueFloat(double value) {	
		super.setValue((int)Math.rint(value*this.factor));
	}
	
	public void setMinimumFloat(double value) {
		this.min = value;
		super.setMinimum((int)Math.rint(value*this.factor));
	}
	
	public void setMaximumFloat(double value) {
		this.max = value;
		super.setMaximum((int)Math.rint(value*this.factor));
	}
	
	public double getValueFloat() {
		return this.getValue()/(this.factor*1.0);
	}
	
	public double getMinimumFloat() {
		return this.min;
	}
	
	public double getMaximumFloat() {
		return this.max;
	}

}
