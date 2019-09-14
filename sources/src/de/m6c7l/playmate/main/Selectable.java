/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

public interface Selectable extends Locateable {

	public void setSelected(boolean selected);
	public boolean isSelected();
	public boolean isSelectable();
	public Object getID();

}
