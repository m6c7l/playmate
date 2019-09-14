/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.m6c7l.playmate.gui.side.assets.AssetChooser;
import de.m6c7l.playmate.gui.side.assets.AssetDataHooked;
import de.m6c7l.playmate.gui.side.assets.AssetDataSelected;
import de.m6c7l.playmate.main.World;

public class SidePane extends JPanel {

	private AssetDataSelected assetDataSelected = null;
	private AssetDataHooked assetDataHooked = null;
	
	public SidePane(World world) {

		this.setLayout(new BorderLayout());
		
		assetDataSelected  = new AssetDataSelected(world);
		assetDataHooked  = new AssetDataHooked(world);
		
		JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				
		panel.setTopComponent(assetDataHooked);
		panel.setBottomComponent(assetDataSelected);
		
		panel.setDividerSize(5);
		panel.setContinuousLayout(true);
		panel.setResizeWeight(0.75);
				
		this.add(panel,BorderLayout.CENTER);

	}

	public AssetChooser getChooserSelectAsset() {
		return this.assetDataSelected.getChooser();
	}
	
	public AssetChooser getChooserHookAsset() {
		return this.assetDataHooked.getChooser();
	}
	
	public void free() {
		assetDataSelected.free();
		assetDataHooked.free();
		this.remove(assetDataSelected);
		this.remove(assetDataHooked);
		assetDataSelected = null;
		assetDataHooked = null;
	}


}
