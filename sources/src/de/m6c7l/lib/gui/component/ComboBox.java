/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

@SuppressWarnings("rawtypes")
public class ComboBox extends JComboBox implements Identifiable {
	
	private ComboBoxTextField editor = null;
	private Object id = null;
	
	public ComboBox() {
		this(FORMAT.EVERYTHING,false);
	}

	public ComboBox(FORMAT format) {
		this(format,false);
	}

	public ComboBox(FORMAT format, boolean mandatory) {
		this(format,mandatory,true);
	}

	public ComboBox(boolean mandatory, boolean editable) {
		this(FORMAT.EVERYTHING,mandatory,editable);
	}
	
	public ComboBox(FORMAT format, boolean mandatory, boolean editable) {
		super();
		this.setFont(this.getFont().deriveFont(Font.PLAIN));
		this.editor = new ComboBoxTextField(this,format,mandatory);
		this.setEditor(editor);
		this.setEditable(editable);
		this.initialize();
	}
	
	public Dimension getPreferredSize() {
		Dimension dE = this.editor.getPreferredSize();
		if (this.getItemCount()==0) {
			return new Dimension(
					dE.width+1+dE.height,
					dE.height);
		}
		return new Dimension(
				super.getPreferredSize().width,
				dE.height);
	}

	public void setEditor(ComboBoxTextField editor) {
		super.setEditor(editor);
	}
	
	public ComboBoxTextField getEditor() {
		return this.editor;
	}
	
	public Object getID() {
		return this.id;
	}
	
	public void setID(Object id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public void addItem(Object[] objects) {
		Object old = getSelectedItem();
		for (int i=0; i<objects.length; i++) {
			this.addItem(objects[i]);
		}
		setSelectedItem(old);
		this.revalidate();
	}

	public void addFormat(FORMAT format) {
		this.editor.addFormat(format);
	}

	public boolean isValueValid() {
		return this.editor.isValueValid();
	}
	
	protected boolean match() {
		if (this.editor==null) return false;
		return this.editor.match();			
	}

	private void initialize() {
		this.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					match();
				}
			}
		});
	}
	
	public JButton getButton() {
		return getButtonSubComponent(this);
	}

	private static JButton getButtonSubComponent(Container container) {
		if (container instanceof JButton) {
			return (JButton) container;
		} else {
			Component[] components = container.getComponents();
			for (Component component : components) {
				if (component instanceof Container) {
					return getButtonSubComponent((Container) component);
				}
			}
		}
		return null;
	}
	
	public FORMAT[] getFormat() {
		return this.editor.getFormat();
	}

	public void setEditable(boolean editable) {
		super.setEditable(true);
		this.editor.setEditable(editable);
	}
	
	public Object getValue() {
		if (this.getSelectedIndex()>-1) {
			return this.getSelectedItem();
		} else {
			return this.editor.getText();
		}
	}

	public void setValue(Object value) {
		if (value!=null) {
			for (int i=0; i<this.getItemCount(); i++) {
				if (this.getItemAt(i).toString().equals(value.toString())) {
					super.setSelectedIndex(i);
					return;
				}
			}			
		}
	}
	
	public void setSelectedItem(Object item) {
		super.setSelectedItem(item);
		this.match();
	}

	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		this.match();
	}

	@SuppressWarnings("unchecked")
	public void setModel(ComboBoxModel model) {
		super.setModel(model);
		this.setSelectedItem(null);
	}
	
	public void setColumns(int columns) {
		this.editor.setColumns(columns);
	}
	
	public void setPrototype(String prototype) {
		this.editor.setPrototype(prototype);
	}
	
	private static class ComboBoxTextField extends TextField implements ComboBoxEditor {
		
		private ComboBox parent = null;
		
		public ComboBoxTextField(ComboBox parent, FORMAT format, boolean mandatory) {
			super(format,mandatory);
			this.parent = parent;
		}

		public Component getEditorComponent() {
			return this;
		}

		public Object getItem() {
			return this.parent.getSelectedItem();
		}
		
		public void setItem(Object anObject) {
			this.parent.setSelectedItem(anObject);
			this.setText(anObject);
		}

		public void setText(Object anObject) {
			super.setText(anObject == null ? null : anObject.toString());
		}

	}
	
}
