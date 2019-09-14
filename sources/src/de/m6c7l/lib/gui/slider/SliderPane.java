/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.slider;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.component.FORMAT;
import de.m6c7l.lib.gui.component.TextField;
import de.m6c7l.lib.gui.layout.GBC;

import javax.swing.event.ChangeEvent;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

public abstract class SliderPane extends JPanel {
	
	private TextField textField = null;
	private JButton btnUp = null;
	private JButton btnDown = null;
	private FloatSlider slider = null;
	
	private double sliderStep = 1.0;
	private double spinnerStep = 1.0;
	
	private boolean alwaysPick = false;
	private boolean refresh = false;
	
	private static int count(double value) {
		double v = value;
		int i = 0;
		for (i = 0; v != Math.round(v); i++) v = v*10;
		return i;
	}
	
	public SliderPane(final boolean alwaysPick, final int horizontalGap, final boolean editable, final boolean flipable) {

		this.alwaysPick = alwaysPick;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0};
		
		setLayout(gridBagLayout);

		textField = new TextField(FORMAT.EVERYTHING,false);
		textField.setFont(textField.getFont().deriveFont(Font.BOLD));
		
		textField.setEditable(editable);
		if (editable) {
			textField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {}
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER) {
						String s = textField.getText();
						try {
							double x = convertValueToSlider(s);
							setValue(x);
							applyValue();
						} catch (Exception ex) {
							textField.setValid(false);
						}
					}
				}
			});			
		}
		
		GBC gbc_tf = new GBC(0).setInsets(0,0,0,horizontalGap).setFill(GBC.FILL.VERTICAL);
		add(textField, gbc_tf);
	
		btnUp = new JButton("+") {
			public boolean hasFocus() {
				return super.hasFocus() && super.isEnabled();
			}
		};
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int a = (int)Math.pow(10,slider.getFractionalDigits());
				double x = ((slider.getValueFloat() + spinnerStep)*a)/a;
				if (x<=slider.getMaximumFloat()) {
					setValue(x);
				} else {
					if (!flipable) {
						setValue(slider.getMaximumFloat());						
					} else {
						setValue(slider.getMinimumFloat());												
					}
				}
				applyValue();
			}
		});
		GBC gbc_btnUp = new GBC(2).setInsets(0,0,0,horizontalGap).setFill(GBC.FILL.VERTICAL);
		add(btnUp, gbc_btnUp);

		btnDown = new JButton("-") {
			public boolean hasFocus() {
				return super.hasFocus() && super.isEnabled();
			}
		};
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int a = (int)Math.pow(10,slider.getFractionalDigits());
				double x = ((slider.getValueFloat() - spinnerStep)*a)/a;
				if (x>=slider.getMinimumFloat()) {
					setValue(x);
				} else {
					if (!flipable) {
						setValue(slider.getMinimumFloat());
					} else {
						setValue(slider.getMaximumFloat());
					}
				}
				applyValue();
			}
		});
		GBC gbc_btnDown = new GBC(1).setInsets(0,0,0,horizontalGap).setFill(GBC.FILL.VERTICAL);
		add(btnDown, gbc_btnDown);
		
		// ++++
		Dimension dd = btnDown.getPreferredSize();
		Dimension du = btnUp.getPreferredSize();
		
		btnDown.setPreferredSize(new Dimension(Math.max(dd.width,du.width),dd.height));
		btnDown.setMinimumSize(btnDown.getPreferredSize());
		
		btnUp.setPreferredSize(new Dimension(Math.max(dd.width,du.width),du.height));
		btnUp.setMinimumSize(btnUp.getPreferredSize());
		// ++++
		
		slider = new FloatSlider();
		GBC gbc_slider = new GBC(3).setFill(GBC.FILL.BOTH);
		add(slider, gbc_slider);
		
		new BoundedChangeListener(this);
		
	}
	
	public void setEnabled(boolean enabled) {
		if (slider.isEnabled()==enabled) return;
		this.slider.setEnabled(enabled);
		this.btnDown.setEnabled(enabled);
		this.btnUp.setEnabled(enabled);
		this.textField.setEnabled(enabled);
		refresh = false;
	}
	
	public final void setValue(double value) {
		slider.setValueFloat(value);	
		update(this.getValue());
	}
	
	public final void setMinimum(double value) {
		slider.setMinimumFloat(value);
	}
	
	public final void setMaximum(double value) {
		slider.setMaximumFloat(value);
	}
	
	public final void setSliderStep(double step) {
		this.sliderStep = step;
		slider.setFractionalDigits(Math.max(count(this.sliderStep),count(this.spinnerStep)));
	}
	
	public final void setSpinnerStep(double step) {
		this.spinnerStep = step;
		slider.setFractionalDigits(Math.max(count(this.sliderStep),count(this.spinnerStep)));
	}
	
	protected final void setFractionalDigits(int digits) {
		slider.setFractionalDigits(digits);
	}
	
	public final double getSliderStep() {
		return sliderStep;
	}

	public double getValue() {
		return this.slider.getValueFloat();
	}

	public abstract double convertValueToSlider(String value) throws NumberFormatException;
	public abstract String convertSliderToValue(double value);
	
	public abstract String getColumnsString();
	public abstract void applyValue();
	
	public String toString(double value) {
		return toString(value, 1, slider.getFractionalDigits());
	}
	
	public static String toString(double value, int digitsAfterDecimal) {
		return toString(value, 1, digitsAfterDecimal);
	}
	
	public static String toString(double value, int digitsBeforeDecimal, int digitsAfterDecimal) {
		String s = "";
		int pre = 1;
		if (Math.abs(value)>0) pre = ((int)Math.log10(Math.abs(value)))+1;
		for (int i=0; i<pre-digitsBeforeDecimal; i++) s = s + "#";
		for (int j=0; j<digitsBeforeDecimal; j++) s = s + "0";
		if (digitsAfterDecimal>0) {
			s = s + ".";
			for (int i=0; i<digitsAfterDecimal; i++) s = s + "0";
		}
		return new DecimalFormat(s).format(value);			
	}
	
	private void update(double value) {

		String s = convertSliderToValue(value);
		if (!s.equals(textField.getText())) {
			textField.setPrototype(getColumnsString());
			textField.setText(s);
			textField.setValid(true);
		}

	}
	
	private static class BoundedChangeListener implements ChangeListener {
		
		private SliderPane pane = null;
		
		private int last = 0;
		private int current = 0;

		public BoundedChangeListener(SliderPane pane) {
			this.pane = pane;
			this.pane.slider.addChangeListener(this);
			this.last = this.pane.slider.getValue();
			this.current = this.pane.slider.getValue();
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {

			int factor = (int)(Math.pow(10,pane.slider.getFractionalDigits()));
			int step = (int)(factor*pane.getSliderStep());

			if (this.current <= pane.slider.getMinimum()) {
				this.current = pane.slider.getMinimum();

			} else if (this.current >= pane.slider.getMaximum()) {
				this.current = pane.slider.getMaximum();
			}

			this.current = pane.slider.getValue();

			if (!pane.slider.getValueIsAdjusting()) { // nachdem gezogen oder geklickt worden ist

				this.last = this.current;

				pane.slider.setValue(this.current);
				pane.update(this.current / factor);
				if (pane.refresh) pane.applyValue();
				pane.refresh = false;

			} else { // wenn gezogen wird

				if (this.last > this.current) {
					this.current = this.current / step * step;

				} else if (this.last < this.current) {
					this.current = this.current / step * step + step;
					this.last = this.current;
				}

				pane.slider.setValue(this.current);
				pane.update(this.current / factor);
				if (pane.alwaysPick) pane.applyValue();
				pane.refresh = true;

			}

		}
		
	}
	
}
