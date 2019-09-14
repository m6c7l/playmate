/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

public class CheckBox extends JCheckBox implements Identifiable {
	
	private Object id = null;
	
	public CheckBox() {
		super();
	}

	public CheckBox(Action a) {
		super(a);
	}

	public CheckBox(Icon icon) {
		super(icon);
	}

	public CheckBox(String text) {
		super(text);
	}

	public CheckBox(Icon icon, boolean selected) {
		super(icon,selected);
	}

	public CheckBox(String text, boolean selected) {
		super(text,selected);
	}

	public CheckBox(String text, Icon icon) {
		super(text,icon);
	}

	public CheckBox(String text, Icon icon, boolean selected) {
		super(text,icon,selected);
	}
	
	public Object getID() {
		return this.id;
	}
	
	public void setID(Object id) {
		this.id = id;
	}

	public boolean isValueValid() {
		return true;
	}

	public Object getValue() {
		return this.isSelected();
	}

	public void setValue(Object value) {
		if (value!=null) {
			this.setSelected(value.toString().equals(Boolean.TRUE.toString())); 
		}
	}
		
}
