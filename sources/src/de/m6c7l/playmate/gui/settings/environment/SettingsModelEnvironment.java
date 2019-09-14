/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.environment;

import java.util.Hashtable;

import de.m6c7l.playmate.gui.settings.SettingsModel;

public class SettingsModelEnvironment extends SettingsModel {
	
	public SettingsModelEnvironment(Hashtable<Object,Object> values) {
		super(values);
	}
	
	public Integer getAcousticRange() {
		try { return new Integer(this.getValue(ID_VALUE_ACOUSTIC_RANGE).toString());
		} catch (NumberFormatException e1) { }
		return null;
	}

	public Integer getVisualRange() {
		try { return new Integer(this.getValue(ID_VALUE_VISUAL_RANGE).toString());
		} catch (NumberFormatException e1) { }
		return null;
	}
	
	public Integer getCurrentSet() {
		try { return new Integer(this.getValue(ID_VALUE_CURRENT_SET).toString());
		} catch (NumberFormatException e1) { }
		return null;
	}

	public Double getCurrentDrift() {
		try { return new Double(this.getValue(ID_VALUE_CURRENT_DRIFT).toString());
		} catch (NumberFormatException e1) { }
		return null;
	}
	
	public boolean isValid() {
		return getVisualRange()!=null && getAcousticRange()!=null && getCurrentSet()!=null && getCurrentDrift()!=null;
	}
	
}
