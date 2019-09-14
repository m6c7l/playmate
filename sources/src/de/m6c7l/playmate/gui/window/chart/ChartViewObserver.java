/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.main.Environment;
import de.m6c7l.playmate.main.Saveable;

public class ChartViewObserver extends JPanel implements Saveable {
	
	private Environment world = null;
	private ArrayList<ChartView> views = null;
	private ChartView pane = null;
	
	public ChartViewObserver(Environment world, ArrayList<ChartView> views) {
		this.world = world;
		this.views = views;
		this.setLayout(new BorderLayout());
	}
	
	public ChartView getChart() {
		return pane;
	}
	
	public void setVisible(boolean visible) {
		if (visible) apply();
		super.setVisible(visible);
	}
	
	private void apply() {
		this.removeAll();
		if (pane!=null) pane.setVisible(false);
		pane = null;
		if (world.getObserver()!=null) {
			Position p = world.getObserver().getPosition();
			for (int i=0; i<views.size(); i++) {
				if (views.get(i).getChart().contains(p)) {
					pane = views.get(i);
					break;
				}
			}
			if (pane!=null) {
				pane.setVisible(true);
				this.add(pane,BorderLayout.CENTER);
				this.revalidate();
				//this.repaint();
			}
		}
	}
	
	public boolean save(File file) throws IOException {
		return pane.save(file);
	}
	
	public String toString() {
		return "Karte";
	}
	
}
