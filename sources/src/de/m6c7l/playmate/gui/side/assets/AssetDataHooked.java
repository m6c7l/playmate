/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.SystemColor;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.playmate.main.World;

public class AssetDataHooked extends JPanel {
	
	private AssetChooser chooser = null;

	private AssetDataHookedTable data = null;
	
	public AssetDataHooked(World model) {
		
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		chooser = new AssetHookChooser(model);
		chooser.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.add(chooser,BorderLayout.NORTH);
		
		JPanel dataPanel = new JPanel();
		
		GridBagLayout gbl = new GridBagLayout();
		
		double[] rweights = new double[3];
		for (int i=0; i<rweights.length; i++) {
			if (i<rweights.length-1) rweights[i] = 0.0; else rweights[i] = 1.0;
		}
		double[] cweights = new double[1];
		for (int i=0; i<cweights.length; i++) {
			cweights[i] = 1.0;
		}
		
		gbl.rowWeights = rweights;
		gbl.columnWeights = cweights;
		
		dataPanel.setLayout(gbl);
		
		data = new AssetDataHookedTable(model);
		GBC gbcData = new GBC(0,0).setAnchor(GBC.FIRST_LINE_START).setFill(GBC.HORIZONTAL);
		dataPanel.add(data,gbcData);
		
		JPanel temp = new JPanel();
		temp.setBackground(SystemColor.text);
		temp.setLayout(new BorderLayout());
		temp.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		temp.add(dataPanel,BorderLayout.NORTH);
		
		this.add(temp,BorderLayout.CENTER);
		
	}

	public Dimension getPreferredSize() {
		return new Dimension();
	}	
	
	public void free() {
		
		data.free();
		data = null;

		chooser.free();
		chooser = null;

	}
	
	public AssetChooser getChooser() {
		return this.chooser;
	}

}
