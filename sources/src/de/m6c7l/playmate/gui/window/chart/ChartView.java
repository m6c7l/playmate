/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import de.m6c7l.playmate.main.Saveable;
import de.m6c7l.playmate.main.io.XMLChart;
import de.m6c7l.playmate.main.utility.ChartBuilder;

public class ChartView extends JPanel implements Saveable {

	private String name = null;
	
	private Chart chart = null;
	private ChartHeader chartHeader = null;
	
	public ChartView(XMLChart xml) throws Exception {
		
		this.setLayout(new BorderLayout());

		name = xml.getName();
		chart = new ChartBuilder(xml).create();
		this.add(chart, BorderLayout.CENTER);
		
		chartHeader = new ChartHeader(chart);
		this.add(chartHeader,BorderLayout.NORTH);
		
		setVisible(false);

	}

	public void free() {
		
		remove(chart);
		chart.free();
		chart = null;

		remove(chartHeader);
		chartHeader.free();
		chartHeader = null;
		
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		chart.setVisible(visible);
	}
	
	public Chart getChart() {
		return chart;
	}
	
	public String toString() {
		return name;
	}

	@Override
	public boolean save(File file) throws IOException {
		return chart.save(file);
	}
	
}
