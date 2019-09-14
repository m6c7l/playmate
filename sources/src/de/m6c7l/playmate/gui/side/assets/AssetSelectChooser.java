/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.World;

public class AssetSelectChooser extends AssetChooser implements ChangeListener {

	public AssetSelectChooser(World model) {
		super(model);
		this.world.addChangeListener(this);
	}

	public void free() {
		this.world.removeChangeListener(this);
		super.free();
	}
	
	public void setIndex(boolean next, int attempts) {
		if ((attempts>=world.getAssetCount())) return;
		if (index==-1) { 
			index = world.indexOf(world.getObserver());
			if (index==-1) index = 0;
		} else if (next) {
			index++;
		} else if (!next) {
			index--;
		}
		index = (index+world.getAssetCount()) % world.getAssetCount();		
		if (world.getAsset(index).isSelectable()) {
			world.setSelected(world.getAsset(index));
			apply();
		} else {
			setIndex(next,attempts+1);
		}
	}
	
	public void apply() {
		Asset selected = world.getSelected();
		if (selected!=null) {			
			Asset observer = world.getObserver();
			if (!selected.equals(observer)) {
				String remark = "";
				if (observer!=null) {
					if (!observer.isAudible(selected)) remark = ""; else remark = "";
				}
				lblView.setText(remark + selected.getID().toString() + remark);				
			} else {
				lblView.setText("Own ship");
			}
			index = world.indexOf(selected);
		} else {
			lblView.setText(" ");	
			index = -1;
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		apply();
	}
	
}
