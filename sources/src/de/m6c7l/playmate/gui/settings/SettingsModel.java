/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings;

import java.util.Hashtable;

public abstract class SettingsModel {

	// environment
	public static final String ID_VALUE_ACOUSTIC_RANGE	= "environment.value_acoustic_range";
	public static final String ID_VALUE_VISUAL_RANGE	= "environment.value_visual_range";
	public static final String ID_VALUE_CURRENT_SET		= "environment.value_current_set";
	public static final String ID_VALUE_CURRENT_DRIFT	= "environment.value_current_drift";	
	
	// units of measure
	public static final String ID_UNIT_LENGTH			= "unit.value_length";
	public static final String ID_UNIT_SPEED			= "unit.value_speed";
	public static final String ID_UNIT_HEIGHT			= "unit.value_height";
	
	// assets
	public static final String ID_VALUE_VECTOR			= "asset.value_motion_vector";
	public static final String ID_VALUE_TRACE			= "asset.value_motion_trace";
	public static final String ID_SHOW_VECTORS			= "asset.show_motion_vectors";
	public static final String ID_SHOW_TRACES			= "asset.show_motion_traces";
	
	// todo: advanced display
	public static final String ID_SHOW_DOT				= "chart.show_distance_off_track";
	public static final String ID_SHOW_CPA				= "chart.show_closest_point_of_approach";
	public static final String ID_SHOW_LI				= "chart.show_look_interval";
	public static final String ID_SHOW_RSA				= "chart.show_resultant_speed_across";
	public static final String ID_SHOW_RSL				= "chart.show_resultant_speed_along";
	public static final String ID_SHOW_1936				= "chart.show_method_1936";
	public static final String ID_SHOW_GDC				= "chart.show_go_deep_circle";
	public static final String ID_OPTION_GDC_REDUCED	= "chart.option_go_deep_circle_reduced";
	public static final String ID_OPTION_GDC_EXTENDED	= "chart.option_go_deep_circle_extended";
	
	private Hashtable<Object,Object> values = null;
	
	public SettingsModel(Hashtable<Object,Object> values) {
		this.values = values;
	}

	public Hashtable<Object,Object> getValues() {
		return this.values;
	}
	
	public void setValue(Object id, Object value) {
		this.values.put(id,value);
	}	
	
	public Object getValue(Object id) {
		if (this.values.get(id)==null) return "";
		return this.values.get(id);
	}

	public int size() {
		return this.values.size();
	}
	
	public abstract boolean isValid();
	
}
