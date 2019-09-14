/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import de.m6c7l.lib.gui.TOOLBOX;

public class TextField extends JTextField implements Identifiable {
	
	private static final Color focusBGColor = new Color(255,255,210);
	private static final Color matchBGColor = new Color(255,210,210);
	
	private Color defaultBGColor = null;
	
	private ArrayList<FORMAT> format = null;
	private boolean mandatory = false;
	
	private boolean focus = false;
	
	private Object id = null;
	
	public TextField() {
		this(FORMAT.EVERYTHING,false);
	}

	public TextField(FORMAT format) {
		this(format,false);
	}

	public TextField(FORMAT format, boolean mandatory) {
		super();
		this.defaultBGColor = getBackground();
		this.format = new ArrayList<FORMAT>();
		this.format.add(format);
		this.mandatory = mandatory;
		this.initialize();
		this.match();
	}

	public Object getID() {
		return this.id;
	}
	
	public void setID(Object id) {
		this.id = id;
	}
	
	public Color getDefaultBackground() {
		return this.defaultBGColor;
	}

	public void addFormat(FORMAT format) {
		this.format.add(format);
	}
	
	public boolean isValueValid() {
		if (this.format==null) return false;
		boolean matching = ((!this.mandatory) && (this.getText().length()==0)) || (!this.isEnabled());
		if (!matching) {
			for (int i=0; i<this.format.size(); i++) {
				if (this.format.get(i).match(this.getText())) {
					if ((!this.mandatory) || (this.getText().length()!=0)) {
						matching = true;
						break;
					}
				}
			}
		}
		return matching;
	}
	
	protected boolean match() {
		boolean matching = isValueValid();
		setValid(matching);
		return matching;
	}
	
	public void setValid(boolean valid) {
		if (valid) {
			if (focus) {
				setBackground(focusBGColor);				
			} else {
				setBackground(defaultBGColor);
			}
		} else {
			setBackground(matchBGColor);
		}		
	}
	
	private void initialize() {
		this.addCaretListener(new CaretListener() {
			public void caretUpdate(final CaretEvent e) {
				match();
			}
		});
		this.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				focus = true;
				match();
			}
			public void focusLost(FocusEvent e) {
				if (match()) {
					setBackground(defaultBGColor);
				}
				focus = false;
			}
		});		
	}

	public FORMAT[] getFormat() {
		return (FORMAT[])this.format.toArray(new FORMAT[this.format.size()]);
	}

	public Object getValue() {
		return this.getText();
	}
	
	public void setPrototype(String prototype) {
		this.setColumns(
				(int)(TOOLBOX.getSize(prototype,getFont()).width+TOOLBOX.getSize(1,getFont()).width*1.5)/
				TOOLBOX.getSize(1,getFont()).width);
	}

	public void setValue(Object value) {
		if (value!=null) {
			this.setText(value.toString());			
		} else {
			this.setText("");
		}
	}
	
	public void setText(String s) {
		super.setText(s);
		this.match();
	}

	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width+2,super.getPreferredSize().height+1);
	}

	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}
	
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}
	
}