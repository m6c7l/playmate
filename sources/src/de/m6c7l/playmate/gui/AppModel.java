/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.lib.util.sound.Sound;
import de.m6c7l.playmate.gui.settings.units.SettingModelUnits;
import de.m6c7l.playmate.gui.side.scenario.TreeModel;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.gui.window.chart.ChartView;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.World;
import de.m6c7l.playmate.main.io.XMLAsset;
import de.m6c7l.playmate.main.io.XMLAssetReader;
import de.m6c7l.playmate.main.io.XMLChart;
import de.m6c7l.playmate.main.io.XMLChartReader;
import de.m6c7l.playmate.main.io.XMLSettingsReader;

public class AppModel {

	private ArrayList<XMLChart> xmlcharts = null;
	private ArrayList<XMLAsset> xmlassets = null;
	
	private ArrayList<Chart> charts = null;
	
	private World world = null;
	private TreeModel tree = null;
	
	private SettingModelUnits preferences = null;
	private Sound sound = null;
	
	public AppModel(World world, XMLChartReader cr, XMLAssetReader ur, XMLSettingsReader pr, Sound s) {
		this(world,cr.getValues(),ur.getValues(),new SettingModelUnits(pr.getValues()),s);
	}

	private AppModel(World w, ArrayList<XMLChart> c, ArrayList<XMLAsset> u, SettingModelUnits p, Sound s) {
		
		this.world = w;
		this.tree = new TreeModel();
		this.preferences = p;
		
		this.xmlassets = u;
		this.xmlcharts = c;
		
		this.sound = s;

		this.charts = new ArrayList<Chart>();
			
		for (int i=0; i<this.xmlassets.size(); i++) {
			this.getTree().addXMLAsset(xmlassets.get(i));	
		}

	}

	public AppModel reset() throws IOException, ParserConfigurationException, SAXException {
		free();
		return new AppModel(new World(),xmlcharts,xmlassets,preferences,sound);
	}
	
	public void free() throws IOException, ParserConfigurationException, SAXException {
		this.tree.free();
		this.world.free();
		this.charts.clear();
	}
	
	/*
	 * view
	 */
	
	public static ChartView[] createChartViews(AppModel model) throws Exception {
		ChartView[] charts = new ChartView[model.xmlcharts.size()];
		for (int i=0; i<charts.length; i++) {			
			charts[i] = new ChartView(model.xmlcharts.get(i));
		}
		return charts;
	}
	
	/*
	 * chart
	 */
	
	public Chart getChart(Position position) {
		for (int i=0; i<charts.size(); i++) {
			if (charts.get(i).contains(position))
				return charts.get(i);
		}
		return null;
	}
	
	/*
	 * model
	 */
	
	public TreeModel getTree() {
		return this.tree;
	}
	
	public SettingModelUnits getSetupDisplay() {
		return this.preferences;
	}
	
	public World getWorld() {
		return this.world;
	}

	public Sound getSound() {
		return sound;
	}
	
	/*
	 * array
	 */

	public boolean removeAsset(Asset asset) {
		if (world.remove(asset)) {
			for (int i=0; i<charts.size(); i++) {
				charts.get(i).remove(asset);
			}
			return true;
		}
		return false;
	}

	public boolean addAsset(Asset asset) {
		if (world.put(asset)) {
			for (int i=0; i<charts.size(); i++) {
				charts.get(i).put(asset);
			}
			return true;
		}
		return false;
	}
	
	public boolean addChart(Chart chart) {
		if (world.put(chart)) {
			this.charts.add(chart);		
			for (int i=0; i<world.getAssetCount(); i++) {
				chart.put(world.getAsset(i));
			}
			return true;
		}
		return false;
	}
	
}
