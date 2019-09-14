/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JPanel;

import de.m6c7l.lib.gui.dialog.SimpleDialog;

public class SettingsDialog extends SimpleDialog {

	private SettingsModel model = null;
	
	public SettingsDialog(String title, JPanel panel, SettingsModel model) {
		super(title, false, true);
		this.model = model;
		this.setBody(panel);
	}

	public SettingsDialog(Dialog owner, String title, JPanel panel, SettingsModel model) {
		super(owner, title, false, true);
		this.model = model;
		this.setBody(panel);
	}

	public SettingsDialog(Frame owner, String title, JPanel panel, SettingsModel model) {
		super(owner, title, false, true);
		this.setBody(panel);
	}

	public SettingsDialog(Window owner, String title, JPanel panel, SettingsModel model) {
		super(owner, title, false, true);
		this.model = model;
		this.setBody(panel);
	}

	public SettingsDialog(Container owner, String title, JPanel panel, SettingsModel model) {
		super(owner, title, false, true);
		this.model = model;
		this.setBody(panel);
	}
	
	public SettingsModel getModel() {
		return this.model;
	}
	
}
