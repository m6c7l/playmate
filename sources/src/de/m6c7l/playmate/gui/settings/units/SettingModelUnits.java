/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.units;

import java.util.Hashtable;

import de.m6c7l.playmate.gui.settings.SettingsModel;
import de.m6c7l.playmate.main.ITEMS;

public class SettingModelUnits extends SettingsModel {

	public SettingModelUnits(Hashtable<Object,Object> values) {
		super(values);
	}

	public ITEMS.LENGTH getLength() {
		return ITEMS.LENGTH.get(this.getValue(ID_UNIT_LENGTH).toString());
	}

	public ITEMS.SPEED getSpeed() {
		return ITEMS.SPEED.get(this.getValue(ID_UNIT_SPEED).toString());
	}

	public ITEMS.HEIGHT getHeight() {
		return ITEMS.HEIGHT.get(this.getValue(ID_UNIT_HEIGHT).toString());
	}
	
	public boolean isValid() {
		return getLength()!=null && getSpeed()!=null;
	}
	
}
