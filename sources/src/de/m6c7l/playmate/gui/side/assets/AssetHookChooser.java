/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.World;

public class AssetHookChooser extends AssetChooser implements ChangeListener {

	public AssetHookChooser(World model) {
		super(model);
		this.world.addChangeListener(this);
	}
	
	public void free() {
		this.world.removeChangeListener(this);
		super.free();
	}

	public void setIndex(boolean next, int attempts) {
		if ((attempts>=world.getAssetCount())) return;
		if (next) {
			index++;
		} else if (!next) {
			index--;
		}
		index = (index+world.getAssetCount()) % world.getAssetCount();		
		if (world.getAsset(index).isHookable()) {
			world.setHooked(world.getAsset(index));
			apply();
		} else {
			setIndex(next,attempts+1);
		}
	}
	
	public void apply() {
		Asset hooked = world.getHooked();
		if (hooked!=null) {
			Asset observer = world.getObserver();
			String remark = "";
			if (observer!=null) {
				if (!observer.isAudible(hooked)) remark = ""; else remark = "";				
			}
			lblView.setText(remark + hooked.getID().toString() + remark);	
			index = world.indexOf(hooked);
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
