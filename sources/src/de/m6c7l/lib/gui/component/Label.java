/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

import java.util.ArrayList;

import javax.swing.JLabel;

public class Label extends JLabel implements Valueable {
	
	private ArrayList<FORMAT> format = null;
	private Object id = null;
	
	public Label() {
		this(FORMAT.EVERYTHING);
	}

	public Label(FORMAT format) {
		super();
		this.format = new ArrayList<FORMAT>();
		this.format.add(format);
		this.match();
	}
	
	public Object getID() {
		return this.id;
	}
	
	public void setID(Object id) {
		this.id = id;
	}

	public Object getValue() {
		return this.getText();
	}

	public void setValue(Object value) {
		if (value!=null) {
			this.setText(value.toString());			
		} else {
			this.setText("");
		}
	}
	
	public boolean isValueValid() {
		if (this.format==null) return false;
		boolean matching = false;
		for (int i=0; i<this.format.size(); i++) {
			if (this.format.get(i).match(this.getText())) {
				matching = true;
				break;
			}
		}
		return matching;
	}

	public void addFormat(FORMAT format) {
		this.format.add(format);
	}
	
	public FORMAT[] getFormat() {
		return (FORMAT[])this.format.toArray(new FORMAT[this.format.size()]);
	}
	
	public void setText(String s) {
		super.setText(s);
		this.match();
	}
	
	protected boolean match() {
		boolean matching = isValueValid();
		if (matching) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}			
		return matching;
	}

}