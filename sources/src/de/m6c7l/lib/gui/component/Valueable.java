/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

public interface Valueable {

	public Object getValue();
	public void setValue(Object value);
	public boolean isValueValid();
	
}
