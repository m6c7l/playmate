/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.cep;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import de.m6c7l.playmate.main.Environment;
import de.m6c7l.playmate.main.Saveable;

public class CEPView extends JPanel implements Saveable {

	private CEP pane = null;
	private CEPHeader cepHeader = null;
	
	public CEPView(Environment model) {
		
		this.setLayout(new BorderLayout());
		
		pane = new CEP(model);
		this.add(pane, BorderLayout.CENTER);
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		
		cepHeader = new CEPHeader(pane);
		
		this.add(cepHeader,BorderLayout.NORTH);
		
		setVisible(false);
		
	}
	
	public CEP getCEP() {
		return pane;
	}

	public void free() {
		
		remove(pane);
		pane.free();
		pane = null;

		remove(cepHeader);
		cepHeader.free();
		cepHeader = null;
		
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		pane.setVisible(visible);
	}
	
	public String toString() {
		return "CEP";
	}

	@Override
	public boolean save(File file) throws IOException {
		return pane.save(file);
	}
	
}
