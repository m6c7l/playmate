/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.assets;

import java.util.Hashtable;

import de.m6c7l.playmate.gui.settings.SettingsModel;
import de.m6c7l.playmate.main.ITEMS;

public class SettingsModelAssets extends SettingsModel {
	
	public SettingsModelAssets(Hashtable<Object,Object> values) {
		super(values);
	}
	
	public ITEMS.MOTION_VECTOR getValueVector() {
		return ITEMS.MOTION_VECTOR.get(this.getValue(ID_VALUE_VECTOR).toString());
	}
	
	public ITEMS.MOTION_VECTOR getValueTrace() {
		return ITEMS.MOTION_VECTOR.get(this.getValue(ID_VALUE_TRACE).toString());
	}
	
	public Boolean getShowVectors() {
		return Boolean.valueOf(this.getValue(ID_SHOW_VECTORS).toString());
	}
	
	public Boolean getShowTraces() {
		return Boolean.valueOf(this.getValue(ID_SHOW_TRACES).toString());
	}
	
	public boolean isValid() {
		return getValueTrace()!=null && getValueVector()!=null && getShowTraces()!=null && getShowVectors()!=null;
	}
	
}
