/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.tactical;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import de.m6c7l.playmate.gui.AppModel;
import de.m6c7l.playmate.main.Saveable;

public class TacticalView extends JPanel implements Saveable {

	private Tactical pane = null;
	
	public TacticalView(AppModel model) {
		
		this.setLayout(new BorderLayout());
		
		pane = new Tactical(model);
		this.add(pane, BorderLayout.CENTER);
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		
		setVisible(false);
		
	}
	
	public Tactical getTactical() {
		return pane;
	}

	public void free() {
		
		remove(pane);
		pane.free();
		pane = null;

	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		pane.setVisible(visible);
	}
	
	public String toString() {
		return "Tactical";
	}

	@Override
	public boolean save(File file) throws IOException {
		return pane.save(file);
	}
	
}
