/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

public interface Hookable extends Selectable {

	public void setHooked(boolean hooked);
	public boolean isHooked();
	public boolean isHookable();
	
}
